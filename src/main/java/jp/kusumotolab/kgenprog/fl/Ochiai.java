package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Ochiai implements FaultLocalization {

  final String LINE_SEPARATOR = System.lineSeparator();

  @Override
  public List<Suspiciouseness> exec(final TargetProject targetProject, final Variant variant,
      final TestProcessBuilder testExecutor) {

    final TestResults testResults = testExecutor.start(variant.getGeneratedSourceCode());

    final List<Suspiciouseness> suspeciousenesses = new ArrayList<>();

    for (final GeneratedAST ast : variant.getGeneratedSourceCode().getFiles()) {
      final String code = ast.getSourceCode();
      final SourceFile file = ast.getSourceFile();
      final int lastLineNumber = getLastLineNumber(code);

      for (int line = 1; line <= lastLineNumber; line++) {
        final List<Location> locations = ast.inferLocations(line);
        if (!locations.isEmpty()) {
          final Location l = locations.get(locations.size() - 1);
          final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(file, l);
          final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(file, l);
          final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(file, l);
          final double value = ef / Math.sqrt((ef + nf) * (ef + ep));
          if (0d < value) {
            final Suspiciouseness s = new Suspiciouseness(l, value);
            suspeciousenesses.add(s);
          }
        }
      }
    }

    return suspeciousenesses;
  }

  private int getLastLineNumber(final String text) {
    final int length1 = text.length();
    final int length2 = text.replaceAll(LINE_SEPARATOR, "").length();
    final int lastLineNumber = (length1 - length2) / LINE_SEPARATOR.length();
    return lastLineNumber;
  }
}
