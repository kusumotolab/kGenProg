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

/**
 * Wraps bytecode into memory.
 * 
 * The JavaCompiler uses the StandardFileManager to perform read/write bytecode on files of type
 * JavaFileObject. This class extends this functionality by reading/writing bytecode into memory
 * instead of files.
 */
public class JavaMemoryObject implements JavaFileObject {

  private ByteArrayOutputStream bos;
  private URI uri;
  private Kind kind;
  private String binaryName;

  /**
   * 書き込み用FileObjectの生成コンストラクタ．ビルド結果の書き込みに用いられる．
   * 
   * @param fileName
   * @param fileKind
   */
  public JavaMemoryObject(final String fileName, final Kind fileKind) {
    this.binaryName = fileName;
    this.uri = URI.create("jmo:///" + fileName.replace('.', '/') + fileKind.extension);
    this.kind = fileKind;
    bos = new ByteArrayOutputStream();
  }

  /**
   * InMemoryClassManager#inferBinaryNameで呼ばれるメソッド． inferする必要がないので直接binaryNameを返す．
   * 
   * @return
   */
  public String getBinaryName() {
    return binaryName;
  }

  public byte[] getClassBytes() {
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
  public final boolean isNameCompatible(String simpleName, Kind fileKind) {
    String baseName = simpleName + kind.extension;
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
    return getClass().getName() + "[" + toUri() + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JavaMemoryObject that = (JavaMemoryObject) o;
    return Objects.equal(uri, that.uri) && Objects.equal(kind, that.kind);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uri, kind);
  }
}
