package jp.kusumotolab.kgenprog;

import java.util.List;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Strategies {

  private final FaultLocalization faultLocalization;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final JDTASTConstruction astConstruction;
  private final SourceCodeValidation sourceCodeValidation;
  private final TestExecutor testExecutor;
  private final VariantSelection variantSelection;

  public Strategies(final FaultLocalization faultLocalization,
      final JDTASTConstruction astConstruction, final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final TestExecutor testExecutor,
      final VariantSelection variantSelection) {

    this.faultLocalization = faultLocalization;
    this.astConstruction = astConstruction;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.testExecutor = testExecutor;
    this.variantSelection = variantSelection;
  }

  public List<Suspiciousness> execFaultLocalization(final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults) {
    return faultLocalization.exec(generatedSourceCode, testResults);
  }

  public GeneratedSourceCode execSourceCodeGeneration(final VariantStore variantStore,
      final Gene gene) {
    return sourceCodeGeneration.exec(variantStore, gene);
  }

  public TestResults execTestExecutor(final GeneratedSourceCode generatedSourceCode) {
    return testExecutor.exec(generatedSourceCode);
  }

  public Fitness execSourceCodeValidation(final GeneratedSourceCode sourceCode,
      final TestResults testResults) {
    return sourceCodeValidation.exec(sourceCode, testResults);
  }

  public GeneratedSourceCode execASTConstruction(final TargetProject targetProject) {
    return astConstruction.constructAST(targetProject);
  }

  public List<Variant> execVariantSelection(final List<Variant> current,
      final List<Variant> generated) {
    return variantSelection.exec(current, generated);
  }
}
