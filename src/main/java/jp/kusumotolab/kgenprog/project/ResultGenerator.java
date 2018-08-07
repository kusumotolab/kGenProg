package jp.kusumotolab.kgenprog.project;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public interface ResultGenerator {

  public void exec(final TargetProject targetProject, final List<Variant> modified);
}
