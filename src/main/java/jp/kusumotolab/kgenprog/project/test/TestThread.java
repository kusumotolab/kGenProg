package jp.kusumotolab.kgenprog.project.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionData;
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
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * Jacoco+JUnitを用いてテストを実行するスレッドオブジェクト．<br>
 * Threadクラスを継承しており，本テスト実行は別スレッドで処理される．<br>
 * このスレッド化はテストのタイムアウト処理のためであり，高速化や並列化が目的ではないことに注意．<br>
 *
 * @author shinsuke
 */
class TestThread extends Thread {

  private final IRuntime jacocoRuntime;
  private final Instrumenter jacocoInstrumenter;
  private final RuntimeData jacocoRuntimeData;
  private final BuildResults buildResults;
  private TestResults testResults; // スレッドの返り値として用いるためにnon-finalフィールド

  private final TargetProject targetProject;
  private final List<String> executionTestNames;

  private long timeout;
  private TimeUnit timeUnit;

  /**
   * コンストラクタ．
   *
   * @param buildResults テスト実行対象のバイナリを保持するビルド結果
   * @param targetProject テスト実行の対象プロジェクト
   * @param executionTestNames どのテストを実行するか
   * @param timeout タイムアウト時間（秒）
   */
  public TestThread(final BuildResults buildResults, final TargetProject targetProject,
      final List<String> executionTestNames, final long timeout) {

    this.jacocoRuntime = new LoggerRuntime();
    this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
    this.jacocoRuntimeData = new RuntimeData();
    try {
      jacocoRuntime.startup(jacocoRuntimeData);
    } catch (final Exception e) {
      // TODO should be described to log
      e.printStackTrace();
    }

    this.buildResults = buildResults;
    this.targetProject = targetProject;
    this.executionTestNames = executionTestNames;

    // カスタムJUnit上でのタイムアウト時間を設定
    this.timeout = timeout;
    this.timeUnit = TimeUnit.SECONDS; // TODO タイムアウトは秒単位が前提
  }

  /**
   * テスト結果の取り出しAPI．<br>
   * スレッド（非同期）実行されるのでrun()の返り値としてではなく，getで結果を取り出す．<br>
   *
   * @return テストの結果
   */
  public TestResults getTestResults() {
    return this.testResults;
  }

  /**
   * JaCoCo + JUnitの実行． <br>
   * コンストラクタで渡されたバイナリに対して，まずJaCoCo計測のためのinstrument処理を施す．<br>
   * さらに，そのバイナリを用いてJUnitを実行する．<br>
   */
  public void run() {
    // ビルド失敗時は即座に諦める
    if (buildResults.isBuildFailed) {
      testResults = new EmptyTestResults(buildResults);
      return;
    }

    final StopWatch stopWatch = StopWatch.createStarted();

    final List<FullyQualifiedName> productFQNs = getProductFQNs();
    final List<FullyQualifiedName> executionTestFQNs = getExecutionTestFQNs();

    // set memoryClassLoader as ContextClassLoader during JUnit execution
    final List<ClassPath> classPaths = targetProject.getClassPaths();
    final URL[] classpathUrls = convertClasspathsToURLs(classPaths);
    final MemoryClassLoader classLoader = new SkippingMemoryClassLoader(classpathUrls);

    try {
      addAllDefinitions(classLoader, productFQNs);
      final List<Class<?>> testClasses = loadAllClasses(classLoader, executionTestFQNs);

      final JUnitCore junitCore = new JUnitCore();

      // JUnitカスタムによる強制タイムアウトの指定
      junitCore.setTimeout(timeout, timeUnit);

      final List<TestResult> testResultList = new ArrayList<>();
      final RunListener listener = new CoverageMeasurementListener(testResultList);
      junitCore.addListener(listener);

      // JUnit実行対象の題材テストはMemClassLoaderでロードされているので，
      // 以下ContextClassLoaderのセットは不要．むしろKGPスレッド全体に作用してしまうのでないほうが良い．
      // Thread.currentThread().setContextClassLoader(classLoader);

      junitCore.run(testClasses.toArray(new Class<?>[testClasses.size()]));

      stopWatch.stop();
      testResults = new TestResults(buildResults, stopWatch.getTime(), testResultList);
    } catch (final ClassNotFoundException e) {
      // クラスロードに失敗．FQNの指定ミスの可能性が大
      testResults = new EmptyTestResults("failed to load classes.");
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
   * @throws IOException
   */
  private void addAllDefinitions(final MemoryClassLoader memoryClassLoader,
      final List<FullyQualifiedName> fqns) throws IOException {
    for (final JavaBinaryObject jmo : buildResults.binaryStore.getAll()) {
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
    return sourcesPaths.stream()
        .map(buildResults.binaryStore::get)
        .flatMap(Collection::stream)
        .map(JavaBinaryObject::getFqn)
        .collect(Collectors.toList());
  }

  private URL[] convertClasspathsToURLs(final List<ClassPath> classpaths) {
    return classpaths.stream()
        .map(cp -> cp.path.toUri())
        .map(this::toURL)
        .toArray(URL[]::new);
  }

  /**
   * To avoid Malformed uri in lambda expression
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

    private final List<TestResult> testResultList;
    private boolean wasFailed;
    private String failedReason;

    /**
     * constructor
     *
     * @throws Exception
     */
    public CoverageMeasurementListener(final List<TestResult> testResultList) {
      this.testResultList = testResultList;
    }

    @Override
    public void testStarted(Description description) {
      jacocoRuntimeData.reset();
      wasFailed = false;
      failedReason = null;
    }

    @Override
    public void testFailure(Failure failure) {
      wasFailed = true;
      failedReason = failure.getException()
          .getMessage();
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

      // 一度でもカバレッジ計測されたクラスのみに対してカバレッジ情報を探索
      for (final ExecutionData data : executionData.getContents()) {

        // 当該テスト実行でprobeが反応しない＝実行されていない場合はskip
        if (!data.hasHits()) {
          continue;
        }

        final String strFqn = data.getName()
            .replace("/", ".");
        final FullyQualifiedName fqn = new TargetFullyQualifiedName(strFqn);
        final byte[] bytecode = buildResults.binaryStore.get(fqn)
            .getByteCode();
        analyzer.analyzeClass(bytecode, "");
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
          .map(RawCoverage::new)
          .collect(Collectors.toMap(Coverage::getExecutedTargetFQN, Functions.identity()));

      final TestResult testResult = new TestResult(testMethodFQN, wasFailed, failedReason,
          coverages);
      testResultList.add(testResult);
    }
  }

}
