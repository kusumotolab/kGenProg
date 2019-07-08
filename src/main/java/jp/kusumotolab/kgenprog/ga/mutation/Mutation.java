package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * ソースコードの変異を行うクラス
 */
public abstract class Mutation {

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection candidateSelection;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   */
  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
  }

  /**
   * @param candidates 再利用するソースコード群
   */
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    candidateSelection.setCandidates(candidates);
  }

  /**
   * 変異処理された Variant を mutationGeneratingCount 分だけ返す
   *
   * @param variantStore Variant の情報を格納するオブジェクト
   * @return 変異された Gene を持った Variant のリスト
   */
  public abstract List<Variant> exec(VariantStore variantStore);

}
