package example;

public class CloseToZero {

  /**
   * 整数をゼロに一つ近づけるメソッド
   * 
   * bug: 条件式が反転している．
   * 
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n == 0) {
      // do nothing
    } else if (n < 0) { // bug here
      n--;
    } else {
      n++;
    }
    return n;
  }

  // 再利用されるべきメソッド1
  public int reuse_me1(int n) {
    if (n == 0) {
    } else if (n > 0) {
      n++;
    }
    return 0;
  }

}
