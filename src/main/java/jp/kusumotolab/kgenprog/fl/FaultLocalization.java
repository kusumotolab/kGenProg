package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public interface FaultLocalization {

  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode, TestResults testResults);
}
