package jp.kusumotolab.kgenprog;

import java.util.List;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
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

  public Strategies(final FaultLocalization faultLocalization,
      final JDTASTConstruction astConstruction, final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final TestExecutor testExecutor) {

    this.faultLocalization = faultLocalization;
    this.astConstruction = astConstruction;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.testExecutor = testExecutor;
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

  public Fitness execSourceCodeValidation(final VariantStore variantStore,
      final TestResults testResults) {
    return sourceCodeValidation.exec(variantStore, testResults);
  }

  public GeneratedSourceCode execASTConstruction(final TargetProject targetProject) {
    return new GeneratedSourceCode(astConstruction.constructAST(targetProject));
  }
}
