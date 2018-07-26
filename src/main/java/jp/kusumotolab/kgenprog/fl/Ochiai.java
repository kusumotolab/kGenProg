package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Ochiai implements FaultLocalization {

  private Logger log = LoggerFactory.getLogger(Ochiai.class);

  @Override
  public List<Suspiciousness> exec(final TargetProject targetProject, final Variant variant,
      final TestProcessBuilder testExecutor) {
    log.debug("enter exec(TargetProject, Variant, TestProcessBuilder)");

    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final TestResults testResults = testExecutor.start(generatedSourceCode);


    final List<Suspiciousness> suspeciousenesses = new ArrayList<>();

    for (final GeneratedAST ast : variant.getGeneratedSourceCode()
        .getAsts()) {
      final String code = ast.getSourceCode();
      final SourcePath path = ast.getSourcePath();
      final int lastLineNumber = countLines(code);

      for (int line = 1; line <= lastLineNumber; line++) {
        final List<ASTLocation> locations = ast.inferLocations(line);
        if (!locations.isEmpty()) {
          final ASTLocation l = locations.get(locations.size() - 1);
          final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(path, l);
          final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(path, l);
          final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(path, l);
          final double value = ef / Math.sqrt((ef + nf) * (ef + ep));
          if (0d < value) {
            final Suspiciousness s = new Suspiciousness(l, value);
            suspeciousenesses.add(s);
          }
        }
      }
    }

    return suspeciousenesses;
  }

  private int countLines(final String text) {
    String[] lines = text.split("\r\n|\r|\n");
    return lines.length;
  }
}
