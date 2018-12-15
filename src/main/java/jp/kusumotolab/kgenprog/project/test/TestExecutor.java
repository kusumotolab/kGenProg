package jp.kusumotolab.kgenprog.project.test;

import io.reactivex.Single;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public interface TestExecutor {

  TestResults exec(final GeneratedSourceCode generatedSourceCode);

  default Single<TestResults> execAsync(final Single<GeneratedSourceCode> generatedSourceCode){
    return generatedSourceCode.map(this::exec);
  }
}
