package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class StatementListVisitor extends ASTVisitor {

  private List<Statement> statements;
  private List<List<Statement>> lineToStatements;
  private CompilationUnit unit;


  public void analyzeStatement(CompilationUnit unit) {
    this.statements = new ArrayList<>();
    this.unit = unit;
    int lineNumberLength = unit.getLineNumber(unit.getLength() - 1);
    this.lineToStatements = IntStream.rangeClosed(0, lineNumberLength)
        .mapToObj(v -> new ArrayList<Statement>(0))
        .collect(Collectors.toList());

    unit.accept(this);
  }

  public List<Statement> getStatements() {
    return statements;
  }

  public List<List<Statement>> getLineToStatements() {
    return lineToStatements;
  }

  private void consumeStatement(Statement s) {
    statements.add(s);

    int begin = unit.getLineNumber(s.getStartPosition());
    int end = unit.getLineNumber(s.getStartPosition() + s.getLength()) + 1;

    lineToStatements.stream()
        .skip(begin)
        .limit(end - begin)
        .forEach(list -> list.add(s));
  }

  @Override
  public boolean visit(AssertStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(Block node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(BreakStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ConstructorInvocation node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ContinueStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(DoStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(EmptyStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(EnhancedForStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ExpressionStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ForStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(IfStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(LabeledStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ReturnStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(SuperConstructorInvocation node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(SwitchCase node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(SwitchStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(SynchronizedStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(ThrowStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(TryStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(TypeDeclarationStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(VariableDeclarationStatement node) {
    consumeStatement(node);
    return true;
  }

  @Override
  public boolean visit(WhileStatement node) {
    consumeStatement(node);
    return true;
  }
}
