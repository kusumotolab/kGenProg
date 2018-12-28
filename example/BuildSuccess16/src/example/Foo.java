package example;

public class Foo {

  public int foo(int n) {
    new Bar(); // ソースがなくバイナリのみ存在するBarへ依存
    return n-1;
  }
}
