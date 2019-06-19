package jp.kusumotolab.kgenprog.ga.mutation.selection;

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
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

/**
 * ステートメントを探索するビジター
 */
public class StatementVisitor extends ASTVisitor {

  private final List<ReuseCandidate<Statement>> reuseCandidateList = new ArrayList<>();
  private FullyQualifiedName fqn;

  /**
   * ステートメントの中に含まれるステートメントを探索する
   *
   * @param statement 探索するステートメント(このステートメントの中ののステートメントを探索する)
   * @param fqn 探索するステートメントの fqn
   */
  public StatementVisitor(final Statement statement, final FullyQualifiedName fqn) {
    this.fqn = fqn;
    statement.accept(this);
  }

  /**
   * 引数で与えられたソースごとのリストの中からステートメントを探索する
   *
   * @param generatedASTS この中からステートメントを探索する
   */
  public StatementVisitor(final List<GeneratedAST<ProductSourcePath>> generatedASTS) {
    for (GeneratedAST<ProductSourcePath> generatedAST : generatedASTS) {
      this.fqn = generatedAST.getPrimaryClassName();
      final CompilationUnit unit = ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot();
      unit.accept(this);
    }
  }

  private void addStatement(final Statement statement) {
    final String packageName = fqn.getPackageName();
    reuseCandidateList.add(new ReuseCandidate<>(statement, packageName, fqn));
  }

  /**
   * @return 再利用候補のリストを返す
   */
  public List<ReuseCandidate<Statement>> getReuseCandidateList() {
    return reuseCandidateList;
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
