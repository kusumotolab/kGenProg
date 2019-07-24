package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
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

  /**
   * コンストラクタ．自動プログラム修正に必要な全ての情報を渡す必要あり．<br>
   *
   * -fがあるかつout-dirが空でないときはout-dirを空にする
   *
   * @param config 設定情報
   */
  public Exporter(final Configuration config) {
    this.config = config;
  }

  private boolean isWritable() {

    if (Files.notExists(config.getOutDir())) {
      return true;
    }

    final Path outDir = config.getOutDir();
    try {
      return Files.walk(outDir, FileVisitOption.FOLLOW_LINKS)
          .noneMatch(e -> !e.equals(outDir));
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  private void clearOutDir() {
    if (Files.notExists(config.getOutDir())) {
      return;
    }

    try {
      final Path outDir = config.getOutDir();
      final List<Path> subFiles = Files.walk(outDir, FileVisitOption.FOLLOW_LINKS)
          .filter(e -> !e.equals(outDir))
          .sorted(Comparator.reverseOrder())
          .collect(Collectors.toList());

      for (final Path subFile : subFiles) {
        Files.deleteIfExists(subFile);
      }
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * パッチおよびJSONの出力を行う<br>
   * <table>
   * <caption>出力の有無</caption>
   * <tr>
   * <td></td>
   * <td>forceオプションあり</td>
   * <td>forceオプションなし</td>
   * </tr>
   *
   * <tr>
   * <td>outDirが空</td>
   * <td>出力する</td>
   * <td>出力する</td>
   * </tr>
   *
   * <tr>
   * <td>outDirが空でない</td>
   * <td>outDirを空にしてから出力する</td>
   * <td>出力しない</td>
   * </tr>
   * </table>
   */
  public void export(final VariantStore variantStore, final PatchGenerator patchGenerator) {
    final PatchStore patchStore = new PatchStore();
    variantStore.getFoundSolutions(config.getRequiredSolutionsCount())
        .stream()
        .map(patchGenerator::exec)
        .forEach(patchStore::add);

    patchStore.writeToLogger();

    if (isWritable()) {

      if (Files.notExists(config.getOutDir())) {
        try {
          Files.createDirectory(config.getOutDir());
        } catch (final IOException e) {
          log.error(e.getMessage(), e);
          return;
        }
      }

      patchStore.writeToFile(config.getOutDir());
      exportJSON(variantStore);
    }
  }

  /**
   * JSONの出力を行う
   */
  private void exportJSON(final VariantStore variantStore) {
    if (config.needNotOutput()) {
      return;
    }

    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();
    variantStoreExporter.writeToFile(config, variantStore);
  }
}
