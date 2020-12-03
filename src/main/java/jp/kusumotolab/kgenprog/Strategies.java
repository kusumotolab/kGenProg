package jp.kusumotolab.kgenprog;

import java.util.List;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation.Input;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 自動プログラム修正を行う際の様々な戦略を保持するためのクラス．
 *
 * @author higo
 */
public class Strategies {

  private final FaultLocalization faultLocalization;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final JDTASTConstruction astConstruction;
  private final SourceCodeValidation sourceCodeValidation;
  private final TestExecutor testExecutor;
  private final VariantSelection variantSelection;

  /**
   * コンストラクタ．自動プログラム修正を行う際に利用する戦略を渡す必要あり．
   *
   * @param faultLocalization 自動バグ限局を行うインスタンス
   * @param sourceCodeGeneration コード生成を行うインスタンス
   * @param sourceCodeValidation コードの評価を行うインスタンス
   * @param testExecutor テスト実行を行うインスタンス
   * @param variantSelection 個体の選択を行うインスタンス
   */
  public Strategies(final FaultLocalization faultLocalization,
      final JDTASTConstruction astConstruction, final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final TestExecutor testExecutor,
      final VariantSelection variantSelection) {

    this.faultLocalization = faultLocalization;
    this.astConstruction = astConstruction;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.testExecutor = testExecutor;
    this.variantSelection = variantSelection;
  }

  /**
   * 自動バグ限局を実行するメソッド．<br>
   * 限局対象のソースコードとテストの実行結果を受け取り，自動バグ限局の実行結果を返す．<br>
   *
   * @param generatedSourceCode 自動バグ限局の対象ソースコード
   * @param testResults テストの実行結果
   * @return 自動バグ限局の実行結果
   */
  public List<Suspiciousness> execFaultLocalization(final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults) {
    return faultLocalization.exec(generatedSourceCode, testResults);
  }

  /**
   * ソースコード生成を実行するメソッド．<br>
   * 生成の基になるソースコード群と生成に用いる遺伝子を与え，生成したソースコードを返す．<br>
   *
   * @param variantStore 生成の基になるソースコード群
   * @param gene 生成に用いる遺伝子
   * @return 生成したソースコード
   */
  public GeneratedSourceCode execSourceCodeGeneration(final VariantStore variantStore,
      final Gene gene) {
    return sourceCodeGeneration.exec(variantStore, gene);
  }

  /**
   * テスト実行を行うメソッド．<br>
   * テストの実行対象となる個体（ソースコード）を引数として渡し，テストの実行結果を返す．<br>
   *
   * @param variant テストの実行対象となる個体（ソースコード）
   * @return テストの実行結果
   */
  public TestResults execTestExecutor(final Variant variant) {
    return testExecutor.exec(variant);
  }

  /**
   * テストの実行を非同期で行うメソッド．クラスタ環境で用いる．<br>
   * テストの実行対象となる個体（ソースコード）を引数として渡し，テストの実行結果を返す．<br>
   *
   * @param variantSingle テストの実行対象となる個体（ソースコード）
   * @return テストの実行結果
   */
  public Single<TestResults> execAsyncTestExecutor(final Single<Variant> variantSingle) {
    return testExecutor.execAsync(variantSingle);
  }

  /**
   * ソースコードの評価を行うメソッド．<br>
   * ソースコードの評価に用いる情報を渡し，評価結果を返す．<br>
   *
   * @param input ソースコードの評価に用いる情報
   * @return 評価結果
   */
  public Fitness execSourceCodeValidation(final Input input) {
    return sourceCodeValidation.exec(input);
  }

  /**
   * 抽象構文木の生成を行うメソッド．<br>
   * 対象プロジェクトを私，抽象構文木情報を含んだソースコードを返す．<br>
   *
   * @param targetProject 対象プロジェクト
   * @return 抽象構文木情報を含んだソースコード
   */
  public GeneratedSourceCode execASTConstruction(final TargetProject targetProject) {
    return astConstruction.constructAST(targetProject);
  }

  /**
   * 個体（プログラム）の選択を行うメソッド．<br>
   * 選択対象の個体群を渡し，その中から選択された個体群を返す．
   *
   * @param current 現在の世代に引き継がれた個体群
   * @param generated 現在の世代で新しく生成された個体群
   * @return currentとgeneratedから選択された個体群
   */
  public List<Variant> execVariantSelection(final List<Variant> current,
      final List<Variant> generated) {
    return variantSelection.exec(current, generated);
  }
}
