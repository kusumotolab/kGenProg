package jp.kusumotolab.kgenprog.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Ochiai;

public class EmptyBuildResults extends BuildResults {

  @SuppressWarnings("unused")
  private Logger log = LoggerFactory.getLogger(Ochiai.class);

  public static final EmptyBuildResults instance = new EmptyBuildResults();

  private EmptyBuildResults() {
    super(null, true, null, null, null);
  }

}
