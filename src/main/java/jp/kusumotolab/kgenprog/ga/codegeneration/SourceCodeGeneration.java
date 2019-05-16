package jp.kusumotolab.kgenprog.ga.codegeneration;

import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

/**
 * Gene の情報を基にソースコードの生成を行うインターフェース
 * @see DefaultSourceCodeGeneration kGenProg のデフォルト実装
 */
public interface SourceCodeGeneration {

  /**
   * 入力されたプロジェクトにを表す個体から必要な情報をここで抜き出す
   *
   * @param initialVariant 入力されたプロジェクトを表す個体
   */
  public void initialize(Variant initialVariant);

  /**
   * @param variantStore これまでのバリアントの情報を保持している VariantStore
   * @param gene ソースコードの編集操作列
   * @return 生成されたソースコード
   */
  public GeneratedSourceCode exec(VariantStore variantStore, Gene gene);
}
