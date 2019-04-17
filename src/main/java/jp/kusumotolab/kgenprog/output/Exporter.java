package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 出力に関する操作を行うクラス
 */
public class Exporter {

  private final static Logger log = LoggerFactory.getLogger(Exporter.class);

  private final Configuration config;
  private final VariantStore variantStore;
  private final PatchGenerator patchGenerator;

  /**
   * コンストラクタ．自動プログラム修正に必要な全ての情報を渡す必要あり．<br>
   *
   * -fがあるかつout-dirが空でないときはout-dirを削除する
   *
   * @param config 設定情報
   * @param variantStore 生成したVariantを保持するクラス
   * @param patchGenerator パッチ生成を行うインスタンス
   */
  public Exporter(final Configuration config, final VariantStore variantStore,
      final PatchGenerator patchGenerator) {
    this.config = config;
    this.variantStore = variantStore;
    this.patchGenerator = patchGenerator;

    if (config.isForce() && !isOutDirEmpty()) {
      deleteFiles(config.getOutDir());
    }
  }

  private boolean isOutDirEmpty() {
    if (Files.notExists(config.getOutDir())) {
      return true;
    }

    try {
      return Files.list(config.getOutDir())
          .count() > 0;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  private void deleteFiles(final Path file) {
    try {

      if (Files.isDirectory(file)) {
        final List<Path> subFiles = Files.list(file)
            .collect(Collectors.toList());

        for (final Path subFile : subFiles) {
          if (Files.isDirectory(subFile)) {
            deleteFiles(subFile);
          }
        }
      }

      Files.deleteIfExists(file);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * パッチの出力を行う
   */
  public void exportPatches() {
    final PatchStore patchStore = new PatchStore();

    variantStore.getFoundSolutions(config.getRequiredSolutionsCount())
        .stream()
        .map(patchGenerator::exec)
        .forEach(patchStore::add);

    patchStore.writeToLogger();

    if (!config.needNotOutput()) {
      patchStore.writeToFile(config.getOutDir());
    }
  }

  /**
   * JSONの出力を行う
   */
  public void exportJSON() {
    if (config.needNotOutput()) {
      return;
    }

    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();
    variantStoreExporter.writeToFile(config, variantStore);
  }
}
