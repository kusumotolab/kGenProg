package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import spoon.Launcher;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.CtScanner;

public class MetricValidation implements SourceCodeValidation {

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults) {
    // todo: retrieve appropriate class
    final GeneratedAST ast = sourceCode.getAsts()
        .get(0);
    final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
    final ComplexityScanner scanner = new ComplexityScanner();

    clazz.accept(scanner);
    final double fitness = 1.0 / scanner.getComplexity();

    return new SimpleFitness(fitness);
  }

  private static class ComplexityScanner extends CtScanner {

    private int complexity = 1;

    public int getComplexity() {
      return complexity;
    }

    @Override
    public void visitCtIf(final CtIf ifElement) {
      ++complexity;
      super.visitCtIf(ifElement);
    }

    @Override
    public void visitCtFor(final CtFor forLoop) {
      ++complexity;
      super.visitCtFor(forLoop);
    }

    @Override
    public void visitCtForEach(final CtForEach foreach) {
      ++complexity;
      super.visitCtForEach(foreach);
    }

    @Override
    public void visitCtWhile(final CtWhile whileLoop) {
      ++complexity;
      super.visitCtWhile(whileLoop);
    }

    @Override
    public void visitCtDo(final CtDo whileLoop) {
      ++complexity;
      super.visitCtDo(whileLoop);
    }

    @Override
    public <T> void visitCtConditional(final CtConditional<T> conditional) {
      ++complexity;
      super.visitCtConditional(conditional);
    }
  }
}
