package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Tarantula).<br>
 * {@code value = (ef / (ef + nf)) / (ef / (ef + nf) + ep / (ep + np))}
 */
public class Tarantula extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return (ef / (double) (ef + nf)) / (ef / (double) (ef + nf) + ep / (double) (ep + np));
  }

}
