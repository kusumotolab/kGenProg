package jp.kusumotolab.kgenprog.project.build;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import com.google.common.base.Objects;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestFullyQualifiedName;

/**
 * コンパイル元となるJavaのソースコードを表すオブジェクト．<br>
 * 
 */
public class JavaSourceObject extends SimpleJavaFileObject {

  private final FullyQualifiedName fqn;
  private final String sourceCode;
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
    this.isTest = sourcePath instanceof TestSourcePath ? true : false;
    this.fqn = isTest ? new TestFullyQualifiedName(fqn) : new TargetFullyQualifiedName(fqn);
    this.sourceCode = sourceCode;
    this.digest = digest;
    this.sourcePath = sourcePath;
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
    return fqn.value;
  }

  public final FullyQualifiedName getFqn() {
    return fqn;
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
