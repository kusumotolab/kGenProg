package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class StatementAndConditionVisitor extends ProgramElementVisitor {

  @Override
  public boolean visit(AssertStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(Block node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(BreakStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ConstructorInvocation node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ContinueStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(DoStatement node) {
    return true;
  }

  @Override
  public void endVisit(final DoStatement node) {
    consumeElement(node.getExpression()); // Blockよりも後に入れるためにendVisitで行う
    super.endVisit(node);
  }

  @Override
  public boolean visit(EmptyStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(EnhancedForStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ExpressionStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ForStatement node) {
    return true;
  }

  @Override
  public void endVisit(final ForStatement node) {
    final Expression expression = node.getExpression();
    if(null != expression) {
      consumeElement(expression); // Blockよりも後に入れるためにendVisitで行う
    }
    super.endVisit(node);
  }

  @Override
  public boolean visit(IfStatement node) {
    return true;
  }

  @Override
  public void endVisit(final IfStatement node) {
    consumeElement(node.getExpression()); // Blockよりも後に入れるためにendVisitで行う
    super.endVisit(node);
  }

  @Override
  public boolean visit(LabeledStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ReturnStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(SuperConstructorInvocation node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(SwitchCase node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(SwitchStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(SynchronizedStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(ThrowStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(TryStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(TypeDeclarationStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(VariableDeclarationStatement node) {
    consumeElement(node);
    return true;
  }

  @Override
  public boolean visit(WhileStatement node) {
    return true;
  }

  @Override
  public void endVisit(final WhileStatement node) {
    consumeElement(node.getExpression()); // Blockよりも後に入れるためにendVisitで行う
    super.endVisit(node);
  }
}
