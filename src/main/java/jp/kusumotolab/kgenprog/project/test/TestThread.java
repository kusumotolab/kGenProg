package jp.kusumotolab.kgenprog.project.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
import jp.kusumotolab.kgenprog.project.BuildResults;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.EmptyBuildResults;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.build.CompilationUnit;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * @author shinsuke
 */
class TestThread extends Thread {

  private MemoryClassLoader memoryClassLoader;
  private final IRuntime jacocoRuntime;
  private final Instrumenter jacocoInstrumenter;
  private final RuntimeData jacocoRuntimeData;
  private TestResults testResults; // used for return value in multi thread
  private BuildResults buildResults;

  private final GeneratedSourceCode generatedSourceCode;
  private final TargetProject targetProject;
  private final List<String> executionTestNames;

  public TestThread(final GeneratedSourceCode generatedSourceCode,
      final TargetProject targetProject, final List<String> executionTestNames) {
    this.jacocoRuntime = new LoggerRuntime();
    this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
    this.jacocoRuntimeData = new RuntimeData();

    this.generatedSourceCode = generatedSourceCode;
    this.targetProject = targetProject;
    this.executionTestNames = executionTestNames;
  }

  // Result extraction point for multi thread
  public TestResults getTestResults() {
    return this.testResults;
  }

  /**
   * JaCoCo + JUnitの実行． sourceClassesで指定したソースをJaCoCoでinstrumentして，JUnitを実行する．
   * 実行対象のclasspathは通ってることが前提．
   */
  public void run() {
    buildResults = buildProject();
    if (buildResults instanceof EmptyBuildResults) {
      testResults = EmptyTestResults.instance;
      return;
    }

    final List<ClassPath> classPaths = targetProject.getClassPaths();

    final List<FullyQualifiedName> targetFQNs = getTargetFQNs();
    final List<FullyQualifiedName> testFQNs = getTestFQNs();
    final List<FullyQualifiedName> executionTestFQNs = getExecutionTestFQNs();

    final TestResults testResults = new TestResults();

    final URL[] classpathUrls = convertClasspathsToURLs(classPaths);
    memoryClassLoader = new MemoryClassLoader(classpathUrls);

    try {
      addAllDefinitions(targetFQNs);
      addAllDefinitions(testFQNs);
      final List<Class<?>> junitClasses = loadAllClasses(executionTestFQNs);

      jacocoRuntime.startup(jacocoRuntimeData);

      // TODO
      // junitCore.run(Classes<?>...)による一括実行を使えないか？
      // 速度改善するかも．jacocoとの連携が難しい．listenerを再利用すると結果がバグる
      for (final Class<?> junitClass : junitClasses) {
        final JUnitCore junitCore = new JUnitCore();
        final CoverageMeasurementListener listener =
            new CoverageMeasurementListener(targetFQNs, testResults);
        junitCore.addListener(listener);
        junitCore.run(junitClass);
      }
    } catch (Exception e) {
      // TODO
      // Should handle safely
      // ひとまず本クラスをThreadで包むためにRuntimeExceptionでエラーを吐く．
      throw new RuntimeException(e);
    }

    // TODO 翻訳のための一時的な処理
    testResults.setBuildResults(buildResults);
    this.testResults = testResults;
  }

  private BuildResults buildProject() {
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    return projectBuilder.build(generatedSourceCode);
  }

  private List<FullyQualifiedName> convertExecutionTestNameToFqn() {
    return executionTestNames.stream()
        .map(TestFullyQualifiedName::new)
        .collect(Collectors.toList());
  }

  private List<FullyQualifiedName> getTargetFQNs() {
    return getFQNs(targetProject.getProductSourcePaths());
  }

  private List<FullyQualifiedName> getTestFQNs() {
    return getFQNs(targetProject.getTestSourcePaths());
  }

  private List<FullyQualifiedName> getExecutionTestFQNs() {
    final List<FullyQualifiedName> execTests = convertExecutionTestNameToFqn();
    if (execTests.isEmpty()) {
      return getTestFQNs();
    }
    return execTests;
  }

  private List<FullyQualifiedName> getFQNs(final List<? extends SourcePath> sourcesPaths) {
    return sourcesPaths.stream()
        .map(source -> buildResults.getPathToFQNs(source.path))
        .filter(fqn -> null != fqn)
        .flatMap(c -> c.stream())
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
   * @param fqns
   * @return
   * @throws ClassNotFoundException
   */
  private List<Class<?>> loadAllClasses(final List<FullyQualifiedName> fqns)
      throws ClassNotFoundException {
    return fqns.stream()
        .map(fqn -> loadClass(fqn))
        .collect(Collectors.toList());
  }

  /***
   * MemoryClassLoaderに対して全てのバイトコード定義を追加する（ロードはせず）．
   * 
   * @param fqns
   * @throws IOException
   */
  private void addAllDefinitions(final List<FullyQualifiedName> fqns) throws IOException {
    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    for (final FullyQualifiedName fqn : fqns) {
      final CompilationUnit compilatinoUnit = compilationPackage.getCompilationUnit(fqn.value);
      final byte[] bytecode = compilatinoUnit.getBytecode();
      final byte[] instrumentedBytecode = jacocoInstrumenter.instrument(bytecode, "");
      memoryClassLoader.addDefinition(fqn, instrumentedBytecode);
    }
  }

  /**
   * 単一クラスをロードしてクラスオブジェクトを返す． Lambdaの中での例外回避のため．
   * 
   * @param fqn
   * @return
   */
  private Class<?> loadClass(final FullyQualifiedName fqn) {
    try {
      return memoryClassLoader.loadClass(fqn);
    } catch (ClassNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
    return null;
  }

  /**
   * JUnit実行のイベントリスナー．内部クラス． JUnit実行前のJaCoCoの初期化，およびJUnit実行後のJaCoCoの結果回収を行う．
   *
   * メモ：JUnitには「テスト成功時」のイベントリスナーがないので，テスト成否をDescriptionに強引に追記して管理
   *
   * @author shinsuke
   */
  class CoverageMeasurementListener extends RunListener {

    final private List<FullyQualifiedName> measuredClasses;
    final public TestResults testResults;
    private boolean wasFailed;

    /**
     * constructor
     *
     * @param measuredFQNs 計測対象のクラス名一覧
     * @param storedTestResults テスト実行結果の保存先
     * @throws Exception
     */
    public CoverageMeasurementListener(List<FullyQualifiedName> measuredFQNs,
        TestResults storedTestResults) throws Exception {
      testResults = storedTestResults;
      measuredClasses = measuredFQNs;
    }

    @Override
    public void testStarted(Description description) {
      resetJacocoRuntimeData();
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
     * JaCoCoが回収した実行結果をリセットする．
     */
    private void resetJacocoRuntimeData() {
      jacocoRuntimeData.reset();
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
        final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
        final CompilationUnit compilatinoUnit =
            compilationPackage.getCompilationUnit(measuredClass.value);
        final byte[] bytecode = compilatinoUnit.getBytecode();
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
          .collect(Collectors.toMap(c -> c.executedTargetFQN, c -> c));

      final TestResult testResult = new TestResult(testMethodFQN, wasFailed, coverages);
      testResults.add(testResult);
    }
  }

}
