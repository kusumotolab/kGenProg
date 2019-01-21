package jp.kusumotolab.kgenprog.testutil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.fl.Ample;
import jp.kusumotolab.kgenprog.fl.Jaccard;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.fl.Tarantula;
import jp.kusumotolab.kgenprog.fl.Zoltar;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class TestUtil {

  public static Variant createVariant(final Configuration config) {
    final Gene gene = new Gene(Collections.emptyList());
    final GeneratedSourceCode sourceCode = createGeneratedSourceCode(config.getTargetProject());
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(sourceCode);
    final TestResults testResults = new LocalTestExecutor(config).exec(variant);
    final Fitness fitness = new DefaultCodeValidation().exec(null, testResults);
    final List<Suspiciousness> suspiciousnesses = config.getFaultLocalization().initialize().exec(sourceCode, testResults);
    final HistoricalElement element = new OriginalHistoricalElement();
    return new Variant(0, 0, gene, sourceCode, testResults, fitness, suspiciousnesses, element);
  }

  public static GeneratedSourceCode createGeneratedSourceCode(final TargetProject project) {
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(project);
    return sourceCode;
  }

}
