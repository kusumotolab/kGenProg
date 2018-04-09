package jp.kusumotolab.kgenprog;

import java.util.List;

public interface Crossover {

    public List<Gene> exec(List<Variant> variants);
}
