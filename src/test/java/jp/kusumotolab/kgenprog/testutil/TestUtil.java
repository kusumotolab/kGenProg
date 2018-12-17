package jp.kusumotolab.kgenprog.testutil;

import static org.assertj.core.api.Assertions.fail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.history.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.history.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class TestUtil {

  public static void deleteWorkDirectory(final Path path) {
    if (Files.exists(path)) {
      try {
        FileUtils.deleteDirectory(path.toFile());
      } catch (IOException e) {
        fail("Couldn't delete work dir [" + path.toString() + "]");
      }
    }
  }

  public static Variant createVariant(final Configuration config) {
    final Gene gene = new Gene(Collections.emptyList());
    final GeneratedSourceCode sourceCode = createGeneratedSourceCode(config.getTargetProject());
    final TestResults testResults = new LocalTestExecutor(config).exec(sourceCode);
    final Fitness fitness = new DefaultCodeValidation().exec(null, testResults);
    final List<Suspiciousness> suspiciousnesses = new Ochiai().exec(sourceCode, testResults);
    final HistoricalElement element = new OriginalHistoricalElement();
    return new Variant(0,0, gene, sourceCode, testResults, fitness, suspiciousnesses, element);
  }

  public static GeneratedSourceCode createGeneratedSourceCode(final TargetProject project) {
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(project);
    return sourceCode;
  }

}
