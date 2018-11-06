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
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * コンパイル後のJavaバイナリオブジェクトクラス．<br>
 * バイナリ自体のFQNやバイナリ生成元となったファイルのPath等，翻訳に必要な情報全てを内包する．<br>
 * 
 * @author shinsuke
 *
 */
public class JavaBinaryObject implements JavaFileObject {

  private final FullyQualifiedName fqn;
  private final FullyQualifiedName originFqn;
  private final String originDigest;
  private final SourcePath originPath;
  private final boolean isTest;
  private final Kind kind;
  private final URI uri;
  private final ByteArrayOutputStream bos;
  private final long lastModified;

  /**
   * @param fqn バイナリ自体のFQN
   * @param originFqn バイナリ生成元のFQN（基本はfqn=originFqnだが内部クラスの場合特殊）
   * @param originDigest バイナリ生成元のダイジェスト
   * @param originPath バイナリ生成元のファイルパス
   * @param isTest テストか否か
   */
  public JavaBinaryObject(final FullyQualifiedName fqn, final FullyQualifiedName originFqn,
      final String originDigest, final SourcePath originPath, final boolean isTest) {
    this.fqn = fqn;
    this.originFqn = originFqn;
    this.originDigest = originDigest;
    this.originPath = originPath;
    this.isTest = isTest;

    this.kind = Kind.CLASS; // それ以外ありえないので決め打ち
    this.uri = URI.create("jmo:///" + fqn.value.replace('.', '/') + kind.extension);

    this.bos = new ByteArrayOutputStream(); // バイナリ情報の格納先
    this.lastModified = System.currentTimeMillis(); // 生成時刻
  }

  public FullyQualifiedName getOriginFqn() {
    return originFqn;
  }

  public String getOriginDigest() {
    return originDigest;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }

  public SourcePath getOriginPath() {
    return originPath;
  }

  public byte[] getByteCode() {
    return bos.toByteArray();
  }

  public boolean isTest() {
    return isTest;
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
    return lastModified;
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
    return fqn + "#" + originDigest.substring(0, 4);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final JavaBinaryObject that = (JavaBinaryObject) o;
    return getKey().equals(that.getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  // equals()とhashCode()に用いるオブジェクト自体のユニークなキー
  private String getKey() {
    return fqn + "#" + originDigest;
  }

}
