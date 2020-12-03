package example;

public class CloseToZero {

  public int close_to_zero(int n) {
    if (n == 0) {
      k++; // build failure
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    return n;
  }
}
