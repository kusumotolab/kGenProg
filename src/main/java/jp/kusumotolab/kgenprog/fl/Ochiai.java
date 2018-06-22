package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class Ochiai implements FaultLocalization {

  private Logger log = LoggerFactory.getLogger(Ochiai.class);

  @Override
  public List<Suspiciouseness> exec(TargetProject targetProject, Variant variant,
      TestProcessBuilder testExecutor) {
    log.debug("enter exec(TargetProject, Variant, TestProcessBuilder)");
    // TestResults.getExecutedFailedTestCounts() 等を使う．( = a_ef )
    // TODO Auto-generated method stub
    return null;
  }

}
