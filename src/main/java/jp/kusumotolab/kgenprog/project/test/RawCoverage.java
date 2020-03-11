package jp.kusumotolab.kgenprog.project.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

/**
 * 行を単位としたカバレッジ情報を表す．<br>
 * どのソースコードに対して（FQN），各行がどのような結果（Status）だったのかを保持する．
 *
 * @author shinsuke
 */
public class RawCoverage implements Coverage {

  private final FullyQualifiedName executedTargetFQN;
  private final List<Status> statuses;

  /**
   * constructor．<br>
   * jacocoで生成したIClassCoverageから生成．
   *
   * @param classCoverage Jacocoが出力したCoverageの情報
   */
  public RawCoverage(final IClassCoverage classCoverage) {
    this.executedTargetFQN = new TargetFullyQualifiedName(classCoverage.getName()
        .replace("/", "."));
    this.statuses = convertClassCoverage(classCoverage);
  }

  /**
   * @param executedTargetFQN Coverage計測対象のクラス
   * @param statuses Coverage計測対象のクラスの各行の情報
   */
  public RawCoverage(final FullyQualifiedName executedTargetFQN, final List<Status> statuses) {
    this.executedTargetFQN = executedTargetFQN;
    this.statuses = statuses;
  }

  /**
   * ClassCoverageに格納されたCoverageをList<Status>に変換する． 実質enumの型変換やってるだけ．
   *
   * @param classCoverage Jacocoが出力したCoverageの情報
   * @return enumに変換した結果
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
  public FullyQualifiedName getExecutedTargetFQN() {
    return executedTargetFQN;
  }

  @Override
  public Status getStatus(final int index) {
    return statuses.get(index);
  }

  @Override
  public int getStatusesSize() {
    return statuses.size();
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
