package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 解のパッチを出力するクラス．
 */
public class PatchExporter extends Exporter {

  private final static Logger log = LoggerFactory.getLogger(PatchExporter.class);
  private final PatchGenerator patchGenerator;

  /**
   * コンストラクタ
   *
   * @param config 設定情報
   * @param patchGenerator パッチを生成するクラス
   */
  public PatchExporter(final Configuration config, final PatchGenerator patchGenerator) {
    super(config);
    this.patchGenerator = patchGenerator;
  }

  /**
   * パッチをログとファイルに出力する．
   *
   * @param variantStore バリアントを保持するクラス
   */
  @Override
  public void export(final VariantStore variantStore) {
    final PatchStore patchStore = new PatchStore();

    // 解のパッチを生成する
    variantStore.getFoundSolutions(config.getRequiredSolutionsCount())
        .stream()
        .map(patchGenerator::exec)
        .forEach(patchStore::add);

    // ログに出力する
    exportToLog(patchStore);

    if (!config.needNotOutput()) {
      // ファイルに出力する
      exportToFile(patchStore);
    }
  }

  /**
   * パッチをログに出力する
   *
   * @param patchStore 生成したパッチを保持するクラス
   */
  private void exportToLog(final PatchStore patchStore) {
    patchStore.writeToLogger();
  }

  /**
   * パッチをファイルに出力する
   *
   * @param patchStore 生成したパッチを保持するクラス
   */
  private void exportToFile(final PatchStore patchStore) {
    if (Files.notExists(config.getOutDir())) {
      try {
        Files.createDirectory(config.getOutDir());
      } catch (final IOException e) {
        log.error("Failed to write patches!");
        log.error(e.getMessage(), e);
        return;
      }
    }
    patchStore.writeToFile(config.getOutDir());
  }
}
