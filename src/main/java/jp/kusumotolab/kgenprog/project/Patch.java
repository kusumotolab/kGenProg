package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Patch implements Result {

  public final String fileName;
  private final List<String> contents;
  private final List<String> diff;

  public Patch(final String fileName, final List<String> containts, final List<String> diff) {
    this.fileName = fileName;
    this.contents = containts;
    this.diff = diff;
  }

  public List<String> getContaints() {
    return contents;
  }

  @Override
  public String getDiff() {
    List<String> formattedDiff = new ArrayList<>(diff);
    formattedDiff.add(0, "");
    return formattedDiff.stream()
        .collect(Collectors.joining(System.getProperty("line.separator")));
  }

  public void write(final String path) {
    final Path outputPath = Paths.get(path);
    try {
      Files.write(outputPath.resolve(fileName + ".java"), contents);
      Files.write(outputPath.resolve(fileName + ".patch"), diff);
    } catch (final IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }
}
