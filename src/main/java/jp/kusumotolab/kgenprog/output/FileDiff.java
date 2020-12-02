package jp.kusumotolab.kgenprog.output;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/***
 * 1ファイルの変更内容．
 * 単一ファイルの差分情報．
 *
 * @author k-naitou
 *
 */
public class FileDiff {

  private final List<String> diff;
  private final String fileName;
  private final List<String> originalSourceCodeLines;
  private final List<String> modifiedSourceCodeLines;

  public FileDiff(final String fileName, final List<String> diff,
      final List<String> originalSourceCodeLines, final List<String> modifiedSourceCodeLines) {
    this.diff = diff;
    this.fileName = fileName;
    this.originalSourceCodeLines = originalSourceCodeLines;
    this.modifiedSourceCodeLines = modifiedSourceCodeLines;
  }

  public String getFileName() {
    return fileName;
  }

  public List<String> getOriginalSourceCodeLines() {
    return originalSourceCodeLines;
  }

  public List<String> getModifiedSourceCodeLines() {
    return modifiedSourceCodeLines;
  }

  public List<String> getDiff() {
    return diff;
  }

  @Override
  public String toString() {
    return diff.stream()
        .collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * デフォルトエンコーディングに変換したdiffを返す
   */
  public String toStringWithDefaultEncoding() {
    final Charset defaultCharset = Charset.defaultCharset();

    return diff.stream()
        .map(e -> e.getBytes(defaultCharset))
        .map(e -> new String(e, defaultCharset))
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
