package jp.kusumotolab.kgenprog.testutil;

import java.util.Random;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;

public class TestUtil {

  public static Variant createVariant(final Configuration config) {
    return createVariantStoreWithDefaultStrategies(config).getInitialVariant();
  }

  public static VariantStore createVariantStoreWithDefaultStrategies(final Configuration config) {
    final Strategies strategies = createDefaultStrategies(config);
    return new VariantStore(config, strategies);
  }

  public static GeneratedSourceCode createGeneratedSourceCode(final TargetProject project) {
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(project);
    return sourceCode;
  }

  private static Strategies createDefaultStrategies(final Configuration config) {
    return new Strategies(new Ochiai(), new JDTASTConstruction(), new DefaultSourceCodeGeneration(),
        new DefaultCodeValidation(), new LocalTestExecutor(config),
        new DefaultVariantSelection(0, new Random(0)));
  }
}
