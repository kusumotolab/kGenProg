package jp.kusumotolab.kgenprog.project.build;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * 差分ビルド + インメモリビルドのためのバイナリ格納庫．<br>
 * FQNとダイジェストをキーとして，ビルド結果となるJavaMemoryObjectバイナリをメモリ上にキャッシュする． <br>
 * 
 * @author shinsuke
 *
 */
public class BinaryStore {

  // 内部データ構造はただのSet．
  // TODO 現在，各クエリの実行がfilter&collectによるベタ処理なので，高速化のためには様々なキャッシュ用Mapを作る必要あり．
  private Set<JavaBinaryObject> cache;

  public BinaryStore() {
    cache = new HashSet<>();
  }

  public void add(final JavaBinaryObject object) {
    cache.add(object);
  }

  public boolean exists(final BinaryStoreKey key) {
    return cache.stream()
        .anyMatch(jbo -> jbo.getOriginKey()
            .equals(key.toString()));
  }

  public Set<JavaBinaryObject> get(final BinaryStoreKey key) {
    return cache.stream()
        .filter(jbo -> jbo.getOriginKey()
            .equals(key.toString()))
        .collect(Collectors.toSet());
  }

  public JavaBinaryObject get(final String fqn) {
    return cache.stream()
        .filter(jbo -> jbo.getFqn()
            .equals(fqn))
        .findFirst()
        .orElseThrow(RuntimeException::new);
  }

  public Set<JavaBinaryObject> get(final SourcePath path) {
    return cache.stream()
        .filter(jbo -> jbo.getOriginPath()
            .equals(path))
        .collect(Collectors.toSet());
  }

  public Set<JavaBinaryObject> getAll() {
    return cache;
  }

  public Iterable<JavaBinaryObject> list(final String packageName) {
    return cache.stream()
        .filter(jbo -> jbo.getName()
            .startsWith("/" + packageName)) // TODO: スラッシュ開始で決め打ち．uriからの変換なので間違いないとは思う
        .collect(Collectors.toList());
  }

  public void removeAll() {
    cache.clear();
  }

  public void addAll(Set<JavaBinaryObject> binaries) {
    cache.addAll(binaries);
  }

}
