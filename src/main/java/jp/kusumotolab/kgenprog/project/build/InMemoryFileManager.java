package jp.kusumotolab.kgenprog.project.build;


import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import com.google.common.collect.Iterables;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestFullyQualifiedName;

/**
 * The standard JavaFileManager uses a simple implementation of type JavaFileObject to read/write
 * bytecode into class files. This class extends the standard JavaFileManager to read/write bytecode
 * into memory using a custom implementation of the JavaFileObject.
 * 
 * @see JavaBinaryObject
 */
public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

  // ビルド結果を格納するStore
  private final BinaryStore binaryStore;

  // クラスパスの解決に用いられるJBOの集合．ビルドごとに書き換えられる．
  private Set<JavaBinaryObject> classPathBinaries;

  public InMemoryFileManager(final JavaFileManager fileManager, final BinaryStore binaryStore) {
    super(fileManager);
    this.binaryStore = binaryStore;
    this.classPathBinaries = Collections.emptySet();
  }

  public void setClassPathBinaries(final Set<JavaBinaryObject> classPathBinaries) {
    this.classPathBinaries = classPathBinaries;
  }


  @Override
  public JavaFileObject getJavaFileForOutput(final Location location, final String name,
      final Kind kind, final FileObject sibling) throws IOException {

    if (null == sibling || !(sibling instanceof JavaSourceObject) || !kind.equals(Kind.CLASS)) {
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
   * ビルド中に呼ばれるメソッド．依存クラス等の解決に利用される．
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
    final Iterable<JavaFileObject> cache = classPathBinaries.stream()
        .filter(jbo -> jbo.getFqn().value.startsWith(packageName))
        .collect(Collectors.toList());

    // TODO location考えなくて良い？

    // 両方を結合して返す
    return Iterables.concat(objs, cache);
  }

  /**
   * inferBinaryNameの拡張．JavaMemoryObjectの場合のみ特殊処理．
   * 
   * @see javax.tools.ForwardingJavaFileManager#inferBinaryName(javax.tools.JavaFileManager.Location,
   *      javax.tools.JavaFileObject)
   * @throws IllegalStateException {@inheritDoc}
   */
  @Override
  public String inferBinaryName(final Location location, final JavaFileObject file) {
    // JMOの場合はバイナリ名の解決を簡略化 ．
    // 標準FMにinferするとIllegalArgumentExceptionが発生するため（理由は不明）．
    if (file instanceof JavaBinaryObject) { // TODO ここ重い
      return ((JavaBinaryObject) file).getFqn().value;
    }
    return fileManager.inferBinaryName(location, file);
  }

  @Override
  public FileObject getFileForInput(final Location location, final String packageName,
      final String relativeName) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JavaFileObject getJavaFileForInput(final Location location, final String className,
      final Kind kind) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSameFile(final FileObject a, final FileObject b) {
    return false;
  }


}
