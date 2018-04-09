package jp.kusumotolab.kgenprog;

import java.util.List;

public abstract class Gene {

    public abstract List<Base> getBases();
    public abstract List<Gene> generateNextGenerationGenes(List<Base> bases);
}
