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
 * 主たる責務はビルド結果となる{@link JavaBinaryObject}バイナリの集合の保持． <br>
 *
 * {@link JavaBinaryObject}が保持する情報に基づき，kgp言語とjavax.tools言語を橋渡し（翻訳）する責務も取り持つ．<br>
 * - kgp言語（SourcePath+FullyQualifiedName）<br>
 * - javax.tools言語（fqnのString）<br>
 *
 * また，保持されているバイナリはfqnやソースパス等様々なクエリで取り出しが可能．<br>
 *
 * 本クラスは2種類の使われ方がある．<br>
 * - ProjectBuilderが保持する，全ビルド結果保持用のグローバルキャッシュ（kgpプロセスで唯一）<br>
 * - BuildResultsが保持する，そのビルド実行のみの結果（ビルドのたびに生成される）<br>
 *
 * なお，本クラスで用いられるFQNには以下2種類があることに注意．<br>
 * - バイナリの生成元となったソースコードのFQN（jp.kusuomtolab.kgenprog.Configuration）<br>
 * - バイナリ自体のソースコードのFQN（jp.kusuomtolab.kgenprog.Configuration）<br>
 * ほとんどの場合，両者は同値であるが内部クラスが存在する場合に限り，1前者：多後者となる．
 *
 * @author shinsuke
 */
public class BinaryStore {

  // 全要素
  private final Set<JavaBinaryObject> cache;

  // 各要素へのアクセス高速化用map
  private final Map<FullyQualifiedName, JavaBinaryObject> fqnMap; // 1対1
  private final Multimap<SourcePath, JavaBinaryObject> pathMap; // 1対多
  private final Multimap<String, JavaBinaryObject> originMap; // 1対多

  /**
   * コンストラクタ
   */
  public BinaryStore() {
    cache = new HashSet<>();
    fqnMap = new HashMap<>();
    pathMap = HashMultimap.create();
    originMap = HashMultimap.create();
  }

  /**
   * 単一バイナリオブジェクトの格納．
   *
   * @param object 格納対象のバイナリ
   */
  public void add(final JavaBinaryObject object) {
    cache.add(object);
    fqnMap.put(object.getFqn(), object);
    pathMap.put(object.getOriginPath(), object);
    originMap.put(object.getOriginFqn() + object.getOriginDigest(), object);
  }

  /**
   * 全バイナリ集合の取得
   *
   * @return
   */
  public Collection<JavaBinaryObject> getAll() {
    return cache;
  }

  /**
   * FQN+Digestをクエリとしたバイナリ集合の取得
   *
   * @param fqn 生成元となったソースコードのFQN
   * @param digest 生成元ソースコードのMD5ハッシュ（差分ビルドのためのハッシュ情報）
   * @return バイナリ集合
   */
  public Collection<JavaBinaryObject> get(final FullyQualifiedName fqn, final String digest) {
    return originMap.get(fqn + digest);
  }

  /**
   * FQNをクエリとした単一バイナリの取得
   *
   * @param fqn バイナリのFQN
   * @return 単一バイナリ
   */
  public JavaBinaryObject get(final FullyQualifiedName fqn) {
    return fqnMap.get(fqn);
  }

  /**
   * ソースコードパスをクエリとしたバイナリ集合の取得
   *
   * @param path ソースコードパス
   * @return バイナリ集合
   */
  public Collection<JavaBinaryObject> get(final SourcePath path) {
    return pathMap.get(path);
  }

  /**
   * FQN+Digestをクエリとしたバイナリ存在有無の確認
   *
   * @param fqn 生成元となったソースコードのFQN
   * @param digest 生成元ソースコードのMD5ハッシュ（差分ビルドのためのハッシュ情報）
   * @return
   */
  public boolean exists(final FullyQualifiedName fqn, final String digest) {
    return !get(fqn, digest).isEmpty();
  }

  /**
   * パッケージ名をクエリとしたバイナリ集合の取得<br>
   * （クラスローダからの利用を想定）
   *
   * @param packageName パッケージ名
   * @return バイナリ集合
   */
  public Collection<JavaBinaryObject> get(final String packageName) {
    // パッケージ名による前方一致（≠完全一致）検索でありMapによる高速化がやりにくい．
    // また，このメソッドはBinaryStoreの部分集合のみに行われるので計算コストは低め．よって全探索で探す．
    return cache.parallelStream()
        .filter(jbo -> jbo.getFqn().value.startsWith(packageName))
        .collect(Collectors.toSet());
  }

  /**
   * 全バイナリ要素の削除
   */
  public void removeAll() {
    cache.clear();
    fqnMap.clear();
    pathMap.clear();
    originMap.clear();
  }

}
