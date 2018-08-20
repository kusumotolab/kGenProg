package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class TestExecutor {

  private final TargetProject targetProject;
  private final long timeoutSeconds;

  public TestExecutor(final TargetProject targetProject, final long timeoutSeconds) {
    this.targetProject = targetProject;

    // TODO
    // timeoutSecondsはconfigから取り出すべき
    this.timeoutSeconds = timeoutSeconds;
  }


  // これを活かす
  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    return null;
  }

  // これは死ぬ
  public TestResults exec(final GeneratedSourceCode generatedSourceCode,
      final List<ClassPath> classPath, final List<FullyQualifiedName> sourceFQNs,
      final List<FullyQualifiedName> testFQNs) throws ExecutionException {

    final TestThread testThread =
        new TestThread(generatedSourceCode, targetProject, classPath, sourceFQNs, testFQNs);

    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<?> future = executor.submit(testThread);
    executor.shutdown();
    try {
      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (final ExecutionException e) {
      // Executor側での例外をそのまま通す．
      throw e;
    } catch (final InterruptedException e) {
      // TODO Should handle safely
      e.printStackTrace();
    } catch (final TimeoutException e) {
      return EmptyTestResults.instance;
    }

    return testThread.getTestResults();
  }
}
