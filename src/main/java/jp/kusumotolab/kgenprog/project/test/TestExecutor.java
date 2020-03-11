package jp.kusumotolab.kgenprog.project.test;

import io.reactivex.Single;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * テスト実行インタフェース．<br>
 *
 * @author shinsuke
 */
public interface TestExecutor {

  /**
   * テスト実行を行う．<br>
   *
   * @param variant 実行対象のソースコードを保持するVariant
   * @return テスト結果
   */
  TestResults exec(final Variant variant);

  default Single<TestResults> execAsync(final Single<Variant> variantSingle) {
    return variantSingle.map(this::exec);
  }

  default void initialize() {
  }

  default void finish() {
  }
}
