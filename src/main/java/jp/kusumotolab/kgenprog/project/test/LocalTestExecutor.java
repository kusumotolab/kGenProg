package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;

public class LocalTestExecutor implements TestExecutor {

  private final Configuration config;
  private final ProjectBuilder projectBuilder;

  public LocalTestExecutor(final Configuration config) {
    this.config = config;
    projectBuilder = new ProjectBuilder(config.getTargetProject());
  }

  @Override
  public TestResults exec(final Variant variant) {
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    if (!generatedSourceCode.isGenerationSuccess()) {
      return EmptyTestResults.instance;
    }

    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);
    final TestThread testThread = new TestThread(buildResults, config.getTargetProject(),
        config.getExecutedTests(), config.getTestTimeLimitSeconds());
    testThread.run();

    return testThread.getTestResults();
  }
}
