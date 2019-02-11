package jp.kusumotolab.kgenprog.ga.crossover;


public class CrossoverInfeasibleException extends Exception {

  private static final long serialVersionUID = 1L;

  public CrossoverInfeasibleException(final String text) {
    super(text);
  }
}
