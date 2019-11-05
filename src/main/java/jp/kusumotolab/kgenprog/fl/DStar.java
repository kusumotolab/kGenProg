package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(DStar).<br>
 * {@code value = Math.pow(ef, 2) / (ep + nf)}<br>
 */
public class DStar extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return Math.pow(ef, 2) / (ep + nf);
  }

}
