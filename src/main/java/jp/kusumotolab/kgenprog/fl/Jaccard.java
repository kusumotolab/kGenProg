package jp.kusumotolab.kgenprog.fl;

/**
 * FL戦略の一つ(Jaccard). <br>
 * {@code value = ef / (ef + nf + ep)}
 */
public class Jaccard extends SpectrumBasedFaultLocalization {

  @Override
  protected double formula(final double ef, final double nf, final double ep, final double np) {
    return ef / (double) (ef + nf + ep);
  }

}
