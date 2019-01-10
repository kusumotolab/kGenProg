package jp.kusumotolab.kgenprog.project.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

public class Coverage {

  public enum Status {
    /**
     * 実行不可能な行 (value is 0x00).
     */
    EMPTY,
    /**
     * 実行可能だが実行されなかった行 (value is 0x01).
     */
    NOT_COVERED,
    /**
     * 実行可能で実行された行 (value is 0x02).
     */
    COVERED,
    /**
     * 実行可能で一部だけ実行された行(value is 0x03). TODO 現在このステータスは一切利用していない．
     * jacocoはif分岐等にこの値をセットするが，本Statusではif分岐はCOVEREDに持ち上げ．
     */
    PARTLY_COVERED
  }

  final public FullyQualifiedName executedTargetFQN;
  final public List<Status> statuses;

  /**
   * constructor． jacocoで生成したIClassCoverageから生成．
   * 
   * @param className Coverage計測対象のクラス名
   * @param statuses Coverage計測の結果
   */
  public Coverage(final IClassCoverage classCoverage) {
    this.executedTargetFQN = new TargetFullyQualifiedName(classCoverage.getName()
        .replace("/", "."));
    this.statuses = convertClassCoverage(classCoverage);
  }

  public Coverage(final FullyQualifiedName executedTargetFQN, final List<Status> statuses) {
    this.executedTargetFQN = executedTargetFQN;
    this.statuses = statuses;
  }

  /**
   * ClassCoverageに格納されたCoverageをList<Status>に変換する． 実質enumの型変換やってるだけ．
   * 
   * @param classCoverage
   * @return
   */
  private List<Status> convertClassCoverage(final IClassCoverage classCoverage) {
    final List<Coverage.Status> statuses = new ArrayList<>();
    for (int i = 1; i <= classCoverage.getLastLine(); i++) {
      final Coverage.Status status;
      final int s = classCoverage.getLine(i)
          .getStatus();

      if (s == ICounter.EMPTY) {
        status = Coverage.Status.EMPTY;
      } else if (s == ICounter.FULLY_COVERED || s == ICounter.PARTLY_COVERED) {
        status = Coverage.Status.COVERED;
      } else if (s == ICounter.NOT_COVERED) {
        status = Coverage.Status.NOT_COVERED;
      } else {
        status = Coverage.Status.EMPTY;
      }
      statuses.add(status);
    }
    return statuses;
  }

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(final int indentDepth) {
    final StringBuilder sb = new StringBuilder();
    final String indent = StringUtils.repeat(" ", indentDepth);
    sb.append(indent + "  {");
    sb.append("\"executedTargetFQN\": \"" + executedTargetFQN + "\", ");
    sb.append("\"coverages\": [");
    sb.append(statuses.stream()
        .map(Enum::ordinal)
        .map(String::valueOf)
        .collect(Collectors.joining(", ")));
    sb.append("]}");
    return sb.toString();
  }
}
