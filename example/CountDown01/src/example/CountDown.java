package example;

public class CountDown {

  /**
   * 整数をゼロに一つ近づけるメソッド
   *
   * bug: if文の条件式の演算子が<であるべき
   *
   * @param n
   * @return
   */
  public int countDown(int n) {
    if (0 <= n) { // bug here
      n--;
    }
    return n;
  }

  // 再利用されるべきメソッド1
  public int doNothing(int n) {
    while (0 < n) {
    }
    return n;
  }
}
