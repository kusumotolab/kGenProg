package jp.kusumotolab.kgenprog.project;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public interface ResultGenerator {

  public List<Result> exec(final TargetProject targetProject, final Variant modified);
}
