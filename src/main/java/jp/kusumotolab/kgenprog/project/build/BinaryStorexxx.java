package jp.kusumotolab.kgenprog.project.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class BinaryStorexxx {

  // TODO ひとまずシングルトン化．真面目に考えてないので要検討
  public static BinaryStorexxx instance = new BinaryStorexxx();

  private Map<BinaryStoreKey, Set<JavaFileObject>> cache;

  public BinaryStorexxx() {
    cache = new HashMap<>();
  }

  public void put(final BinaryStoreKey key, JavaFileObject object) {
    if (null == cache.get(key)) {
      cache.put(key, new HashSet<>());
    }
    cache.get(key)
        .add(object);
  }

  public Set<JavaFileObject> get(final BinaryStoreKey key) {
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    return null; // TODO Is emptyList better?
  }

  public List<JavaFileObject> getAll() {
    return cache.values()
        .stream()
        .flatMap(Set::stream)
        .collect(Collectors.toList());
  }

  public Iterable<JavaFileObject> list(final String packageName) {
    return cache.values()
        .stream()
        .flatMap(Collection::stream)
        .filter(jfo -> jfo.getName()
            .startsWith("/" + packageName)) // TODO: スラッシュ開始で決め打ち．uriからの変換なので間違いないとは思う
        .collect(Collectors.toList());
  }

  public void removeAll() {
    cache.clear();
  }

}
