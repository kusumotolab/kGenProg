package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * example/BuildSuccess01-03に対応するクラス名等のエイリアス． <br>
 * テストのためのユーティリティクラス．
 * 
 * @author shinsuke
 *
 */
public class ExampleAlias {

  // Src（パス）へのエイリアス
  public static class Src {

    public final static Path Foo = Paths.get("src/example/Foo.java");
    public final static Path Bar = Paths.get("src/example/Bar.java");
    public final static Path Baz = Paths.get("src/example/Baz.java");

    public final static Path FooTest = appendTest(Foo);
    public final static Path BarTest = appendTest(Bar);
    public final static Path BazTest = appendTest(Baz);
  }

  // Binへのエイリアス
  public static class Bin {

    public final static Path Foo = sourceToBin(Src.Foo);
    public final static Path Bar = sourceToBin(Src.Bar);
    public final static Path Baz = sourceToBin(Src.Baz);

    public final static Path FooTest = sourceToBin(Src.FooTest);
    public final static Path BarTest = sourceToBin(Src.BarTest);
    public final static Path BazTest = sourceToBin(Src.BazTest);
  }

  // Fqnへのエイリアス
  public static class Fqn {

    public final static FullyQualifiedName Foo = pathToFqn(Src.Foo);
    public final static FullyQualifiedName Bar = pathToFqn(Src.Bar);
    public final static FullyQualifiedName Baz = pathToFqn(Src.Baz);

    public final static FullyQualifiedName FooTest = pathToFqn(Src.FooTest);
    public final static FullyQualifiedName BarTest = pathToFqn(Src.BarTest);
    public final static FullyQualifiedName BazTest = pathToFqn(Src.BazTest);

    public final static FullyQualifiedName FooTest01 = appendFqn(FooTest, ".test01");
    public final static FullyQualifiedName FooTest02 = appendFqn(FooTest, ".test02");
    public final static FullyQualifiedName FooTest03 = appendFqn(FooTest, ".test03");
    public final static FullyQualifiedName FooTest04 = appendFqn(FooTest, ".test04");

    public final static FullyQualifiedName BarTest01 = appendFqn(BarTest, ".test01");
    public final static FullyQualifiedName BarTest02 = appendFqn(BarTest, ".test02");
    public final static FullyQualifiedName BarTest03 = appendFqn(BarTest, ".test03");
    public final static FullyQualifiedName BarTest04 = appendFqn(BarTest, ".test04");
    public final static FullyQualifiedName BarTest05 = appendFqn(BarTest, ".test05");

    public final static FullyQualifiedName BazInner = appendFqn(Baz, "$InnerClass");
    public final static FullyQualifiedName BazStaticInner = appendFqn(Baz, "$StaticInnerClass");
    public final static FullyQualifiedName BazAnonymous = appendFqn(Baz, "$1");
    public final static FullyQualifiedName BazOuter = pathToFqn(Paths.get("example/OuterClass"));

  }

  // ライブラリへのエイリアス
  public final static class Lib {

    public final static Path Junit = Paths.get("lib/junit4/junit-4.12.jar");
    public final static Path Hamcrest = Paths.get("lib/junit4/hamcrest-core-1.3.jar");
  }


  private static Path sourceToBin(final Path path) {
    final String pathName = path.toString();
    return Paths.get(pathName.replace("src" + File.separator, "") // binファイルはsrcディレクトリ外
        .replace(".java", ".class")); // 拡張子の修正
  }

  private static Path appendTest(final Path path) {
    final String pathName = path.toString();
    return Paths.get(pathName.replace(".java", "Test.java"));
  }

  private static FullyQualifiedName pathToFqn(final Path path) {
    final String pathName = path.toString();
    final String fqn = pathName.replace("src" + File.separator, "") // srcディレクトリ指定を削除
        .replace(".java", "") // 拡張子を削除
        .replace(File.separator, "."); // セパレータを.に置換

    if (fqn.endsWith("Test")) {
      return new TestFullyQualifiedName(fqn);
    }
    return new TargetFullyQualifiedName(fqn);
  }


  private static FullyQualifiedName appendFqn(final FullyQualifiedName fqn, final String method) {
    final String appendedFqn = fqn.value.concat(method);

    if (fqn.value.endsWith("Test")) {
      return new TestFullyQualifiedName(appendedFqn);
    }
    return new TargetFullyQualifiedName(appendedFqn);
  }
}
