package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation implements Mutation {

  private List<Statement> candidates = new ArrayList<>();
  private final RandomNumberGeneration randomNumberGeneration;

  public RandomMutation() {
    this.randomNumberGeneration = new RandomNumberGeneration();
  }

  public RandomMutation(RandomNumberGeneration randomNumberGeneration) {
    this.randomNumberGeneration = randomNumberGeneration;
  }

  @Override
  public void setCandidates(List<GeneratedAST> candidates) {
    candidates.stream().sorted(Comparator.comparing(x -> x.getSourceFile().path)).forEach(e -> {
      final CompilationUnit unit = ((GeneratedJDTAST) e).getRoot();
      final Visitor visitor = new Visitor();
      unit.accept(visitor);
      this.candidates.addAll(visitor.statements);
    });
  }

  @Override
  public List<Base> exec(List<Suspiciouseness> suspiciousenesses) {
    List<Base> bases = suspiciousenesses.stream()
        .sorted(Comparator.comparingDouble(Suspiciouseness::getValue).reversed())
        .map(this::makeBase).collect(Collectors.toList());
    return bases;
  }

  private Base makeBase(Suspiciouseness suspiciouseness) {
    return new Base(suspiciouseness.getLocation(), makeOperationAtRandom());
  }

  private Operation makeOperationAtRandom() {
    final int randomNumber = randomNumberGeneration.getRandomNumber(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertOperation(chooseNodeAtRandom());
      case 2:
        return new ReplaceOperation(chooseNodeAtRandom());
    }
    return new NoneOperation();
  }

  private Statement chooseNodeAtRandom() {
    return candidates.get(randomNumberGeneration.getRandomNumber(candidates.size()));
  }

  private class Visitor extends ASTVisitor {

    private List<Statement> statements = new ArrayList<>();

    private void addStatement(Statement statement) {
      statements.add(statement);
    }

    @Override
    public boolean visit(AssertStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(BreakStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ContinueStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(EmptyStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ExpressionStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(IfStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(SwitchStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(TryStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
      addStatement(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(WhileStatement node) {
      addStatement(node);
      return super.visit(node);
    }
  }
}
