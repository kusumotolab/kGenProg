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

  public void add(final Patch patche) {
    this.patches.add(patche);
  }

  public Patch get(final int index) {
    return patches.get(index);
  }

  public void writeWithFile(final Path outDir) {
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

  public void writeWithoutFile() {
    for (final Patch patch : patches) {
      log.info(System.lineSeparator() + patch.getDiff());
    }
  }
}
