package jp.kusumotolab.kgenprog.testutil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;

/**
 * example/BuildSuccess01-03に対応するクラス名等のエイリアス． <br>
 * テストのためのユーティリティクラス．
 * 
 * @author shinsuke
 *
 */
public class ExampleAlias {

  // Srcパスへのエイリアス
  public static class Src {

    public final static Path FOO = Paths.get("src/example/Foo.java");
    public final static Path BAR = Paths.get("src/example/Bar.java");
    public final static Path BAZ = Paths.get("src/example/Baz.java");
    public final static Path QUX = Paths.get("src/example/Qux.java");

    public final static Path FOO_TEST = appendFileSuffix(FOO, "Test");
    public final static Path BAR_TEST = appendFileSuffix(BAR, "Test");
    public final static Path BAZ_TEST = appendFileSuffix(BAZ, "Test");
    public final static Path QUX_TEST = appendFileSuffix(QUX, "Test");
  }

  // Binパスへのエイリアス
  public static class Bin {

    public final static Path FOO = sourceToBin(Src.FOO);
    public final static Path BAR = sourceToBin(Src.BAR);
    public final static Path BAZ = sourceToBin(Src.BAZ);
    public final static Path QUX = sourceToBin(Src.QUX);

    public final static Path FOO_TEST = sourceToBin(Src.FOO_TEST);
    public final static Path BAR_TEST = sourceToBin(Src.BAR_TEST);
    public final static Path BAZ_TEST = sourceToBin(Src.BAZ_TEST);

    public final static Path BAZ_INNER = appendFileSuffix(BAZ, "$InnerClass");
    public final static Path BAZ_STATIC_INNER = appendFileSuffix(BAZ, "$StaticInnerClass");
    public final static Path BAZ_ANONYMOUS = appendFileSuffix(BAZ, "$1");
    public final static Path BAZ_OUTER = Paths.get("example/OuterClass.class");
  }

  // Fqnへのエイリアス
  public static class Fqn {

    public final static FullyQualifiedName FOO = pathToFqn(Src.FOO);
    public final static FullyQualifiedName BAR = pathToFqn(Src.BAR);
    public final static FullyQualifiedName BAZ = pathToFqn(Src.BAZ);
    public final static FullyQualifiedName QUX = pathToFqn(Src.QUX);

    public final static FullyQualifiedName FOO_TEST = pathToFqn(Src.FOO_TEST);
    public final static FullyQualifiedName BAR_TEST = pathToFqn(Src.BAR_TEST);
    public final static FullyQualifiedName BAZ_TEST = pathToFqn(Src.BAZ_TEST);
    public final static FullyQualifiedName QUX_TEST = pathToFqn(Src.QUX_TEST);

    public final static FullyQualifiedName FOO_TEST01 = appendFqn(FOO_TEST, ".test01");
    public final static FullyQualifiedName FOO_TEST02 = appendFqn(FOO_TEST, ".test02");
    public final static FullyQualifiedName FOO_TEST03 = appendFqn(FOO_TEST, ".test03");
    public final static FullyQualifiedName FOO_TEST04 = appendFqn(FOO_TEST, ".test04");

    public final static FullyQualifiedName BAR_TEST01 = appendFqn(BAR_TEST, ".test01");
    public final static FullyQualifiedName BAR_TEST02 = appendFqn(BAR_TEST, ".test02");
    public final static FullyQualifiedName BAR_TEST03 = appendFqn(BAR_TEST, ".test03");
    public final static FullyQualifiedName BAR_TEST04 = appendFqn(BAR_TEST, ".test04");
    public final static FullyQualifiedName BAR_TEST05 = appendFqn(BAR_TEST, ".test05");

    public final static FullyQualifiedName BAZ_INNER = appendFqn(BAZ, "$InnerClass");
    public final static FullyQualifiedName BAZ_STATIC_INNER = appendFqn(BAZ, "$StaticInnerClass");
    public final static FullyQualifiedName BAZ_ANONYMOUS = appendFqn(BAZ, "$1");
    public final static FullyQualifiedName BAZ_OUTER = pathToFqn(Bin.BAZ_OUTER);
  }

  // ライブラリへのエイリアス
  public final static class Lib {

    private final static Path TEMP = Paths.get(System.getProperty("java.io.tmpdir"));
    public final static Path JUNIT = TEMP.resolve("junit-4.12.jar");
    public final static Path HAMCREST = TEMP.resolve("hamcrest-core-1.3.jar");
  }

  private static Path appendFileSuffix(final Path path, final String suffix) {
    final String pathStr = path.toString();
    final String ext = getExtension(pathStr);
    return Paths.get(pathStr.replace(ext, suffix + ext));
  }

  private static Path sourceToBin(final Path path) {
    final String pathStr = path.toString();
    final String extension = getExtension(pathStr);
    return Paths.get(pathStr.replace("src" + File.separator, "") // binファイルはsrcディレクトリ外
        .replace(extension, ".class")); // 拡張子の修正
  }

  private static String getExtension(final String fileName) {
    return fileName.substring(fileName.lastIndexOf("."), fileName.length());
  }

  private static FullyQualifiedName pathToFqn(final Path path) {
    final String pathStr = path.toString();
    final String ext = getExtension(pathStr);
    final String fqn = pathStr.replace("src" + File.separator, "") // srcディレクトリ指定を削除
        .replace(ext, "") // 拡張子を削除
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
