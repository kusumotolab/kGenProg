package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public interface FaultLocalization {

  public List<Suspiciousness> exec(TargetProject targetProject, Variant variant,
      TestProcessBuilder testExecutor);
}
