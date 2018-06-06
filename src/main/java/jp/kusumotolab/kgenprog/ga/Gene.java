package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;

public abstract class Gene {

  public abstract List<Base> getBases();

  public abstract List<Gene> generateNextGenerationGenes(List<Base> bases);
}


class TreeGene extends Gene {
  private Gene parent;
  private Base base;

  @Override
  public List<Base> getBases() {


    return parent.getBases();
  }

  @Override
  public List<Gene> generateNextGenerationGenes(List<Base> bases) {
    // TODO Auto-generated method stub
    return null;
  }
}
