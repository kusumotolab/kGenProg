package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Patch {

  private static final Logger log = LoggerFactory.getLogger(Patch.class);

  private final List<String> diff;
  public final String fileName;
  private final List<String> originalSourceCodeLines;
  private final List<String> modifiedSourceCodeLines;

  public Patch(final List<String> diff, final String fileName,
      final List<String> originalSourceCodeLines, final List<String> modifiedSourceCodeLines) {
    this.diff = diff;
    this.fileName = fileName;
    this.originalSourceCodeLines = originalSourceCodeLines;
    this.modifiedSourceCodeLines = modifiedSourceCodeLines;
  }

  public List<String> getOriginalSourceCodeLines() {
    return originalSourceCodeLines;
  }

  public List<String> getModifiedSourceCodeLines() {
    return modifiedSourceCodeLines;
  }

  public String getDiff() {
    final List<String> formattedDiff = new ArrayList<>(diff);
    return String.join(System.lineSeparator(), formattedDiff);
  }

  public void write(final String path) {
    final Path outputPath = Paths.get(path);
    try {
      Files.write(outputPath.resolve(fileName + ".java"), modifiedSourceCodeLines);
      Files.write(outputPath.resolve(fileName + ".patch"), diff);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

}
