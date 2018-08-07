package jp.kusumotolab.kgenprog.project.test;

/**
 * example/BuildSuccess01-03に対応するクラス名等のエイリアス． <br>
 * テストのためのユーティリティクラス．
 * 
 * @author shinsuke
 *
 */
public class ExampleAlias {

  // aliases for tested elements
  public final static String Foo = "src/example/Foo.java";
  public final static String FooTest = appendTest(Foo);
  public final static String Bar = "src/example/Bar.java";
  public final static String BarTest = appendTest(Bar);
  public final static String Baz = "src/example/Baz.java";
  public final static String BazTest = appendTest(Baz);

  public final static FullyQualifiedName FooFqn = nameToFqn(Foo);
  public final static FullyQualifiedName FooTestFqn = nameToFqn(FooTest);
  public final static FullyQualifiedName BarFqn = nameToFqn(Bar);
  public final static FullyQualifiedName BarTestFqn = nameToFqn(BarTest);

  public final static FullyQualifiedName FooTest01Fqn = nameToFqn(FooTest, ".test01");
  public final static FullyQualifiedName FooTest02Fqn = nameToFqn(FooTest, ".test02");
  public final static FullyQualifiedName FooTest03Fqn = nameToFqn(FooTest, ".test03");
  public final static FullyQualifiedName FooTest04Fqn = nameToFqn(FooTest, ".test04");

  public final static FullyQualifiedName BarTest01Fqn = nameToFqn(BarTest, ".test01");
  public final static FullyQualifiedName BarTest02Fqn = nameToFqn(BarTest, ".test02");
  public final static FullyQualifiedName BarTest03Fqn = nameToFqn(BarTest, ".test03");
  public final static FullyQualifiedName BarTest04Fqn = nameToFqn(BarTest, ".test04");
  public final static FullyQualifiedName BarTest05Fqn = nameToFqn(BarTest, ".test05");

  public final static FullyQualifiedName BazInnerFqn = nameToFqn(Baz, "$InnerClass");
  public final static FullyQualifiedName BazStaticInnerFqn = nameToFqn(Baz, "$StaticInnerClass");
  public final static FullyQualifiedName BazAnonymousFqn = nameToFqn(Baz, "$1");
  public final static FullyQualifiedName BazOuterFqn = nameToFqn("example/OuterClass");

  public final static String Junit = "lib/junit4/junit-4.12.jar";
  public final static String Hamcrest = "lib/junit4/hamcrest-core-1.3.jar";

  private static String appendTest(final String name) {
    return name.replace(".java", "Test.java");
  }

  private static FullyQualifiedName nameToFqn(final String name) {
    return nameToFqn(name, "");
  }

  private static FullyQualifiedName nameToFqn(final String name, final String testMethodName) {
    final String fqn = name.replace("src/", "")
        .replace(".java", "")
        .replace("/", ".")
        .concat(testMethodName);

    if (fqn.endsWith("Test")) {
      return new TestFullyQualifiedName(fqn);
    }
    return new TargetFullyQualifiedName(fqn);
  }
}
