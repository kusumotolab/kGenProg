package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class MetricValidation implements SourceCodeValidation {

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults) {
    if (!sourceCode.isGenerationSuccess()) {
      return new SimpleFitness(testResults.getSuccessRate());
    }

    // todo: retrieve appropriate class
    final GeneratedAST ast = sourceCode.getAsts()
        .get(0);
    final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
    final ComplexityScanner scanner = new ComplexityScanner();

    clazz.accept(scanner);

    return new MetricFitness(scanner.getComplexity());
  }
}
