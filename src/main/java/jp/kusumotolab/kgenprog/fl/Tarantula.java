package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 *  FL戦略の一つ(Tarantula).
 *  <br>
 *  {@code value = (ef / (ef + nf)) / (ef / (ef + nf) + ep / (ep + np))}<br>
 *  {@code ef}:該当する文を実行し，通過しなかったテストの個数<br>
 *  {@code nf}:該当する文を実行せずに，通過しなかったテストの個数<br>
 *  {@code ep}:該当する文を実行し，通過したテストの個数<br>
 *  {@code np}:該当する文を実行せずに，通過したテストの個数
 */
public class Tarantula implements FaultLocalization {

  /**
   * 疑惑値を計算する.
   * @param generatedSourceCode
   * @param testResults
   * @return suspiciousnesses 疑惑値
   */
  @Override
  public List<Suspiciousness> exec(final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults) {

    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST<ProductSourcePath> ast : generatedSourceCode.getProductAsts()) {
      final ProductSourcePath path = ast.getSourcePath();
      final int lastLineNumber = ast.getNumberOfLines();
      final ASTLocations astLocations = ast.createLocations();

      for (int line = 1; line <= lastLineNumber; line++) {
        final List<ASTLocation> locations = astLocations.infer(line);
        if (!locations.isEmpty()) {
          final ASTLocation l = locations.get(locations.size() - 1);
          final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(path, l);
          final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(path, l);
          final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(path, l);
          final long np = testResults.getNumberOfPassedTestsNotExecutingTheStatement(path, l);
          final double value = (ef / (double)(ef + nf)) / (ef / (double)(ef + nf) + ep / (double)(ep + np));
          if (0d < value) {
            final Suspiciousness s = new Suspiciousness(l, value);
            suspiciousnesses.add(s);
          }
        }
      }
    }

    return suspiciousnesses;
  }
}
