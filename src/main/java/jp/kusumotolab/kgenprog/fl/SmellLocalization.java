package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class SmellLocalization implements FaultLocalization {

  @Override
  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode,
      TestResults testResults) {
    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST ast : generatedSourceCode.getAsts()) {
      final String code = ast.getSourceCode();
      final int lastLineNumber = countLines(code);

      for (int line = 1; line <= lastLineNumber; ++line) {
        final List<ASTLocation> locations = ast.inferLocations(line);

        if (locations.isEmpty()) {
          continue;
        }

        final ASTLocation location = locations.get(locations.size() - 1);
        // todo: calculate smell
        suspiciousnesses.add(new Suspiciousness(location, 1.0));
      }
    }

    return suspiciousnesses;
  }

  private int countLines(final String text) {
    final String[] lines = text.split("\r\n|\r|\n");
    return lines.length;
  }
}
