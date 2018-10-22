package jp.kusumotolab.kgenprog.project.build;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import com.google.common.base.Objects;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

/**
 * コンパイル元となるJavaのソースコードを表すオブジェクト．<br>
 * 
 */
public class JavaSourceObject extends SimpleJavaFileObject {

  private final String sourceCode;
  private final String fqn;
  private final SourcePath sourcePath;
  private final String digest;
  private final boolean isTest;

  public JavaSourceObject(final GeneratedAST<? extends SourcePath> ast) {
    this(ast.getPrimaryClassName(), ast.getSourceCode(), ast.getMessageDigest(),
        ast.getSourcePath());
  }

  private JavaSourceObject(final String fqn, final String sourceCode, final String digest,
      final SourcePath sourcePath) {
    super(URI.create("jso:///" + fqn.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.fqn = fqn;
    this.sourceCode = sourceCode;
    this.digest = digest;
    this.sourcePath = sourcePath;
    this.isTest = sourcePath instanceof TestSourcePath ? true : false;
  }

  public String getMessageDigest() {
    return digest;
  }

  public SourcePath getSourcePath() {
    return sourcePath;
  }

  public boolean isTest() {
    return isTest;
  }

  @Override
  public final CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return sourceCode;
  }

  @Override
  public final String getName() {
    return fqn;
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
    return Objects.equal(sourceCode, that.sourceCode) && Objects.equal(fqn, that.fqn);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(sourceCode, fqn);
  }

  @Override
  public final String toString() {
    return fqn + "#" + digest.substring(0, 4);
  }

}
