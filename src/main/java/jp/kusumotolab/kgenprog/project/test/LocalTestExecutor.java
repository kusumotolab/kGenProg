package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;

/**
 * junitテストをローカルマシン上で実行する．<br>
 * コンストラクタで指定されたConfiguration情報に基づき，指定Variantのテストを実行する．<br>
 *
 * TestThreadクラスのラッパークラスである．<br>
 *
 * @author shinsuke
 */
public class LocalTestExecutor implements TestExecutor {

  private final Configuration config;
  private final ProjectBuilder projectBuilder;

  /**
   * コンストラクタ．<br>
   *
   * @param config テスト実行に必要なプロジェクト設定情報
   */
  public LocalTestExecutor(final Configuration config) {
    this.config = config;
    projectBuilder = new ProjectBuilder(config.getTargetProject());
  }

  /**
   * 対象ソースコードのビルドとテストの実行を行う．<br>
   * ビルド失敗時はEmptyTestResultsを返す．<br>
   */
  @Override
  public TestResults exec(final Variant variant) {
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    if (!generatedSourceCode.isGenerationSuccess()) {
      return new EmptyTestResults("build failed.");
    }

    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);
    final TestThread testThread = new TestThread(buildResults, config.getTargetProject(),
        config.getExecutedTests(), config.getTestTimeLimitSeconds());
    testThread.run();

    return testThread.getTestResults();
  }
}
