package jp.kusumotolab.kgenprog.project.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * 差分ビルド + インメモリビルドのためのバイナリ格納庫．<br>
 * ビルド結果となるJavaMemoryObjectバイナリを保持する． <br>
 * 
 * @author shinsuke
 *
 */
public class BinaryStore {

  // 全要素
  final private Set<JavaBinaryObject> cache;

  // 各要素へのアクセス高速化用map
  final private Map<FullyQualifiedName, JavaBinaryObject> fqnMap; // 1対1
  final private Multimap<SourcePath, JavaBinaryObject> pathMap; // 1対多
  final private Multimap<String, JavaBinaryObject> originMap; // 1対多

  public BinaryStore() {
    cache = new HashSet<>();
    fqnMap = new HashMap<>();
    pathMap = HashMultimap.create();
    originMap = HashMultimap.create();
  }

  public void add(final JavaBinaryObject object) {
    cache.add(object);
    fqnMap.put(object.getFqn(), object);
    pathMap.put(object.getOriginPath(), object);
    originMap.put(object.getOriginFqn() + object.getOriginDigest(), object);
  }

  public Collection<JavaBinaryObject> getAll() {
    return cache;
  }

  public Collection<JavaBinaryObject> get(final FullyQualifiedName fqn, final String digest) {
    return originMap.get(fqn + digest);
  }

  public JavaBinaryObject get(final FullyQualifiedName fqn) {
    return fqnMap.get(fqn);
  }

  public Collection<JavaBinaryObject> get(final SourcePath path) {
    return pathMap.get(path);
  }

  public boolean exists(final FullyQualifiedName fqn, final String digest) {
    return !get(fqn, digest).isEmpty();
  }

  public Collection<JavaBinaryObject> get(final String packageName) {
    // パッケージ名による前方一致（≠完全一致）検索でありMapによる高速化がやりにくい．
    // また，このメソッドはBinaryStoreの部分集合のみに行われるので計算コストは低め．よって全探索で探す．
    return cache.parallelStream()
        .filter(jbo -> jbo.getFqn().value.startsWith(packageName))
        .collect(Collectors.toSet());
  }

  public void removeAll() {
    cache.clear();
    fqnMap.clear();
    pathMap.clear();
    originMap.clear();
  }

}
