package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Patches {

  private static Logger log = LoggerFactory.getLogger(Patches.class);

  private final List<Patch> patches = new ArrayList<>();
  private final boolean isDiscardOutput;

  public Patches(final boolean isDiscardOutput) {
    this.isDiscardOutput = isDiscardOutput;
  }

  public void addAllPatch(final List<Patch> patches) {
    this.patches.addAll(patches);
  }

  public void write(final Path outDir) {
    if (isDiscardOutput) {
      writeWithoutFile();
    } else {
      writeWithFile(outDir);
    }
  }

  private void writeWithFile(final Path outDir) {
    log.debug("enter outputPatch(VariantStore)");
    try {
      if (Files.notExists(outDir)) {
        Files.createDirectories(outDir);
      }
    } catch (final IOException e) {
      log.error(e.getMessage());
    }

    for (final Patch patch : patches) {
      log.info(System.lineSeparator() + patch.getDiff());
      patch.write(outDir);
    }
  }

  private void writeWithoutFile() {
    log.debug("enter outputPatch(VariantStore)");
    for (final Patch patch : patches) {
      log.info(System.lineSeparator() + patch.getDiff());
    }
  }
}
