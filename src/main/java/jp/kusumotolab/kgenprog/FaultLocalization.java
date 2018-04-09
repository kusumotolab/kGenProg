package jp.kusumotolab.kgenprog;

import java.util.List;

public interface FaultLocalization {

    public List<Suspiciouseness> exec(TargetProject targetProject, Variant variant);
}
