package jp.kusumotolab.kgenprog.ga;

import java.util.Collections;
import java.util.List;

public class SiglePointCrossover implements Crossover {

  @Override
  public List<Gene> exec(final List<Variant> variants) {
    return Collections.emptyList();
  }

}
