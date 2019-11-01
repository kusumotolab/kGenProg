package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Ochiai). <br>
 * {@code value = ef / Math.sqrt((ef + nf) * (ef + ep))}
 */
public class Ochiai extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return ef / Math.sqrt((ef + nf) * (ef + ep));
  }

}
