package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Ochiai). <br>
 * {@code value = ef / Math.sqrt((ef + nf) * (ef + ep))}
 */
public class Ochiai extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(double ef, double nf, double ep, double np) {
    return ef / Math.sqrt((ef + nf) * (ef + ep));
  }

  @Override
  protected boolean isSkippablePassedTests() {
    return true;
  }
}
