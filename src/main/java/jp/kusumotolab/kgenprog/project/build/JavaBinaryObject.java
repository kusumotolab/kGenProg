package jp.kusumotolab.kgenprog.project.build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import com.google.common.base.Objects;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JavaBinaryObject implements JavaFileObject {

  private final String primaryKey;
  private final String fqn;
  private final Kind kind;
  private final String digest;
  private final SourcePath path;
  private final URI uri;
  private final ByteArrayOutputStream bos;

  public JavaBinaryObject(final String primaryKey, final String fqn, final Kind kind, final String digest, final SourcePath path) {
    this.primaryKey = primaryKey;
    this.fqn = fqn;
    this.kind = kind;
    this.digest = digest;
    this.uri = URI.create("jmo:///" + fqn.replace('.', '/') + kind.extension);
    this.path = path;
    this.bos = new ByteArrayOutputStream();
  }
  
  public String getPrimaryKey() {
    return primaryKey;
  }
  /**
   * InMemoryClassManager#inferBinaryNameで呼ばれるメソッド． inferする必要がないので直接binaryNameを返す．
   * 
   * @return
   */
  public String getBinaryName() {
    return fqn;
  }

  public SourcePath getPath() {
    return path;
  }
  
  public byte[] getByteCode() {
    return bos.toByteArray();
  }

  @Override
  public final URI toUri() {
    return uri;
  }

  @Override
  public final String getName() {
    return uri.getPath();
  }

  @Override
  public final InputStream openInputStream() throws IOException {
    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public final OutputStream openOutputStream() throws IOException {
    return bos;
  }

  @Override
  public final Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public final CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public final Writer openWriter() throws IOException {
    return new OutputStreamWriter(openOutputStream());
  }

  @Override
  public final long getLastModified() {
    return 0L;
  }

  @Override
  public final boolean delete() {
    return false;
  }

  @Override
  public final Kind getKind() {
    return kind;
  }

  @Override
  public final boolean isNameCompatible(final String simpleName, final Kind fileKind) {
    final String baseName = simpleName + kind.extension;
    return fileKind.equals(getKind()) && (baseName.equals(toUri().getPath()) || toUri().getPath()
        .endsWith("/" + baseName));
  }

  @Override
  public final NestingKind getNestingKind() {
    return null;
  }

  @Override
  public final Modifier getAccessLevel() {
    return null;
  }

  @Override
  public final String toString() {
    return fqn + "#" + digest.substring(0, 4);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final JavaBinaryObject that = (JavaBinaryObject) o;
    return Objects.equal(fqn, that.fqn) && Objects.equal(digest, that.digest);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fqn, digest);
  }
  
}
