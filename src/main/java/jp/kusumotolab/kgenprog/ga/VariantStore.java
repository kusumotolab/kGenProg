package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;

public class VariantStore {

  private List<Variant> currentVariants;
  private final List<Variant> foundSolutions;
  private final OrdinalNumber generation;
  
  public VariantStore(final Variant ...initialVariants) {
    this(Arrays.asList(initialVariants));
  }
  
  public VariantStore(final List<Variant> initialVariants) {
    currentVariants = initialVariants;
    foundSolutions = new ArrayList<>();
    generation = new OrdinalNumber(1);
  }
  
  public OrdinalNumber getGenerationNumber() {
    return generation;
  }
  
  public OrdinalNumber getFoundSolutionsNumber() {
    return new OrdinalNumber(foundSolutions.size());
  }

  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }
  
  public void addFoundSolution(final Variant variant) {
    foundSolutions.add(variant);
  }

  public List<Variant> getFoundSolutions() {
    return foundSolutions;
  }
  
  public void setNextGenerationVariants(final List<Variant> variants) {
    generation.incrementAndGet();
    currentVariants = variants;
  }
}
