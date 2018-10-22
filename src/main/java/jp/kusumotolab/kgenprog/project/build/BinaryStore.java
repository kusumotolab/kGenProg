package jp.kusumotolab.kgenprog.project.build;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * 差分ビルド + インメモリビルドのためのバイナリ格納庫．<br>
 * FQNをキーとしてビルド結果であるJavaMemoryObjectバイナリをメモリ上にキャッシュする． <br>
 * 
 * ref jsr107 https://static.javadoc.io/javax.cache/cache-api/1.0.0/javax/cache/package-summary.html
 * 
 * @author shin
 *
 */
public class BinaryStore {

  private Set<JavaMemoryObject> cache;

  public BinaryStore() {
    cache = new HashSet<>();
  }

  @Deprecated
  public void add(BinaryStoreKey key, final JavaMemoryObject object) {
    cache.add(object);
  }

  public void add(final JavaMemoryObject object) {
    cache.add(object);
  }

  public Set<JavaMemoryObject> get(final BinaryStoreKey key) {
    return cache.stream()
        .filter(jmo -> jmo.getPrimaryKey().equals(key.toString()))
        .collect(Collectors.toSet());
  }
  
  public JavaMemoryObject get(final String fqn) {
    return cache.stream()
        .filter(jmo -> jmo.getBinaryName().equals(fqn))
        .findFirst().orElseThrow(RuntimeException::new);
  }

  public Set<JavaMemoryObject> get(final SourcePath path) {
    return cache.stream()
        .filter(jmo -> jmo.getPath().equals(path))
        .collect(Collectors.toSet());
  }
  
  public Set<JavaMemoryObject> getAll() {
    return cache;
  }

  public Iterable<JavaMemoryObject> list(final String packageName) {
    return cache.stream()
        .filter(jmo -> jmo.getName()
            .startsWith("/" + packageName)) // TODO: スラッシュ開始で決め打ち．uriからの変換なので間違いないとは思う
        .collect(Collectors.toList());
  }

  public void removeAll() {
    cache.clear();
  }

}
