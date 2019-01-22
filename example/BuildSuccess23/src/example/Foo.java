package example;

public class Foo {

  public void foo() throws ClassNotFoundException {
    this.getClass()
        .getClassLoader()
        .loadClass("jp.kusumotolab.kgenprog.CUILauncher");
  }

}
