package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
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
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class StatementVisitor extends ASTVisitor {

  private List<Statement> statements = new ArrayList<>();

  public StatementVisitor(final Statement statement) {
    statement.accept(this);
  }

  public StatementVisitor(final List<GeneratedAST<ProductSourcePath>> generatedASTS) {
    for (GeneratedAST<ProductSourcePath> generatedAST : generatedASTS) {
      final CompilationUnit unit = ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot();
      unit.accept(this);
    }
  }

  private void addStatement(Statement statement) {
    statements.add(statement);
  }

  public List<Statement> getStatements() {
    return statements;
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
