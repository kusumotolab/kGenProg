package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Zoltar). <br>
 * {@code value = ef / (ef + nf + ep + 10000 * nf * ep / ef)}
 */
public class Zoltar extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(double ef, double nf, double ep, double np) {
    return ef / (ef + nf + ep + 10000d * nf * ep / ef);
  }

  @Override
  protected boolean isSkippablePassedTests() {
    return true;
  }
}
