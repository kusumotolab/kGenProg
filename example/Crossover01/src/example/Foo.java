public class Foo {
  public void a(int n) {
    if (n) {
      n = 0;      // loc0
    } else {
      n = 1;      // loc1
    }
    n = 2;        // loc2
  }
}
