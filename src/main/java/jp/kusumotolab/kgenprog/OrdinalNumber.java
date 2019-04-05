package jp.kusumotolab.kgenprog;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 序数を生成するためのクラス
 * 
 * @author higo
 *
 */
public class OrdinalNumber extends AtomicInteger {

  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ．初期値0でインスタンスを生成する．
   */
  public OrdinalNumber() {
    this(0);
  }

  /**
   * コンストラクタ．初期値を与えてインスタンスを生成する．
   * 
   * @param initialValue 初期値
   */
  public OrdinalNumber(final int initialValue) {
    super(initialValue);
  }

  /**
   * 序数の文字列表現を返す．
   * 
   * @return 序数の文字列表現
   */
  @Override
  public String toString() {

    final int cardinalNumber = this.get();

    // "st"をつける場合．11は対象外．
    if ((cardinalNumber % 10 == 1) && (cardinalNumber % 100 != 11)) {
      return cardinalNumber + "st";
    }

    // "nd"をつける場合．12は対象外．
    else if ((cardinalNumber % 10 == 2) && (cardinalNumber % 100 != 12)) {
      return cardinalNumber + "nd";
    }

    // "rd"をつける場合．13の場合は対象外．
    else if ((cardinalNumber % 10 == 3) && (cardinalNumber % 100 != 13)) {
      return cardinalNumber + "rd";
    }

    // "th"をつける場合．上記の以外すべて．
    else {
      return cardinalNumber + "th";
    }
  }
}
