package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;

public class TestExecutor {

  private final Configuration config;
  private final ProjectBuilder projectBuilder;

  public TestExecutor(final Configuration config) {
    this.config = config;
    projectBuilder = new ProjectBuilder(config.getTargetProject());
  }

  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    if (!generatedSourceCode.isGenerationSuccess()) {
      return EmptyTestResults.instance;
    }

    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    final TestThread testThread =
        new TestThread(buildResults, config.getTargetProject(), config.getExecutedTests());
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<?> future = executor.submit(testThread);
    executor.shutdown();
    try {
      future.get();
    } catch (final ExecutionException e) {
      // TODO Should handle safely
      // Executor側での例外をそのまま通す．
      e.printStackTrace();
    } catch (final InterruptedException e) {
      // TODO Should handle safely
      e.printStackTrace();
    }

    return testThread.getTestResults();
  }
}
