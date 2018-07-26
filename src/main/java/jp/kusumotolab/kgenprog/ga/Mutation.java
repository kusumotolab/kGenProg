package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public abstract class Mutation {

  private static Logger log = LoggerFactory.getLogger(Mutation.class);

  protected final List<Statement> candidates = new ArrayList<>();
  protected final RandomNumberGeneration randomNumberGeneration;
  protected final int numberOfBase;

  public Mutation(final int numberOfBase, final RandomNumberGeneration randomNumberGeneration) {
    this.randomNumberGeneration = randomNumberGeneration;
    this.numberOfBase = numberOfBase;
  }

  public void setCandidates(final List<GeneratedAST> candidates) {
    log.debug("enter setCandidates(List<>)");

    candidates.forEach(e -> {
          final CompilationUnit unit = ((GeneratedJDTAST) e).getRoot();
          final Visitor visitor = new Visitor();
          unit.accept(visitor);
          this.candidates.addAll(visitor.statements);
        });
    log.debug("exit setCandidates(List<>)");
  }

  public abstract List<Base> exec(List<Suspiciousness> suspiciousnesses);

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
