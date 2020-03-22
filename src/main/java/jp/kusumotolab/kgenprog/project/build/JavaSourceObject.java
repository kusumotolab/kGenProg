package jp.kusumotolab.kgenprog.project.build;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

/**
 * コンパイル元となるJavaのソースコードを表すオブジェクト．<br>
 * 差分コンパイル時の重複回避のために，fqnとastから得られるdigest情報をキーとして保持する．<br>
 *
 * @author shinsuke
 */
public class JavaSourceObject extends SimpleJavaFileObject {

  private final FullyQualifiedName fqn;
  private final String sourceCode;
  private final SourcePath sourcePath;
  private final String digest;
  private final boolean isTest;

  /**
   * constructor<br>
   * astからJSOを生成する．
   *
   * @param ast 生成元となるast
   */
  public JavaSourceObject(final GeneratedAST<?> ast) {
    this(ast.getPrimaryClassName(), ast.getSourceCode(), ast.getMessageDigest(),
        ast.getSourcePath());
  }

  /**
   * constructor<br>
   * ソースコードファイルからJSOを生成する．
   *
   * @param fqn ソースコードのfqn
   * @param sourceCode ソースコードのbody
   * @param digest メッセージダイジェスト
   * @param sourcePath パス情報
   */
  private JavaSourceObject(final FullyQualifiedName fqn, final String sourceCode,
      final String digest, final SourcePath sourcePath) {
    super(URI.create("jso:///" + fqn.value.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);

    this.fqn = fqn;
    this.sourceCode = sourceCode;
    this.digest = digest;
    this.sourcePath = sourcePath;
    this.isTest = sourcePath.getClass() == TestSourcePath.class;
  }

  public String getMessageDigest() {
    return digest;
  }

  public SourcePath getSourcePath() {
    return sourcePath;
  }

  public final FullyQualifiedName getFqn() {
    return fqn;
  }

  public boolean isTest() {
    return isTest;
  }

  @Override
  public final CharSequence getCharContent(final boolean ignoreEncodingErrors) {
    return sourceCode;
  }

  @Override
  public final String getName() {
    return fqn.value;
  }

  @Override
  public final String toString() {
    return fqn + "#" + digest.substring(0, 4);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final JavaSourceObject that = (JavaSourceObject) o;
    return getKey().equals(that.getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  // equals()とhashCode()に用いるオブジェクト自体のユニークなキー
  private String getKey() {
    return fqn + "#" + sourceCode;
  }

}
