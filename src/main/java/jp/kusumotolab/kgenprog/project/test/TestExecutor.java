package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class TestExecutor {

  private final TargetProject targetProject;
  private final long timeoutSeconds;

  public TestExecutor(final Configuration config) {
    this(config.getTargetProject(), config.getTimeLimit());
  }

  @Deprecated
  public TestExecutor(final TargetProject targetProject) {
    this(targetProject, Configuration.DEFAULT_TIME_LIMIT);
  }

  @Deprecated
  public TestExecutor(final TargetProject targetProject, final long timeoutSeconds) {
    this.targetProject = targetProject;
    this.timeoutSeconds = timeoutSeconds;
  }


  // これを活かす
  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    final TestThread testThread = new TestThread(generatedSourceCode, targetProject);

    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<?> future = executor.submit(testThread);
    executor.shutdown();
    try {
      future.get(timeoutSeconds, TimeUnit.SECONDS);
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

  // これは死ぬ
  // public TestResults ______exec(final GeneratedSourceCode generatedSourceCode,
  // final List<ClassPath> classPath, final List<FullyQualifiedName> sourceFQNs,
  // final List<FullyQualifiedName> testFQNs) throws ExecutionException {
  //
  // final TestThread testThread =
  // new TestThread(generatedSourceCode, targetProject, classPath, sourceFQNs, testFQNs);
  //
  // final ExecutorService executor = Executors.newSingleThreadExecutor();
  // final Future<?> future = executor.submit(testThread);
  // executor.shutdown();
  // try {
  // future.get(timeoutSeconds, TimeUnit.SECONDS);
  // } catch (final ExecutionException e) {
  // // Executor側での例外をそのまま通す．
  // throw e;
  // } catch (final InterruptedException e) {
  // // TODO Should handle safely
  // e.printStackTrace();
  // } catch (final TimeoutException e) {
  // return EmptyTestResults.instance;
  // }
  //
  // return testThread.getTestResults();
  // }
}
