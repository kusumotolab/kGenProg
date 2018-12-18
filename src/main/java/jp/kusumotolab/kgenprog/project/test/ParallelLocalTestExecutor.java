package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class ParallelLocalTestExecutor extends LocalTestExecutor {

  private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
      .availableProcessors());

  public ParallelLocalTestExecutor(final Configuration config) {
    super(config);
  }

  @Override
  public Single<TestResults> execAsync(final Single<GeneratedSourceCode> generatedSourceCode) {
    return generatedSourceCode.subscribeOn(Schedulers.from(executorService))
        .map(this::exec);
  }
}
