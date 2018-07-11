package jp.kusumotolab.kgenprog.ga;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiglePointCrossover implements Crossover {

  private static Logger log = LoggerFactory.getLogger(SiglePointCrossover.class);

  @Override
  public List<Gene> exec(final List<Variant> variants) {
    log.debug("enter exec(List<>)");
    return Collections.emptyList();
  }

}
