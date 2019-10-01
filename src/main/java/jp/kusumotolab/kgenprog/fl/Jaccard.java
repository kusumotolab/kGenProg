package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Jaccard). <br>
 * {@code value = ef / (ef + nf + ep)}
 */
public class Jaccard extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(double ef, double nf, double ep, double np) {
    return ef / (double) (ef + nf + ep);
  }

  @Override
  protected boolean isSkippablePassedTests() {
    return true;
  }
}
