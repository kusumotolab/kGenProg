package jp.kusumotolab.kgenprog.project.build;

import java.io.IOException;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import com.google.common.collect.Iterables;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;

/**
 * メモリ上でファイルシステムを模倣するファイルマネージャ．<br>
 * KGP高速化のためのインメモリビルドが責務．<br>
 * {@link javax.tools.JavaCompiler}が本クラスを操作し，ビルド結果の書き出しや依存クラスの解決を行う．<br>
 * 
 * @author shinsuke
 *
 */
public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

  // ビルド結果を格納するStore
  private final BinaryStore binaryStore;

  // クラスパスの解決に用いられるJBOの集合．ビルドごとに書き換えられる．
  private final BinaryStore classPathBinaries;

  /**
   * コンストラクタ
   * @param fileManager クラスパス解決に用いるファイルマネージャ
   * @param binaryStore グローバルキャッシュに用いるバイナリ集合
   * @param classPathBinaries クラスパスの解決に用いられるバイナリ集合（差分ビルド用）
   */
  public InMemoryFileManager(final JavaFileManager fileManager, final BinaryStore binaryStore,
      final BinaryStore classPathBinaries) {
    super(fileManager);
    this.binaryStore = binaryStore;
    this.classPathBinaries = classPathBinaries;
  }

  /**
   * バイナリをFMに書き出すメソッド．<br>
   * 指定された書き出し対象のバイナリオブジェクトをkgp専用のオブジェクト（JavaBinaryObject）に変換し，<br>
   * キャッシュ（BinaryStore）に格納する．
   */
  @Override
  public JavaFileObject getJavaFileForOutput(final Location location, final String name,
      final Kind kind, final FileObject sibling) throws IOException {

    if (null == sibling || sibling.getClass() != JavaSourceObject.class
        || !kind.equals(Kind.CLASS)) {
      // TODO 再現状況と対処方法は不明．一応
      throw new UnsupportedOperationException();
    }

    // JavaBinaryObjectを生成してキャッシュに保存
    final JavaBinaryObject jbo = createJavaBinaryObject(name, (JavaSourceObject) sibling);
    binaryStore.add(jbo);

    return jbo;
  }

  private JavaBinaryObject createJavaBinaryObject(final String name,
      final JavaSourceObject origin) {
    final boolean isOriginTest = origin.isTest();

    final FullyQualifiedName fqn =
        isOriginTest ? new TestFullyQualifiedName(name) : new TargetFullyQualifiedName(name);
    final String originDigest = origin.getMessageDigest();
    final FullyQualifiedName originFqn = origin.getFqn();
    final SourcePath originPath = origin.getSourcePath();

    return new JavaBinaryObject(fqn, originFqn, originDigest, originPath, isOriginTest);
  }

  /**
   * FMの保持する各種ファイルを探し出すメソッド．ビルド中に呼ばれる．<br>
   * ソースコードファイルの探索や依存解決等に利用される．
   * 
   * @see javax.tools.ForwardingJavaFileManager#list(javax.tools.JavaFileManager.Location,
   *      java.lang.String, java.util.Set, boolean)
   */
  @Override
  public Iterable<JavaFileObject> list(final Location location, final String packageName,
      final Set<Kind> kinds, final boolean recurse) throws IOException {

    // まずは普通のFMからバイナリを取り出す．標準libの解決等．
    final Iterable<JavaFileObject> objs = fileManager.list(location, packageName, kinds, recurse);

    // classPathBinariesからもバイナリを取り出す
    final Iterable<JavaBinaryObject> cache = classPathBinaries.get(packageName);

    // TODO location考えなくて良い？

    // 両方を結合して返す
    return Iterables.concat(objs, cache);
  }

  /**
   * バイナリの名前解決を行う．<br>
   * KGPで利用するJavaMemoryObjectが対象の場合に限り，特殊処理を加えている．<br>
   * 
   * @see javax.tools.ForwardingJavaFileManager#inferBinaryName(javax.tools.JavaFileManager.Location,
   *      javax.tools.JavaFileObject)
   * @throws IllegalStateException {@inheritDoc}
   */
  @Override
  public String inferBinaryName(final Location location, final JavaFileObject file) {
    // JMOの場合はバイナリ名の解決を簡略化 ．
    // 標準FMにinferするとIllegalArgumentExceptionが発生するため（inferBinaryName()の内部処理がわからないので理由は不明）．
    if (file.getClass() == JavaBinaryObject.class) {
      return ((JavaBinaryObject) file).getFqn().value;
    }
    return fileManager.inferBinaryName(location, file);
  }

  /**
   * 指定ファイルが同一オブジェクトを確認する．<br>
   * 
   * TODO 現在利用されていないので無条件で例外を吐かせておく．必要なら実装する．
   */
  @Override
  public boolean isSameFile(final FileObject a, final FileObject b) {
    throw new UnsupportedOperationException();
  }

}
