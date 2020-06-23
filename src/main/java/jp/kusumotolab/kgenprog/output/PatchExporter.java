package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 解のパッチをログとファイルに出力するクラス．
 */
class PatchExporter implements Exporter {

  private static final Logger log = LoggerFactory.getLogger(PatchExporter.class);
  public static final String DIR_PREFIX = "patch-v";
  private final Path outdir;
  private final PatchGenerator patchGenerator;

  PatchExporter(final Path outdir) {
    this.outdir = outdir;
    this.patchGenerator = new PatchGenerator();
  }

  /**
   * パッチをログとファイルに出力する．
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

    final List<Variant> solutions = variantStore.getFoundSolutions();

    solutions.stream()
        .map(patchGenerator::exec)
        .forEach(p -> {
          writeLog(p);
          writePatch(p);
        });
  }

  private void writeLog(final Patch patch) {
    patch.getFileDiffs()
        .forEach(fd -> log.info(String.format("patch (v%d)%s%s",
            patch.getVariantId(), System.lineSeparator(), fd))
        );
  }

  private void writePatch(final Patch patch) {
    final long id = patch.getVariantId();
    final Path subdir = outdir.resolve(DIR_PREFIX + Long.toString(id));

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
