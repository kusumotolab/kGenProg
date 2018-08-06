package example;

public class CloseToZero {

  /**
   * 整数をゼロに一つ近づけるメソッド
   * 
   * bug: 0を与えたときに0であるべきが1になる
   * 
   * fix1: 'if(n==0){return n;}' をメソッド冒頭に追加<br>
   * fix2: 'if(n==0){return n;}' をelseブロックの冒頭に追加<br>
   * fix3: 'if(n==0){n--;};' をelseブロックの冒頭に追加<br>
   * fix4: 'else if(n==0){}' をif-elseの間に追加
   * 
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n > 0) {
      n--;
    } else {
      n++;
    }
    return n;
  }

  // 再利用されるべきメソッド1
  public int reuse_me1(int n) {
    if (n == 0) { // fix1とfix2で再利用されるStatement
      return n;
    }
    return 0;
  }

  // 再利用されるべきメソッド2
  public void reuse_me2(int n) {
    if (n == 0) { // fix3で再利用されるStatement
      n--;
    }
  }

  // 再利用されるべきメソッド3
  public void reuse_me3(int n) {
    if (n > 0) {
    } else if (n == 0) { // fix4で再利用されるStatement
    }
  }

  // 再利用されても意味のないメソッド群
  public void reuse_me_fake(int n) {
    int i = 0;
    i++;
    n += i;
  }

}
