package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * テスト情報に基づくFL<br>
 * メトリクスの計算式のみを抽象化
 */
public abstract class SpectrumBasedFaultLocalization implements FaultLocalization {

  private boolean skippableFormula;

  public SpectrumBasedFaultLocalization() {
    skippableFormula = isSkippableFormula();
  }

  /**
   * 与えられた数式が以下条件を満たすかを確認する．満たす場合，計算手順をある程度省略可能．<br>
   * 条件：ef=0 の場合に常に計算結果が 0 になる．<br>
   *
   * @see <a href="https://github.com/kusumotolab/kGenProg/pull/658">issue#658</a>
   */
  private boolean isSkippableFormula() {
    // 3つの条件でテスト (efは常に0)
    return formula(0, 10, 20, 30) == 0d && //
        formula(0, 99, 300, 0) == 0d && //
        formula(0, 33, 55, 66) == 0d;
  }

  /**
   * 疑惑値を計算する.
   *
   * @param generatedSourceCode 自動バグ限局の対象ソースコード
   * @param testResults テストの実行結果
   * @return suspiciousnesses 疑惑値
   */
  @Override
  public List<Suspiciousness> exec(final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults) {

    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    final Set<FullyQualifiedName> execuetdFailedTargetFQNs = testResults.getFailedTestResults()
        .stream()
        .map(tr -> tr.getExecutedTargetFQNs())
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());

    final Set<ASTLocation> registeredLocations = new HashSet<>();

    for (final GeneratedAST<ProductSourcePath> ast : generatedSourceCode.getProductAsts()) {

      // do nothing if none of failed target fqns contain the ast
      if (skippableFormula && !execuetdFailedTargetFQNs.contains(ast.getPrimaryClassName())) {
        continue;
      }

      final ProductSourcePath path = ast.getSourcePath();
      final int lastLineNumber = ast.getNumberOfLines();
      final ASTLocations astLocations = ast.createLocations();

      for (int line = 1; line <= lastLineNumber; line++) {
        final List<ASTLocation> locations = astLocations.infer(line);

        // no location found
        if (locations.isEmpty()) {
          continue;
        }

        final ASTLocation location = locations.get(locations.size() - 1);
        if (registeredLocations.contains(location)) {
          continue;
        }
        registeredLocations.add(location);
        final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(path, location);
        final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(path, location);
        final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(path, location);
        final long np = testResults.getNumberOfPassedTestsNotExecutingTheStatement(path, location);

        final double value = formula(ef, nf, ep, np);

        // zero or nan means nothing
        if (value <= 0 || Double.isNaN(value)) {
          continue;
        }

        final Suspiciousness s = new Suspiciousness(location, value);
        suspiciousnesses.add(s);
      }
    }
    return suspiciousnesses;
  }

  /**
   * FLメトリクスの計算式<br>
   * {@code ef}:該当する文を実行した失敗テストの個数<br>
   * {@code nf}:該当する文を実行しなかった失敗テストの個数<br>
   * {@code ep}:該当する文を実行した通過テストの個数<br>
   * {@code np}:該当する文を実行しなかった通過テストの個数<br>
   */
  protected abstract double formula(final double ef, final double nf, final double ep,
      final double np);
}
