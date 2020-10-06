package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.HashMap;
import java.util.Map;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ReproducedSourceCode;

/**
 * Gene の情報を基にソースコードの生成を行うクラス
 * このクラスはすでに生成済みのソースコードを生成しようとすると，
 * そのソースコードの生成をやめて，ReproducedSourceCode を返す
 *
 * @see SourceCodeGeneration
 */
public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private final Map<String, ReproducedStatus> sourceCodeMap = new HashMap<>();

  /**
   * 重複したソースコードの生成を避けるため，初期状態のソースコードの情報をここで保持する
   *
   * @param initialVariant 入力されたプロジェクトを表す個体
   */
  @Override
  public void initialize(final Variant initialVariant) {
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    putSourceCode(generatedSourceCode);
  }

  /**
   * @param variantStore これまでの個体の情報を保持している VariantStore
   * @param gene ソースコードの編集操作列
   * @return 生成されたソースコード
   * 新規なら GeneratedSourceCode
   * 生成に失敗したら GenerationFailedSourceCode
   * すでに生成済みなら ReproducedSourceCode
   * @see GeneratedSourceCode
   * @see jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode
   * @see ReproducedSourceCode
   */
  @Override
  public GeneratedSourceCode exec(final VariantStore variantStore, final Gene gene) {
    final Variant initialVariant = variantStore.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    for (final Base base : gene.getBases()) {
      final Operation operation = base.getOperation();
      generatedSourceCode = operation.apply(generatedSourceCode, base.getTargetLocation());

      // immediately return failed source code if operation#apply was failed
      if (! generatedSourceCode.isGenerationSuccess()) {
        return generatedSourceCode;
      }
    }

    if (sourceCodeMap.containsKey((generatedSourceCode.getMessageDigest()))) {
      final ReproducedStatus status = sourceCodeMap.get(generatedSourceCode.getMessageDigest());
      status.incrementCounter();
      generatedSourceCode = new ReproducedSourceCode(status);
    } else {
      putSourceCode(generatedSourceCode);
    }

    return generatedSourceCode;
  }

  private void putSourceCode(final GeneratedSourceCode generatedSourceCode) {
    final ReproducedStatus status = new ReproducedStatus(
        generatedSourceCode.isGenerationSuccess(), generatedSourceCode.getGenerationMessage());
    sourceCodeMap.put(generatedSourceCode.getMessageDigest(), status);
  }
}
