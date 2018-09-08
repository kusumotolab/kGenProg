package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public interface Crossover {

  public List<Gene> exec(List<Variant> variants);
}
