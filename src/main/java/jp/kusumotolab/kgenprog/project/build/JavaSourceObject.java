package jp.kusumotolab.kgenprog.project.build;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import com.google.common.base.Objects;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * Stores Java source code from a String into a JavaFileObject.
 * 
 * This class is responsible for creating a Java File Object from a string containing the Java
 * source code.
 */
public class JavaSourceObject extends SimpleJavaFileObject {

  private final String code;
  private final String className;
  private final SourcePath sourcePath;
  private String digest;

  public JavaSourceObject(final GeneratedAST<? extends SourcePath> ast) {
    this(ast.getPrimaryClassName(), ast.getSourceCode(), ast.getMessageDigest(),
        ast.getSourcePath());
  }

  private JavaSourceObject(final String className, final String javaSourceCode, final String digest,
      final SourcePath sourcePath) {
    super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension),
        Kind.SOURCE);
    this.className = className;
    this.code = javaSourceCode;
    this.digest = digest;
    this.sourcePath = sourcePath;
  }

  public SourcePath getSourcePath() {
    return sourcePath;
  }

  public String getMessageDigest() {
    return digest;
  }

  @Override
  public final CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }

  @Override
  public final String getName() {
    return className;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JavaSourceObject that = (JavaSourceObject) o;
    return Objects.equal(code, that.code) && Objects.equal(className, that.className);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(code, className);
  }

  @Override
  public final String toString() {
    return className + "#" + digest.substring(0, 4);
  }

}
