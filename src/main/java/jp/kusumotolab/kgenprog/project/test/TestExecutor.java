package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class TestExecutor {

  private final Configuration config;

  public TestExecutor(final Configuration config) {
    this.config = config;
  }

  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    if (!generatedSourceCode.isGenerationSuccess()) {
      return EmptyTestResults.instance;
    }

    final TestThread testThread =
        new TestThread(generatedSourceCode, config.getTargetProject(), config.getExecutedTests());
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<?> future = executor.submit(testThread);
    executor.shutdown();
    try {
      future.get(config.getTestTimeLimitSeconds(), TimeUnit.SECONDS);
    } catch (final ExecutionException e) {
      // TODO Should handle safely
      // Executor側での例外をそのまま通す．
      e.printStackTrace();
    } catch (final InterruptedException e) {
      // TODO Should handle safely
      e.printStackTrace();
    } catch (final TimeoutException e) {
      return EmptyTestResults.instance;
    }

    return testThread.getTestResults();
  }
}
