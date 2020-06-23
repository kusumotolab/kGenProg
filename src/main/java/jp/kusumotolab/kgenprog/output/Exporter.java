package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * ログや外部ファイルへの出力処理を行うインタフェース．
 */
interface Exporter {

  /**
   * ログや外部ファイルへの出力を行う
   */
  void export(final VariantStore variantStore);

  default void createDir(final Path outdir) {
    if (Files.notExists(outdir)) {
      try {
        Files.createDirectories(outdir);
      } catch (IOException e) {
        throw new UncheckedIOException(e); // couldn't handle this exception
      }
    }
  }
}
