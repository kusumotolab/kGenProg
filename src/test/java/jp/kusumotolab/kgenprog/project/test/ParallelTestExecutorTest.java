package jp.kusumotolab.kgenprog.project.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.junit.Test;
import io.reactivex.Observable;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class ParallelTestExecutorTest {

  @Test
  public void testExec() {
    final TestExecutor mockExecutor = mock(TestExecutor.class);
    final AtomicBoolean called = new AtomicBoolean(false);
    when(mockExecutor.exec(any())).then(e -> {
      called.set(true);
      return null;
    });
    final TestExecutor testExecutor = new ParallelTestExecutor(mockExecutor);
    testExecutor.exec(null);
    assertThat(called).isTrue();
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

    // Single => Observable
    final List<Observable<String>> observables = singleList.stream()
        .map(Single::toObservable)
        .collect(Collectors.toList());

    // 全ての Observables を一つの Single にまとめる
    final Single<List<String>> listSingle = Observable.zip(observables,
        objects -> Arrays.stream(objects)
            .map(e -> ((String) e))
            .collect(Collectors.toList()))
        .single(Collections.emptyList());

    // まとめた Single の結果を取得
    final List<String> list = listSingle.blockingGet();
    final HashSet<String> threadNameSet = new HashSet<>(list);
    assertThat(threadNameSet.size()).isGreaterThanOrEqualTo(2);
  }
}
