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
   * コンストラクタ．<br>
   *
   * @param config 出力に必要な設定情報
   */
  public Exporter(final Configuration config) throws RuntimeException {
    this.config = config;
    this.clearOutDir();
  }

  /**
   * 出力先のディレクトリが空でないときは出力先のディレクトリを空にする<br>
   * 出力先のディレクトリが存在しないときは何もしない
   */
  private void clearOutDir() throws RuntimeException {
    if (Files.notExists(config.getOutDir())) {
      return;
    }

    try {
      final List<Path> subFiles = Files.walk(config.getOutDir(), FileVisitOption.FOLLOW_LINKS)
          .filter(e -> !e.equals(config.getOutDir()))
          .sorted(Comparator.reverseOrder())
          .collect(Collectors.toList());

      for (final Path subFile : subFiles) {
        Files.deleteIfExists(subFile);
      }
    } catch (final IOException e) {
      final String message = String.format("Cannot clear directory %s", config.getOutDir());
      log.error(message);
      throw new RuntimeException(message);
    }
  }

  /**
   * パッチおよびJSONの出力を行う
   */
  public void export(final VariantStore variantStore, final PatchGenerator patchGenerator) {
    final PatchStore patchStore = new PatchStore();

    variantStore.getFoundSolutions(config.getRequiredSolutionsCount())
        .stream()
        .map(patchGenerator::exec)
        .forEach(patchStore::add);
    patchStore.writeToLogger();

    if (!config.needNotOutput()) {
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
    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();
    variantStoreExporter.writeToFile(config, variantStore);
  }
}
