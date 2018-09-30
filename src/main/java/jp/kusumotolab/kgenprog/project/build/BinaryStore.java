package jp.kusumotolab.kgenprog.project.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

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

  private Map<FullyQualifiedName, JavaFileObject> cache;

  private BinaryStore() {
    cache = new HashMap<>();
  }

  public void put(final FullyQualifiedName fqn, JavaFileObject object) {
    cache.put(fqn, object);
  }

  public JavaFileObject get(final FullyQualifiedName fqn) {
    if (cache.containsKey(fqn)) {
      return cache.get(fqn);
    }
    return null;
  }

  public Iterable<JavaFileObject> list(final String packageName) {
    return cache.values()
        .stream()
        .filter(fo -> fo.getName()
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
