package jp.kusumotolab.kgenprog.project.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
import jp.kusumotolab.kgenprog.project.ClassPath;

/**
 * 
 * @author shinsuke
 *
 */
class TestThread extends Thread {

  private MemoryClassLoader memoryClassLoader;
  private final IRuntime jacocoRuntime;
  private final Instrumenter jacocoInstrumenter;
  private final RuntimeData jacocoRuntimeData;
  private final List<ClassPath> classPaths;
  private final List<FullyQualifiedName> sourceFQNs;
  private final List<FullyQualifiedName> testFQNs;
  private TestResults testResults; // used for return value in multi thread

  public TestThread(final List<ClassPath> classPaths, final List<FullyQualifiedName> sourceFQNs,
      final List<FullyQualifiedName> testFQNs) {
    this.jacocoRuntime = new LoggerRuntime();
    this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
    this.jacocoRuntimeData = new RuntimeData();

    // Execution params
    this.classPaths = classPaths;
    this.sourceFQNs = sourceFQNs;
    this.testFQNs = testFQNs;

  }

  public TestResults getTestResults() {
    return this.testResults;
  }

  /**
   * JaCoCo + JUnitの実行． sourceClassesで指定したソースをJaCoCoでinstrumentして，JUnitを実行する．
   * 実行対象のclasspathは通ってることが前提．
   * 
   * @param sourceFQNs 計測対象のソースコードのFQNのリスト
   * @param testFQNs 実行する単体テストのFQNのリスト
   * @return テストの実行結果（テスト成否やCoverage等）
   * @throws Exception
   */
  public void run() {
    final TestResults testResults = new TestResults();

    final URL[] classpathUrls = convertClasspathsToURLs(classPaths);
    memoryClassLoader = new MemoryClassLoader(classpathUrls);

    try {
      loadInstrumentedClasses(sourceFQNs);
      final List<Class<?>> junitClasses = loadInstrumentedClasses(testFQNs);

      // TODO
      // junitCore.run(Classes<?>...)による一括実行を使えないか？
      // 速度改善するかも．jacocoとの連携が難しい．listenerを再利用すると結果がバグる
      for (final Class<?> junitClass : junitClasses) {
        final JUnitCore junitCore = new JUnitCore();
        final CoverageMeasurementListener listener =
            new CoverageMeasurementListener(sourceFQNs, testResults);
        junitCore.addListener(listener);
        junitCore.run(junitClass);
      }
    } catch (Exception e) {
      // TODO
      // Should handle safely
      // ひとまず本クラスをThreadで包むためにRuntimeExceptionでエラーを吐く．
      throw new RuntimeException(e);
    }
    this.testResults = testResults;
  }

  private URL[] convertClasspathsToURLs(final List<ClassPath> classpaths) {
    return classpaths.stream()
        .map(cp -> cp.path.toUri())
        .map(uri -> toURL(uri))
        .toArray(URL[]::new);
  }

  /**
   * To avoid Malforme
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
   * jacoco計測のためのクラス書き換えを行い，その書き換えたバイト配列をクラスロードする．
   * 
   * @param fqns 書き換え対象（計測対象）クラスのFQNs
   * @return 書き換えたクラスオブジェクトs
   * @throws Exception
   */
  private List<Class<?>> loadInstrumentedClasses(final List<FullyQualifiedName> fqns)
      throws Exception {
    final List<Class<?>> loadedClasses = new ArrayList<>();
    for (final FullyQualifiedName fqn : fqns) {
      final byte[] instrumentedData = getInstrumentedClassBinary(fqn);
      loadedClasses.add(loadClass(fqn, instrumentedData));
    }
    return loadedClasses;
  }

  /**
   * jacoco計測のためのクラス書き換えを行ったバイト配列を返す． クラスローダーに対する作用はなし．
   * 
   * @param fqn 書き換え対象（計測対象）クラスのFQNs
   * @return 書き換えたクラスオブジェクトのバイナリ
   * @throws Exception
   */
  private byte[] getInstrumentedClassBinary(final FullyQualifiedName fqn) throws Exception {
    final InputStream is = getClassFileInputStream(fqn);
    final byte[] bytes = jacocoInstrumenter.instrument(is, "");
    is.close();
    return bytes;
  }

  /**
   * MemoryClassLoaderを使ったクラスロード．
   * 
   * @param fqn
   * @param bytes
   * @return
   * @throws ClassNotFoundException
   */
  private Class<?> loadClass(final FullyQualifiedName fqn, final byte[] bytes)
      throws ClassNotFoundException {
    memoryClassLoader.addDefinition(fqn, bytes);
    return memoryClassLoader.loadClass(fqn); // force load instrumented class.
  }

  /**
   * ファイルシステムから.classファイルへのInputStreamを取り出す．
   * 
   * @param fqn 読み込み対象のFQN
   * @return
   */
  private InputStream getClassFileInputStream(final FullyQualifiedName fqn) {
    final String resource = fqn.value.replace('.', '/') + ".class";
    return memoryClassLoader.getResourceAsStream(resource);
  }

  /**
   * JUnit実行のイベントリスナー．内部クラス． JUnit実行前のJaCoCoの初期化，およびJUnit実行後のJaCoCoの結果回収を行う．
   * 
   * メモ：JUnitには「テスト成功時」のイベントリスナーがないので，テスト成否をDescriptionに強引に追記して管理
   * 
   * @author shinsuke
   *
   */
  class CoverageMeasurementListener extends RunListener {

    private final Description FAILED = Description.createTestDescription("failed", "failed");

    final private List<FullyQualifiedName> measuredClasses;
    final public TestResults testResults;

    /**
     * constructor
     * 
     * @param measuredFQNs 計測対象のクラス名一覧
     * @param storedTestResults テスト実行結果の保存先
     * @throws Exception
     */
    public CoverageMeasurementListener(List<FullyQualifiedName> measuredFQNs,
        TestResults storedTestResults) throws Exception {
      jacocoRuntime.startup(jacocoRuntimeData);
      testResults = storedTestResults;
      measuredClasses = measuredFQNs;
    }

    @Override
    public void testStarted(Description description) {
      resetJacocoRuntimeData();
    }

    @Override
    public void testFailure(Failure failure) {
      noteTestExecutionFail(failure);
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
     * Failureオブジェクトの持つDescriptionに，当該テストがfailしたことをメモする．
     * 
     * @param failure
     */
    private void noteTestExecutionFail(Failure failure) {
      failure.getDescription()
          .addChild(FAILED);
    }

    /**
     * Descriptionから当該テストがfailしたかどうかを返す．
     * 
     * @param description
     * @return テストがfailしたかどうか
     */
    private boolean isFailed(Description description) {
      return description.getChildren()
          .contains(FAILED);
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
      jacocoRuntime.shutdown();

      final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
      for (final FullyQualifiedName measuredClass : measuredClasses) {
        final InputStream is = getClassFileInputStream(measuredClass);
        analyzer.analyzeClass(is, measuredClass.value);
        is.close();
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
      final boolean isFailed = isFailed(description);

      final Map<FullyQualifiedName, Coverage> coverages = coverageBuilder.getClasses()
          .stream()
          .map(Coverage::new)
          .collect(Collectors.toMap(c -> c.executedTargetFQN, c -> c));

      final TestResult testResult = new TestResult(testMethodFQN, isFailed, coverages);
      testResults.add(testResult);
    }
  }

}
