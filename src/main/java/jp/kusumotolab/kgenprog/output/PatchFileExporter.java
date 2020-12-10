package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 解のパッチをファイルに出力する．
 */
class PatchFileExporter implements Exporter {

  private static final Logger log = LoggerFactory.getLogger(PatchFileExporter.class);
  public static final String DIR_PREFIX = "patch-v";
  private final Path outdir;

  PatchFileExporter(final Path outdir) {
    this.outdir = outdir;
  }

  /**
   * パッチをファイルに出力する．
   *
   * @param variantStore バリアントを保持するクラス
   */
  @Override
  public void export(final VariantStore variantStore) {
    createDir(outdir);

    // Warn if previous patch folder exists
    try (final Stream<Path> walk = Files.walk(outdir)) {
      if (walk.filter(Files::isDirectory)
          .map(Path::getFileName)
          .map(Path::toString)
          .anyMatch(p -> p.startsWith(DIR_PREFIX))) {
        log.warn("warning: previous patch folders exist in out dir.");
      }
    } catch (final IOException e) {
      log.warn(e.getMessage());
    }

    final PatchGenerator generator = new PatchGenerator();
    variantStore.getFoundSolutions()
        .stream()
        .map(generator::exec)
        .forEach(this::writeToFile);
  }

  private void writeToFile(final Patch patch) {
    final long id = patch.getVariantId();
    final Path subdir = outdir.resolve(DIR_PREFIX + id);

    try {
      Files.createDirectories(subdir);
    } catch (final IOException e) {
      throw new UncheckedIOException(e); // cannot handle this exception
    }

    patch.getFileDiffs()
        .forEach(fd -> this.writeFileDiff(fd, subdir));
  }

  private void writeFileDiff(final FileDiff diff, final Path subdir) {
    final String filename = diff.getFileName();
    try {
      Files.write(subdir.resolve(filename + ".java"), diff.getModifiedSourceCodeLines());
      Files.write(subdir.resolve(filename + ".diff"), diff.getDiff());
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
