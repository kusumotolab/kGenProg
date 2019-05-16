package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * RxJava を用いて実装された Variant
 * テスト実行などを非同期で行う
 * 現在 kGenProg が使用しているのはこのクラス
 * TODO: 将来的に Variant を消す
 *
 * @see Variant
 */
public class LazyVariant extends Variant {

  private Single<TestResults> testResultsSingle;
  private Single<Fitness> fitnessSingle;
  private Single<List<Suspiciousness>> suspiciousnessListSingle;

  /**
   * @param id この個体の識別子
   * @param generationNumber この個体の世代数
   * @param gene この個体の遺伝子
   * @param generatedSourceCode この個体のソースコード
   * @param historicalElement この個体がが生成されるまでの過程
   */
  public LazyVariant(final long id, final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final HistoricalElement historicalElement) {
    super(id, generationNumber, gene, generatedSourceCode, null, null, null,
        historicalElement);
  }

  /**
   * このメソッドを呼び出すことで，テストを実行する
   * テストを実行するスレッドは testResultsSingle のスレッドに依存
   */
  void subscribe() {
    if (testResultsSingle == null) {
      return;
    }
    testResultsSingle.subscribe();
  }

  /**
   * @param testResultsSingle テスト処理を持った Single
   */
  void setTestResultsSingle(
      final Single<TestResults> testResultsSingle) {
    this.testResultsSingle = testResultsSingle;
  }

  /**
   * @param fitnessSingle 評価処理を持った Single
   */
  void setFitnessSingle(
      final Single<Fitness> fitnessSingle) {
    this.fitnessSingle = fitnessSingle;
  }

  /**
   * @param suspiciousnessListSingle 疑惑値を算出する処理を持った Single
   */
  void setSuspiciousnessListSingle(
      final Single<List<Suspiciousness>> suspiciousnessListSingle) {
    this.suspiciousnessListSingle = suspiciousnessListSingle;
  }

  /**
   * @return この個体が解かどうか
   */
  @Override
  public boolean isCompleted() {
    return fitnessSingle.blockingGet()
        .isMaximum();
  }

  /**
   * @return ビルドに成功したかどうか
   */
  @Override
  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResultsSingle.blockingGet()
        .getClass();
  }

  /**
   * @return この個体のテスト結果
   */
  @Override
  public TestResults getTestResults() {
    return this.testResultsSingle.blockingGet();
  }

  /**
   * @return この個体の評価値
   */
  @Override
  public Fitness getFitness() {
    return this.fitnessSingle.blockingGet();
  }

  /**
   * @return この個体の疑惑値
   */
  @Override
  public List<Suspiciousness> getSuspiciousnesses() {
    return this.suspiciousnessListSingle.blockingGet();
  }
}
