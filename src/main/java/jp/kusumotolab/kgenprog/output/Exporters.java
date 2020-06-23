package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

public class Exporters {

  private static final Logger log = LoggerFactory.getLogger(Exporters.class);
  private final Path outdir;
  private final Set<Exporter> exporterSet;

  public Exporters(final Configuration config) {
    outdir = config.getOutDir();
    exporterSet = createExporterSet(config);
    clearPreviousResults();
  }

  private Set<Exporter> createExporterSet(final Configuration config) {
    if (config.needNotOutput()) {
      return Collections.emptySet();
    }
    if (config.isHistoryRecord()) {
      return Set.of(new PatchExporter(outdir), new JSONExporter(outdir));
    }
    return Set.of(new PatchExporter(outdir));
  }

  public void exportAll(final VariantStore variantStore) {
    if (exporterSet.isEmpty()) {
      return; // do nothing when exporters are empty
    }
    this.exporterSet.forEach(e -> e.export(variantStore));
  }

  private void clearPreviousResults() {
    if (Files.notExists(outdir)) {
      return;
    }

    // delete outdir recursively
    try (final Stream<Path> walk = Files.walk(outdir)) {
      walk.sorted(Comparator.reverseOrder())
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (final IOException e) {
              log.warn(String.format("warn: cannot clear outdir %s", path));
            }
          });
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
