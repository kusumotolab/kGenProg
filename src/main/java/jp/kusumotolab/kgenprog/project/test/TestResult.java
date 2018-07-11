package jp.kusumotolab.kgenprog.project.test;

import static java.util.stream.Collectors.toList;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class TestResult implements Serializable {

  private static final long serialVersionUID = 1L;

  final public FullyQualifiedName executedTestFQN;
  final public boolean failed;
  final private Map<FullyQualifiedName, Coverage> coverages;

  /**
   * constructor
   * 
   * @param executedTestFQN 実行したテストメソッドの名前
   * @param failed テストの結果
   * @param coverages テスト対象それぞれの行ごとのCoverage計測結果
   */
  public TestResult(final FullyQualifiedName executedTestFQN, final boolean failed,
      final Map<FullyQualifiedName, Coverage> coverages) {
    this.executedTestFQN = executedTestFQN;
    this.failed = failed;
    this.coverages = coverages;
  }

  public List<FullyQualifiedName> getExecutedTargetFQNs() {
    return this.coverages.keySet()
        .stream()
        .collect(toList());
  }

  public Coverage getCoverages(final FullyQualifiedName testFQN) {
    return this.coverages.get(testFQN);
  }

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(final int indentDepth) {
    final StringBuilder sb = new StringBuilder();
    final String indent = StringUtils.repeat(" ", indentDepth);
    sb.append(indent + "{\n");
    sb.append(indent + "  \"executedTestFQN\": \"" + executedTestFQN + "\",\n");
    sb.append(indent + "  \"wasFailed\": " + failed + ",\n");
    sb.append(indent + "  \"coverages\": [\n");
    sb.append(String.join(",\n", coverages.values()
        .stream()
        .map(c -> c.toString(indentDepth + 2))
        .collect(toList())));
    sb.append("\n");
    sb.append(indent + "  ]\n");
    sb.append(indent + "}");
    return sb.toString();
  }
}
