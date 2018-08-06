package example;

public class CloseToZero {

  /**
   * 整数をゼロに一つ近づけるメソッド
   * 
   * bug: 0以外の値の時に期待値+1が返ってきてしまう
   * 
   * fix1: バグ行を削除<br>
   * fix2: バグ行の次に 'n--;' を追加<br>
   * 
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n == 0) {
      return 0;
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    n++; // bug here
    return n;
  }

}
