package jp.kusumotolab.kgenprog.project.test;

import io.reactivex.Single;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public interface TestExecutor {

  TestResults exec(final Variant variant);

  default Single<TestResults> execAsync(final Single<Variant> variantSingle){
    return variantSingle.map(this::exec);
  }

  default void initialize() {}

  default void finish() {}
}
