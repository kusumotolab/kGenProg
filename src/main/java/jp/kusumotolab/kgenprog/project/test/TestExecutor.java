package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jp.kusumotolab.kgenprog.project.ClassPath;

public class TestExecutor {

  final private long timeoutSeconds;

  public TestExecutor(final long timeoutSeconds) {
    // TODO
    // timeoutSecondsはconfigから取り出すべき
    this.timeoutSeconds = timeoutSeconds;
  }

  public TestResults exec(final List<ClassPath> classPath,
      final List<FullyQualifiedName> sourceFQNs, final List<FullyQualifiedName> testFQNs) {

    final TestThread testThread = new TestThread(classPath, sourceFQNs, testFQNs);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    final Future<?> future = executor.submit(testThread);
    executor.shutdown();
    try {
      future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (final InterruptedException | ExecutionException e) {
      // TODO Should handle safely
      e.printStackTrace();
    } catch (TimeoutException e) {
      return EmptyTestResults.instance;
    }

    return testThread.getTestResults();
  }
}
