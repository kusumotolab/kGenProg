package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 1ファイルの変更内容．
 * @author k-naitou
 *
 */
public class FileDiff {

  private static final Logger log = LoggerFactory.getLogger(FileDiff.class);

  private final List<String> diff;
  public final String fileName;
  private final List<String> originalSourceCodeLines;
  private final List<String> modifiedSourceCodeLines;

  public FileDiff(final List<String> diff, final String fileName,
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
    return String.join(System.lineSeparator(), diff);
  }

  public void write(final Path outDir) {
    try {
      Files.write(outDir.resolve(fileName + ".java"), modifiedSourceCodeLines);
      Files.write(outDir.resolve(fileName + ".patch"), diff);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
