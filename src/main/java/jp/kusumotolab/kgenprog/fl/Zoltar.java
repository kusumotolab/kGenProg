package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Zoltar). <br>
 * {@code value = ef / (ef + nf + ep + 10000 * nf * ep / ef)}
 */
public class Zoltar extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return ef / (ef + nf + ep + 10000d * nf * ep / ef);
  }

}
