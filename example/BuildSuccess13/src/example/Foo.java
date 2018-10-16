package example;

public class Foo {

  public void foo() {
    new Bar(); // ソースがなくバイナリのみ存在するBarへ依存
  }
}
