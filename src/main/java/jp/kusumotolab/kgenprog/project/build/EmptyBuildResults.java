package jp.kusumotolab.kgenprog.project.build;

public class EmptyBuildResults extends BuildResults {

  public static final EmptyBuildResults instance = new EmptyBuildResults();

  private EmptyBuildResults() {
    super(null, null, null, true);
  }

}
