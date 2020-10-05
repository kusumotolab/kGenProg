package example;

public class Foo {

  public void a(int i) {
    if (n > 0) {
      n--;        // locA
    } else {
      n++;        // locB
    }
    return n;     // locC
  }
}
