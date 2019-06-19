package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 各個体に関する様々な情報を保持するクラス
 */
public class Variant {

  private final long id;
  private final int generationNumber;
  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private int selectionCount = 0;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;

  /**
   * コンストラクタ
   *
   * @param id この個体の識別子
   * @param generationNumber この個体が何世代目で生成されたか
   * @param gene この個体がもつべき遺伝子
   * @param generatedSourceCode この個体のソースコード
   * @param testResults この個体のテスト結果
   * @param fitness この個体のの評価値
   * @param suspiciousnesses この個体のソースコードに関する疑惑値
   * @param historicalElement この個体が生成されるまでの過程
   */
  public Variant(final long id, final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final TestResults testResults,
      final Fitness fitness, final List<Suspiciousness> suspiciousnesses,
      final HistoricalElement historicalElement) {
    this.id = id;
    this.generationNumber = generationNumber;
    this.gene = gene;
    this.generatedSourceCode = generatedSourceCode;
    this.testResults = testResults;
    this.fitness = fitness;
    this.suspiciousnesses = suspiciousnesses;
    this.historicalElement = historicalElement;
  }

  /**
   * @return この個体が解かどうか
   */
  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  /**
   * @return この個体の識別子
   */
  public long getId() {
    return id;
  }

  /**
   * @return この個体の AST を生成する上で文法的にた出しいかどうか
   */
  public boolean isSyntaxValid() {
    return generatedSourceCode.isGenerationSuccess();
  }

  /**
   * @return この個体はすでに生成済みかどうか
   */
  public boolean isReproduced() {
    return generatedSourceCode.isReproducedSourceCode();
  }

  /**
   * @return ビルドに成功したかどうか
   */
  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResults.getClass();
  }

  /**
   * @return この個体がビルドにに取り組んだかどうか
   * (AST の生成に失敗した場合・重複している場合はビルドに取り組まない)
   */
  public boolean triedBuild() {
    return generatedSourceCode.shouldBeTested();
  }

  /**
   * @return この個体の世代数
   */
  public OrdinalNumber getGenerationNumber() {
    return new OrdinalNumber(generationNumber);
  }

  /**
   * @return この個体の遺伝子
   */
  public Gene getGene() {
    return gene;
  }

  /**
   * @return この個体のソースコード
   */
  public GeneratedSourceCode getGeneratedSourceCode() {
    return generatedSourceCode;
  }

  /**
   * @return この個体のテスト結果
   */
  public TestResults getTestResults() {
    return testResults;
  }

  /**
   * @return この個体が何回選択されたか
   */
  public int getSelectionCount() {
    return selectionCount;
  }

  /**
   * @return この個体の評価値
   */
  public Fitness getFitness() {
    return fitness;
  }

  /**
   * @return この個体の疑惑値
   */
  public List<Suspiciousness> getSuspiciousnesses() {
    return suspiciousnesses;
  }

  /**
   * @return この個体が生成されるまでの過程
   */
  public HistoricalElement getHistoricalElement() {
    return historicalElement;
  }

  /**
   * この個体が選択されるたびに呼び，選択された回数を更新する
   */
  void incrementSelectionCount() {
    selectionCount++;
  }
}
