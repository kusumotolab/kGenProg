package example;

public class Foo {

  public int foo(int n) {
    new Bar(); // ソースがなくバイナリのみ存在するBarへ依存
    return n - 1;
  }

  public int foo2() {
    external.Baz baz = new external.Baz(); // jarライブラリへの依存
    return baz.baz(); // 一応メソッドを呼び出し（中身は0を返すだけのメソッド）
  }
}
