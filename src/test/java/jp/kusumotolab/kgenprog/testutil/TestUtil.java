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
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
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
    final Gene gene = new SimpleGene(Collections.emptyList());
    final GeneratedSourceCode sourceCode = createGeneratedSourceCode(config.getTargetProject());
    final TestResults testResults = new TestExecutor(config).exec(sourceCode);
    final Fitness fitness = new DefaultCodeValidation().exec(null, testResults);
    final List<Suspiciousness> suspiciousnesses = new Ochiai().exec(sourceCode, testResults);
    return new Variant(gene, sourceCode, testResults, fitness, suspiciousnesses);
  }
  
  public static GeneratedSourceCode createGeneratedSourceCode(final TargetProject project) {
    final List<GeneratedAST> constructAST = new JDTASTConstruction().constructAST(project);
    final GeneratedSourceCode sourceCode = new GeneratedSourceCode(constructAST);
    return sourceCode;
  }

}
