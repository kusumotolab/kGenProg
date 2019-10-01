package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Ample).<br>
 * {@code value = Math.abs(ef / (ef + nf) - ep / (ep + np))}<br>
 */
public class Ample extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(double ef, double nf, double ep, double np) {
    return Math.abs(ef / (double) (ef + nf) - ep / (double) (ep + np));
  }

  @Override
  protected boolean isSkippablePassedTests() {
    return false;
  }
}
