package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.Collection;
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

    for (final GeneratedAST<ProductSourcePath> ast : generatedSourceCode.getProductAsts()) {

      // do nothing if none of failed target fqns contain the ast
      if (isSkippablePassedTests()
          && !execuetdFailedTargetFQNs.contains(ast.getPrimaryClassName())) {
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

        final ASTLocation loc = locations.get(locations.size() - 1);
        final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(path, loc);
        final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(path, loc);
        final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(path, loc);
        final long np = testResults.getNumberOfPassedTestsNotExecutingTheStatement(path, loc);

        final double value = formula(ef, nf, ep, np);

        // zero or nan means nothing
        if (value <= 0 || Double.isNaN(value)) {
          continue;
        }

        final Suspiciousness s = new Suspiciousness(loc, value);
        suspiciousnesses.add(s);
      }
    }
    return suspiciousnesses;
  }

  /**
   * FLメトリクスの計算式<br>
   * {@code ef}:該当する文を実行した失敗テストの個数<br>
   * {@code nf}:該当する文を実行しなかった失敗テストの個数<br>
   * {@code ep}:該当する文を実行した通過テストの個数
   * {@code np}:該当する文を実行しなかった通過テストの個数<br>
   */
  abstract protected double formula(double ef, double nf, double ep, double np);

  /**
   * {@link SpectrumBasedFaultLocalization#formula}の計算において，通過テストのみを対象としてよいかどうか．
   * 高速化のためのパラメタであり不明な際はtrueでも正しく動作する．
   * @return
   */
  abstract protected boolean isSkippablePassedTests();

}
