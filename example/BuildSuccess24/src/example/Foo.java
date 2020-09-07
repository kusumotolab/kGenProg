package example;

public class Foo {

  /**
   * 2つの整数のうち大きい整数を返す
   *
   * @param n 整数
   * @param m 整数
   * @return n, mのうち大きい整数
   */
  public int max(int n, int m) {
    if (n < m) {
      return m;
    } else {
      return n;
    }
  }
}
