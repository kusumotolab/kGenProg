package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Patch implements Result {

  private final String fileName;
  private final List<String> containts;
  private final List<String> diff;

  public Patch(final String fileName, final List<String> containts, final List<String> diff) {
    this.fileName = fileName;
    this.containts = containts;
    this.diff = diff;
  }

  public String getName() {
    return fileName;
  }

  public List<String> getContaints() {
    return containts;
  }

  public List<String> getDiff() {
    return diff;
  }

  public String getDiffInString() {
    diff.add(0, "");
    return diff.stream()
        .collect(Collectors.joining(System.getProperty("line.separator")));
  }

  public void write(final String path) {
    final Path outputPath = Paths.get(path);
    try {
      Files.write(outputPath.resolve(fileName + ".java"), containts);
      Files.write(outputPath.resolve(fileName + ".patch"), diff);
    } catch (final IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }
}
