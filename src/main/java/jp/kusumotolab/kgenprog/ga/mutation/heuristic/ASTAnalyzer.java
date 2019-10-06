package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

/**
 * ASTの構造を解析するクラス
 */
public class ASTAnalyzer {

  /**
   * 対象のノードがメソッドの中で一番最後に実行されるステートメントかどうか
   *
   * @param statement 対象のステートメント
   * @return ステートメントがメソッドの中で最後かどうか
   */
  public boolean isLastStatement(final Statement statement) {
    ASTNode node = statement;
    while (!(node instanceof MethodDeclaration) && !(node instanceof LambdaExpression)
        && !(node instanceof Initializer)) {
      final ASTNode parent = node.getParent();
      if (parent instanceof Block) {
        final Block block = (Block) parent;
        final List statements = block.statements();
        final Object lastObject = statements.get(statements.size() - 1);
        if (!lastObject.equals(node)) {
          return false;
        }
      }
      node = parent;
    }
    return true;
  }

  /**
   * @param node ノード
   * @return ノードが含まれているメソッドがの返り値がvoidかどうか
   */
  public boolean isVoidMethod(final ASTNode node) {
    final FullyQualifiedName returnType = getReturnType(node);
    if (returnType == null) {
      return false;
    }
    return returnType
        .toString()
        .toLowerCase()
        .equals("void");
  }

  /**
   * @param node ノード
   * @return 引数のノードが含まれているメソッドの返り値の型
   */
  public FullyQualifiedName getReturnType(final ASTNode node) {
    ASTNode n = node;
    while (!(n instanceof MethodDeclaration) && !(n instanceof LambdaExpression)
        && !(node instanceof Initializer)) {
      n = n.getParent();
    }
    if (n instanceof LambdaExpression || n instanceof Initializer) {
      return null;
    }
    final MethodDeclaration methodDeclaration = (MethodDeclaration) n;
    if (methodDeclaration.isConstructor()) {
      return null;
    }
    final String type = methodDeclaration.getReturnType2()
        .toString();
    return new TargetFullyQualifiedName(type);
  }

  /**
   * @param statement 対象のステートメント
   * @return 対象のステートメントの後ろに挿入可能かどうか
   */
  public boolean canInsertAfter(final Statement statement) {
    if (statement instanceof IfStatement) {
      return canInsertAfterIfStatement(((IfStatement) statement));
    } else if (statement instanceof SwitchStatement) {
      return canInsertAfterSwitchStatement(((SwitchStatement) statement));
    } else if (statement instanceof TryStatement) {
      return canInsertAfterTryStatement(((TryStatement) statement));
    } else if (statement instanceof Block) {
      return canInsertAfterBlock(((Block) statement));
    }
    return !(statement instanceof ReturnStatement)
        && !(statement instanceof ThrowStatement)
        && !(statement instanceof BreakStatement)
        && !(statement instanceof ContinueStatement);
  }

  private boolean canInsertAfterIfStatement(final IfStatement statement) {
    final Statement thenStatement = statement.getThenStatement();
    if (canInsertAfter(thenStatement)) {
      return true;
    }
    final Statement elseStatement = statement.getElseStatement();
    return canInsertAfter(elseStatement);
  }

  @SuppressWarnings("unchecked")
  private boolean canInsertAfterSwitchStatement(final SwitchStatement statement) {
    final List<Object> statements = statement.statements();

    // default: が無い場合
    final boolean hasDefault = statements.stream()
        .filter(e -> e instanceof SwitchCase)
        .anyMatch(e -> ((SwitchCase) e).isDefault());
    if (!hasDefault) {
      return true;
    }

    // break文があるかどうか
    final boolean hasBreak = statements.stream()
        .anyMatch(e -> e instanceof BreakStatement);
    if (hasBreak) {
      return true;
    }

    final Object lastStatement = statements.get(statements.size() - 1);
    if (lastStatement instanceof Statement) {
      return canInsertAfter(((Statement) lastStatement));
    }
    throw new RuntimeException(lastStatement.getClass() + " is not supported.");
  }

  @SuppressWarnings("unchecked")
  private boolean canInsertAfterTryStatement(final TryStatement statement) {
    final boolean tryStatementResult = canInsertAfter(statement);
    final boolean catchResult = statement.catchClauses()
        .stream()
        .anyMatch(e -> canInsertAfter(((Statement) e)));
    if (!tryStatementResult && !catchResult) {
      return false;
    }
    return canInsertAfter(statement.getFinally());
  }

  private boolean canInsertAfterBlock(final Block statement) {
    final List statements = statement.statements();
    if (statements.isEmpty()) {
      return true;
    }
    return canInsertAfter(((Statement) statements.get(statements.size() - 1)));
  }

  /**
   * @param node 対象のノード
   * @return Break文を挿入できるかどうか
   */
  public boolean canInsertBreak(final ASTNode node) {
    if (!isInLoopOrSWitch(node)) {
      return false;
    }
    return isLastStatementInParent(node);
  }

  /**
   * @param node 対象のノード
   * @return 対象のノードがループかSwitch文の中にいるかどうか
   */
  public boolean isInLoopOrSWitch(final ASTNode node) {
    ASTNode parent = node.getParent();
    while (!(parent instanceof MethodDeclaration) && !(parent instanceof LambdaExpression)) {
      if (parent instanceof SwitchStatement
          || parent instanceof ForStatement
          || parent instanceof EnhancedForStatement
          || parent instanceof WhileStatement) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  /**
   * @param node 対象のノード
   * @return continueを挿入できるかどうか
   */
  public boolean canInsertContinue(final ASTNode node) {
    if (!isInLoop(node)) {
      return false;
    }
    return isLastStatementInParent(node);
  }

  /**
   * @param node 対象のノード
   * @return 対象のノードが親ノードであるBlockの中で一番最後かどうか
   */
  public boolean isLastStatementInParent(final ASTNode node) {
    final ASTNode parent = node.getParent();

    if (!(parent instanceof Block)) {
      return false;
    }
    final Block block = (Block) parent;
    final List statements = block.statements();
    final Object last = statements.get(statements.size() - 1);
    return last.equals(node);
  }

  /**
   * @param node 対象のノード
   * @return 対象のノードがループの中にあるかどうか
   */
  private boolean isInLoop(final ASTNode node) {
    ASTNode parent = node.getParent();
    while (!(parent instanceof MethodDeclaration) && !(parent instanceof LambdaExpression)) {
      if (parent instanceof ForStatement
          || parent instanceof EnhancedForStatement
          || parent instanceof WhileStatement) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }
}
