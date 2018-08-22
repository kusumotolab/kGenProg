package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;

public class VariantStore {

  private List<Variant> currentVariants;
  private final OrdinalNumber generation;
  private final OrdinalNumber foundSolutions;
  
  public VariantStore(final Variant ...initialVariants) {
    this(Arrays.asList(initialVariants));
  }
  
  public VariantStore(final List<Variant> initialVariants) {
    currentVariants = new ArrayList<>(initialVariants);
    generation = new OrdinalNumber(1);
    foundSolutions = new OrdinalNumber(0);
  }
  
  public OrdinalNumber getGenerationNumber() {
    return generation;
  }
  
  public OrdinalNumber getFoundSolutionsNumber() {
    return foundSolutions;
  }

  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }
  
  public void addFoundSolution(final Variant variant) {
    
  }

  public List<Variant> getFoundSolutions() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void setNextGenerationVariants(final List<Variant> variants) {
    generation.incrementAndGet();
    currentVariants = variants;
  }
}
