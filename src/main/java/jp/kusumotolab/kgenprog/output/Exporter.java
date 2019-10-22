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
 * ログや外部ファイルへの出力処理を行うクラス．
 */
public abstract class Exporter {

  private final static Logger log = LoggerFactory.getLogger(Exporter.class);
  protected final Configuration config;

  public Exporter(final Configuration config) {
    this.config = config;
  }

  /**
   * ログや外部ファイルへの出力を行う
   */
  public abstract void export(final VariantStore variantStore);

  /**
   * 以前の出力結果を削除する．
   */
  public final void clearPreviousResults() throws RuntimeException {
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
      final String message = String.format("Cannot clear directory (%s)", config.getOutDir());
      log.error(message);
      throw new RuntimeException(message);
    }
  }
}
