package jp.kusumotolab.kgenprog;

import java.util.concurrent.atomic.AtomicInteger;

public class OrdinalNumber extends AtomicInteger {

  private static final long serialVersionUID = 1L;

  public OrdinalNumber() {
    this(0);
  }

  public OrdinalNumber(final int initialValue) {
    super(initialValue);
  }

  /**
   * 序数の文字列表現を返す
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
