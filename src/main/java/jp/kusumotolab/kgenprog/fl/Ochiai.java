package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class Ochiai implements FaultLocalization {

  @Override
  public List<Suspiciouseness> exec(TargetProject targetProject, Variant variant,
      TestProcessBuilder testExecutor) {
    // TestResults.getExecutedFailedTestCounts() 等を使う．( = a_ef )
    // TODO Auto-generated method stub
    return null;
  }

}
