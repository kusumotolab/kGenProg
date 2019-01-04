package example;

public class GreatestCommonDivider {

  /**
   * 2つの数の最大公約数を返すメソッド
   * 
   * Automatically finding patches using genetic programming (ICSE 2009)
   * に例題として出てくるプログラムをJava言語用にアレンジ．
   * 
   * @param a
   * @param b
   * @return
   */
  public int gcd(int a, int b) {

    if (a == 0) {
      return 0; // to be "return b"
    }

    while (b != 0) {
      if (a > b) {
        a = a - b;
      } else {
        b = b - a;
      }
    }

    return a;
  }

  @SuppressWarnings("unused")
  private int reuse_me(int a, int b) {
    if (a > b) {
      return a;
    } else if (a < b) {
      return b;
    } else {
      return 0;
    }
  }
}
