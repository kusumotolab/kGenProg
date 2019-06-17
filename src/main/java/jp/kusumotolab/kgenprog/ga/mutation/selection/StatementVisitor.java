package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
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

/**
 * ステートメントを探索するビジター
 */
public class StatementVisitor extends ASTVisitor {

  private final List<Statement> statements = new ArrayList<>();

  /**
   * ASTNode の中に含まれるステートメントを探索する
   *
   * @param node 探索するノード(このノードの中のステートメントを探索する)
   */
  public StatementVisitor(final ASTNode node) {
    node.accept(this);
  }

  private void addStatement(final Statement statement) {
    statements.add(statement);
  }

  /**
   * @return ステートメントのリストを返す
   */
  public List<Statement> getStatements() {
    return statements;
  }

  @Override
  public boolean visit(final AssertStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final BreakStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final ContinueStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final DoStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final EmptyStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final ExpressionStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final ForStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final IfStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final ReturnStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final SwitchStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final SynchronizedStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final ThrowStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final TryStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final VariableDeclarationStatement node) {
    addStatement(node);
    return super.visit(node);
  }

  @Override
  public boolean visit(final WhileStatement node) {
    addStatement(node);
    return super.visit(node);
  }
}
