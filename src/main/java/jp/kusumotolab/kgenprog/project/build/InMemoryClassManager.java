package jp.kusumotolab.kgenprog.project.build;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import com.google.common.collect.Iterables;

/**
 * The standard JavaFileManager uses a simple implementation of type JavaFileObject to read/write
 * bytecode into class files. This class extends the standard JavaFileManager to read/write bytecode
 * into memory using a custom implementation of the JavaFileObject.
 * 
 * @see JavaMemoryObject
 */
public class InMemoryClassManager extends ForwardingJavaFileManager<JavaFileManager> {

  private List<CompilationUnit> memory = new ArrayList<>();
  private BinaryStore binaryStore = BinaryStore.instance;

  public InMemoryClassManager(JavaFileManager fileManager) {
    super(fileManager);
  }

  @Override
  public FileObject getFileForInput(JavaFileManager.Location location, String packageName,
      String relativeName) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className,
      JavaFileObject.Kind kind) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String name, Kind kind,
      FileObject sibling) throws IOException {

    if (!(sibling instanceof JavaFileObjectFromString)) {
      throw new UnsupportedOperationException(); // TODO
    }
    String hash = ((JavaFileObjectFromString) sibling).getMessageDigest();
    String _name = ((JavaFileObjectFromString) sibling).getName();

    JavaMemoryObject co = new JavaMemoryObject(name, kind);
    CompilationUnit cf = new CompilationUnit(name, co);
    memory.add(cf);
    binaryStore.put(new BinaryStoreKey(_name, hash), co); // TODO temporaly
    return co;
  }

  @Override
  public boolean isSameFile(FileObject a, FileObject b) {
    return false;
  }

  /**
   * Gets the bytecode as a list of compiled classes. If the source code generates inner classes,
   * these classes will be placed in front of the returned list and the class associated to the
   * source file will be the last element in the list.
   * 
   * @return List of compiled classes
   */
  public List<CompilationUnit> getAllClasses() {
    return memory;
  }

  /**
   * ビルド中に呼ばれるメソッド．依存クラス等の解決に利用される．
   * 
   * @see javax.tools.ForwardingJavaFileManager#list(javax.tools.JavaFileManager.Location,
   *      java.lang.String, java.util.Set, boolean)
   */
  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
      boolean recurse) throws IOException {
    // まずは普通のFMからバイナリを取り出す
    Iterable<JavaFileObject> objs = fileManager.list(location, packageName, kinds, recurse);

    // BinaryStoreからもバイナリを取り出す
    Iterable<JavaFileObject> cache = binaryStore.list(packageName);

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
  public String inferBinaryName(Location location, JavaFileObject file) {
    // JMOの場合はバイナリ名の解決を簡略化 ．
    // 標準FMにinferするとIllegalArgumentExceptionが発生するため（理由は不明）．
    if (file instanceof JavaMemoryObject) {
      return ((JavaMemoryObject) file).getBinaryName();
    }
    return fileManager.inferBinaryName(location, file);
  }
}
