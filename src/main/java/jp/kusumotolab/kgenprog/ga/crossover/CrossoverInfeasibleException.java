package jp.kusumotolab.kgenprog.ga.crossover;

/**
 * 交叉処理がなんらかの理由により正しく行われなかった際に発生する例外．
 *
 * @author higo
 */
public class CrossoverInfeasibleException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ．例外の発生原因や症状を現す文字列を与える必要あり．
   *
   * @param text 例外の発生原因や症状
   */
  public CrossoverInfeasibleException(final String text) {
    super(text);
  }
}
