package example;

public class Foo {

  public int foo(int n) {
    if (n == 0) {
      n = Bar.bar2(n); // bug here
    } else if (n > 0) {
      n = Bar.bar2(n);
    } else {
      n = Bar.bar1(n);
    }
    return n;
  }
}
