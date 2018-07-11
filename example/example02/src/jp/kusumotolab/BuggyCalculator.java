package jp.kusumotolab;
public class BuggyCalculator {
  public int close_to_zero(  int n){
    if (n > 0) {
      n--;
    }
 else {
      n++;
    }
    return n;
  }
}
