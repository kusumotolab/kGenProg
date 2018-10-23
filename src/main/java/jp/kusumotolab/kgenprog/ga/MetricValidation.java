package jp.kusumotolab.kgenprog.ga;

import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class MetricValidation implements SourceCodeValidation {

  private static final Logger log = LoggerFactory.getLogger(MetricValidation.class);

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults) {
    if (!sourceCode.isGenerationSuccess()) {
      return new SimpleFitness(testResults.getSuccessRate());
    }

    // todo: retrieve appropriate class
    ProductSourcePath sourcePath = new ProductSourcePath(
        Paths.get("example/refactoring/GeometricMean/src/example/GeometricMean.java"));
    final GeneratedAST ast = sourceCode.getAst(sourcePath);

    log.debug("\n{}", ast.getSourceCode());

    final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
    final ComplexityScanner scanner = new ComplexityScanner();
    clazz.accept(scanner);

    final double fitness = scanner.getComplexity();

    return new MetricFitness(fitness, testResults.getSuccessRate());
  }
}
