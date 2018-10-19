package jp.kusumotolab.kgenprog.project.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;

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

  // TODO ひとまずシングルトン化．真面目に考えてないので要検討
  public static BinaryStore instance = new BinaryStore();

  private Map<BinaryStoreKey, JavaFileObject> cache;

  public BinaryStore() {
    cache = new HashMap<>();
  }

  public void put(final BinaryStoreKey key, JavaFileObject object) {
    cache.put(key, object);
  }

  public JavaFileObject get(final BinaryStoreKey key) {
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    return null;
  }

  public Iterable<JavaFileObject> list(final String packageName) {
    return cache.values()
        .stream()
        .filter(jfo -> jfo.getName()
            .startsWith("/" + packageName)) // TODO: スラッシュ開始で決め打ち．uriからの変換なので間違いないとは思う
        .collect(Collectors.toList());
  }

  public Collection<JavaFileObject> getAll() {
    return cache.values();
  }

  public void removeAll() {
    cache.clear();
  }

}
