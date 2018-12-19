package jp.kusumotolab.kgenprog.project.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class ParallelTestExecutorTest {

  @Test
  public void testExec() {
    final TestExecutor mockExecutor = mock(TestExecutor.class);
    final TestExecutor testExecutor = new ParallelTestExecutor(mockExecutor);
    testExecutor.exec(null);
    verify(mockExecutor, times(1)).exec(any());
  }

  @Test
  public void testExecAsync() {
    final TestExecutor mockExecutor = mock(TestExecutor.class);
    when(mockExecutor.exec(any())).then(e -> new TestResults());

    final ParallelTestExecutor testExecutor = new ParallelTestExecutor(mockExecutor);
    final List<Single<String>> singleList = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final Single<GeneratedSourceCode> sourceCodeSingle = Single.just(
          mock(GeneratedSourceCode.class));
      final Single<TestResults> single = testExecutor.execAsync(sourceCodeSingle);
      final Single<String> threadName = single.map(e -> {
        final Thread currentThread = Thread.currentThread();
        return currentThread.getName();
      });
      singleList.add(threadName);
    }

    final Single<List<String>> listSingle = Single.concat(singleList)
        .toList();
    final List<String> list = listSingle.blockingGet();
    final HashSet<String> threadNameSet = new HashSet<>(list);
    assertThat(threadNameSet.size()).isGreaterThanOrEqualTo(2);
  }
}
