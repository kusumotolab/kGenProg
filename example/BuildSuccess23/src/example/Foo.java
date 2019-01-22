package example;

public class Foo {

  // 指定FQNのクラスをロードする
  public void load(final String name) throws ClassNotFoundException {
    this.getClass()
        .getClassLoader()
        .loadClass(name);
  }

}
