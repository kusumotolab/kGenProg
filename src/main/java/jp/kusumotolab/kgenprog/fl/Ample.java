package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Ample).<br>
 * {@code value = Math.abs(ef / (ef + nf) - ep / (ep + np))}<br>
 */
public class Ample extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return Math.abs(ef / (ef + nf) - ep / (ep + np));
  }

}
