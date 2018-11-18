package jp.kusumotolab.kgenprog.project.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import com.google.common.base.Functions;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.build.BinaryStore;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * @author shinsuke
 */
class TestThread extends Thread {

  private final IRuntime jacocoRuntime;
  private final Instrumenter jacocoInstrumenter;
  private final RuntimeData jacocoRuntimeData;
  private TestResults testResults; // used for return value in multi thread
  private BuildResults buildResults;

  // private final GeneratedSourceCode generatedSourceCode;
  private final TargetProject targetProject;
  private final List<String> executionTestNames;

  public TestThread(final BuildResults buildResults, final TargetProject targetProject,
      final List<String> executionTestNames) {

    this.jacocoRuntime = new LoggerRuntime();
    this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
    this.jacocoRuntimeData = new RuntimeData();

    this.buildResults = buildResults;
    this.targetProject = targetProject;
    this.executionTestNames = executionTestNames;
  }

  // Result extraction point for multi thread
  public TestResults getTestResults() {
    return this.testResults;
  }

  /**
   * JaCoCo + JUnitの実行． sourceClassesで指定したソースをJaCoCoでinstrumentして，JUnitを実行する．
   */
  public void run() {
    // 初期処理（プロジェクトのビルドと返り値の生成）

    // XXXXXXXXXXXXXXXXXXX TODO
    // final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    // buildResults = projectBuilder.build(generatedSourceCode);
    testResults = new TestResults();
    testResults.setBuildResults(buildResults); // FLメトリクス算出のためにtestResultsにbuildResultsを登録しておく．

    // ビルド失敗時は即座に諦める
    if (buildResults.isBuildFailed) {
      testResults = EmptyTestResults.instance;
      return;
    }

    final List<FullyQualifiedName> productFQNs = getProductFQNs();
    final List<FullyQualifiedName> executionTestFQNs = getExecutionTestFQNs();

    final List<ClassPath> classPaths = targetProject.getClassPaths();
    final URL[] classpathUrls = convertClasspathsToURLs(classPaths);
    final MemoryClassLoader classLoader = new MemoryClassLoader(classpathUrls);

    try {
      addAllDefinitions(classLoader, productFQNs);
      final List<Class<?>> testClasses = loadAllClasses(classLoader, executionTestFQNs);

      final JUnitCore junitCore = new JUnitCore();
      final CoverageMeasurementListener listener =
          new CoverageMeasurementListener(productFQNs, testResults);
      junitCore.addListener(listener);
      junitCore.run(testClasses.toArray(new Class<?>[testClasses.size()]));

    } catch (final ClassNotFoundException e) {
      // クラスロードに失敗．FQNの指定ミスの可能性が大
      this.testResults = EmptyTestResults.instance;
      return;
    } catch (Exception e) {
      // TODO
      // Should handle safely
      // ひとまず本クラスをThreadで包むためにRuntimeExceptionでエラーを吐く．
      throw new RuntimeException(e);
    }
  }

  /**
   * MemoryClassLoaderに対して全てのバイトコード定義を追加する（ロードはせず）．<br>
   * プロダクト系ソースコードのみJaCoCoインストルメントを適用する．
   * 
   * @param memoryClassLoader
   * @param fqns
   * @param isInstrument
   * @throws IOException
   */
  private void addAllDefinitions(final MemoryClassLoader memoryClassLoader,
      final List<FullyQualifiedName> fqns) throws IOException {
    final BinaryStore binaryStore = buildResults.getBinaryStore();
    for (final JavaBinaryObject jmo : binaryStore.getAll()) {
      final FullyQualifiedName fqn = jmo.getFqn();
      final byte[] rawBytecode = jmo.getByteCode();
      final byte[] bytecode = jmo.isTest() ? rawBytecode : instrumentBytecode(rawBytecode);
      memoryClassLoader.addDefinition(fqn, bytecode);
    }
  }

  private byte[] instrumentBytecode(final byte[] bytecode) throws IOException {
    return jacocoInstrumenter.instrument(bytecode, "");
  }

  private List<FullyQualifiedName> getProductFQNs() {
    return getFQNs(targetProject.getProductSourcePaths());
  }

  private List<FullyQualifiedName> getTestFQNs() {
    return getFQNs(targetProject.getTestSourcePaths());
  }

  private List<FullyQualifiedName> getExecutionTestFQNs() {
    final List<FullyQualifiedName> executionTests = convertExecutionTestNameToFQN();
    // 実行テストの指定がない場合は全テストを実行する
    if (executionTests.isEmpty()) {
      return getTestFQNs();
    }
    return executionTests;
  }

  private List<FullyQualifiedName> convertExecutionTestNameToFQN() {
    return executionTestNames.stream()
        .map(TestFullyQualifiedName::new)
        .collect(Collectors.toList());
  }

  private List<FullyQualifiedName> getFQNs(final List<? extends SourcePath> sourcesPaths) {
    final BinaryStore binStore = buildResults.getBinaryStore();
    return sourcesPaths.stream()
        .map(source -> binStore.get(source))
        .flatMap(Collection::stream)
        .map(jmo -> jmo.getFqn())
        .collect(Collectors.toList());
  }

  private URL[] convertClasspathsToURLs(final List<ClassPath> classpaths) {
    return classpaths.stream()
        .map(cp -> cp.path.toUri())
        .map(uri -> toURL(uri))
        .toArray(URL[]::new);
  }

  /**
   * To avoid Malform uri in lambda expression
   *
   * @param uri
   * @return
   */
  private URL toURL(final URI uri) {
    try {
      return uri.toURL();
    } catch (MalformedURLException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
    // TODO
    return null;
  }

  /**
   * 全クラスを定義内からロードしてクラスオブジェクトの集合を返す．
   * 
   * @param memoryClassLoader
   * @param fqns
   * @return
   * @throws ClassNotFoundException
   */
  private List<Class<?>> loadAllClasses(final MemoryClassLoader memoryClassLoader,
      final List<FullyQualifiedName> fqns) throws ClassNotFoundException {
    final List<Class<?>> classes = new ArrayList<>();
    for (final FullyQualifiedName fqn : fqns) {
      classes.add(memoryClassLoader.loadClass(fqn)); // 例外が出るので非stream処理
    }
    return classes;
  }



  /**
   * JUnit実行のイベントリスナー．内部クラス． JUnit実行前のJaCoCoの初期化，およびJUnit実行後のJaCoCoの結果回収を行う．
   *
   * メモ：JUnitには「テスト成功時」のイベントリスナーがないので，テスト成否をDescriptionに強引に追記して管理
   *
   * @author shinsuke
   */
  class CoverageMeasurementListener extends RunListener {

    final private TestResults testResults;
    final private List<FullyQualifiedName> measuredClasses;
    private boolean wasFailed;

    /**
     * constructor
     *
     * @param measuredFQNs 計測対象のクラス名一覧
     * @param storedTestResults テスト実行結果の保存先
     * @throws Exception
     */
    public CoverageMeasurementListener(List<FullyQualifiedName> measuredFQNs,
        final TestResults storedTestResults) throws Exception {
      jacocoRuntime.startup(jacocoRuntimeData);
      testResults = storedTestResults;
      measuredClasses = measuredFQNs;
    }

    @Override
    public void testStarted(Description description) {
      jacocoRuntimeData.reset();
      wasFailed = false;
    }

    @Override
    public void testFailure(Failure failure) {
      wasFailed = true;
    }

    @Override
    public void testFinished(Description description) throws IOException {
      collectRuntimeData(description);
    }

    /**
     * Descriptionから実行したテストメソッドのFQNを取り出す．
     *
     * @param description
     * @return
     */
    private FullyQualifiedName getTestMethodName(Description description) {
      return new TestFullyQualifiedName(description.getTestClass()
          .getName() + "." + description.getMethodName());
    }

    /**
     * jacocoにより計測した行ごとのCoverageを回収し，TestResultsに格納する．
     *
     * @throws IOException
     */
    private void collectRuntimeData(final Description description) throws IOException {
      final CoverageBuilder coverageBuilder = new CoverageBuilder();
      analyzeJacocoRuntimeData(coverageBuilder);
      addJacocoCoverageToTestResults(coverageBuilder, description);
    }

    /**
     * jacocoにより計測した行ごとのCoverageを回収する．
     *
     * @param coverageBuilder 計測したCoverageを格納する保存先
     * @throws IOException
     */
    private void analyzeJacocoRuntimeData(final CoverageBuilder coverageBuilder)
        throws IOException {
      final ExecutionDataStore executionData = new ExecutionDataStore();
      final SessionInfoStore sessionInfo = new SessionInfoStore();
      jacocoRuntimeData.collect(executionData, sessionInfo, false);
      // jacocoRuntime.shutdown(); // Don't shutdown (This statement is a cause for bug #290)

      final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
      for (final FullyQualifiedName measuredClass : measuredClasses) {
        final byte[] bytecode = buildResults.getBinaryStore()
            .get(measuredClass)
            .getByteCode();
        analyzer.analyzeClass(bytecode, measuredClass.value);
      }
    }

    /**
     * 回収したCoverageを型変換しTestResultsに格納する．
     *
     * @param coverageBuilder Coverageが格納されたビルダー
     * @param description テストの実行情報
     */
    private void addJacocoCoverageToTestResults(final CoverageBuilder coverageBuilder,
        final Description description) {
      final FullyQualifiedName testMethodFQN = getTestMethodName(description);

      final Map<FullyQualifiedName, Coverage> coverages = coverageBuilder.getClasses()
          .stream()
          .map(Coverage::new)
          .collect(Collectors.toMap(c -> c.executedTargetFQN, Functions.identity()));

      final TestResult testResult = new TestResult(testMethodFQN, wasFailed, coverages);
      testResults.add(testResult);
    }
  }

}
