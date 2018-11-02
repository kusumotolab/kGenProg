package jp.kusumotolab.kgenprog.project.build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyBuildResults extends BuildResults {

  @SuppressWarnings("unused")
  private Logger log = LoggerFactory.getLogger(EmptyBuildResults.class);

  public static final EmptyBuildResults instance = new EmptyBuildResults();

  private EmptyBuildResults() {
    super(null, null, null, true);
  }

}
