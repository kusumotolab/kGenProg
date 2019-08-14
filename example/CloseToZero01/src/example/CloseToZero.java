package example;

public class CloseToZero {

  /**
   * 整数をゼロに一つ近づけるメソッド
   * 
   * bug: 0を与えたときに0であるべきが1になる
   * 
   * fix1: バグ行を削除<br>
   * fix2: バグ行の次に 'n--;' を追加<br>
   * fix3: バグ行を 'return n;' で置換
   * 
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n == 0) {
      n++; // bug here
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    return n;
  }
}
