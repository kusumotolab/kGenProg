package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

/**
 * アクセスすることができる変数を探すクラス
 * バインディングしないこと前提に作っているため，探索範囲は同じクラスのみ
 * ローカル変数，引数，フィールド変数を探す
 */
public class AccessibleVariableSearcher {

  /**
   * 引数で与えたノードでアクセスできるノードを探す
   *
   * @param node 探索の始点となるノード
   * @return アクセスできる変数のリスト
   */
  public List<Variable> exec(final ASTNode node) {
    return searchRecursively(node);
  }

  /**
   * 引数で与えた位置でアクセスできるノードを探す
   *
   * @param location 探索の始点となる位置
   * @return アクセスできる変数のリスト
   */
  public List<Variable> exec(final ASTLocation location) {
    if (!(location instanceof JDTASTLocation)) {
      throw new IllegalArgumentException("location must be JDTASTLocation");
    }
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    return exec(jdtastLocation.getNode());
  }

  private List<Variable> searchRecursively(final ASTNode node) {
    final ASTNode parent = node.getParent();
    final List<Variable> results = new ArrayList<>();
    if (parent instanceof MethodDeclaration) {
      final List<Variable> variables = extractFromMethodDeclaration(((MethodDeclaration) parent));
      results.addAll(variables);
    } else if (parent != null) {
      final List<Variable> parentVariables = searchRecursively(parent);
      results.addAll(parentVariables);
    }
    final List<Variable> variables = search(node);
    results.addAll(variables);
    return results;
  }

  private List<Variable> search(final ASTNode node) {
    final List<Variable> variables = new ArrayList<>();
    final ASTNode parent = node.getParent();

    // 親がBlockでないのなら引数のnode以外にstatementを持たないので空のListを返す
    if (!(parent instanceof Block)) {
      return variables;
    }
    final Block block = (Block) parent;
    for (final Object statement : block.statements()) {
      // 引数で与えられたnodeまでに含まれる変数宣言を探す
      if (statement.equals(node)) {
        break;
      }

      if (statement instanceof VariableDeclarationStatement) {
        final VariableDeclarationStatement vdStatement = (VariableDeclarationStatement) statement;
        final List<Variable> extractVariables = extractFromVariableDeclarationStatement(
            vdStatement);
        variables.addAll(extractVariables);
      }
    }
    return variables;
  }

  private List<Variable> extractFromVariableDeclarationStatement(
      final VariableDeclarationStatement vdStatement) {
    final List<Variable> variables = new ArrayList<>();
    final Type type = vdStatement.getType();
    for (final Object fragment : vdStatement.fragments()) {
      if (fragment instanceof VariableDeclarationFragment) {
        final VariableDeclarationFragment vdFragment = (VariableDeclarationFragment) fragment;
        final SimpleName name = vdFragment.getName();
        final FullyQualifiedName fqn = new TargetFullyQualifiedName(type.toString());
        final Variable variable = new Variable(name.getIdentifier(), fqn,
            Modifier.isFinal(vdStatement.getModifiers()));
        variables.add(variable);
      } else {
        // 基本的にはここには入らないはず
        // 入った場合はその型に応じた処理を書く
        throw new RuntimeException("Not Implemented");
      }
    }
    return variables;
  }

  private List<Variable> extractFromMethodDeclaration(final MethodDeclaration node) {
    final List<Variable> results = new ArrayList<>();

    for (final Object parameter : node.parameters()) {
      if (!(parameter instanceof SingleVariableDeclaration)) {
        throw new RuntimeException("Not Implemented");
      }
      final SingleVariableDeclaration vDeclaration = (SingleVariableDeclaration) parameter;
      final SimpleName name = vDeclaration.getName();
      final Type type = vDeclaration.getType();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(type.toString());
      results.add(
          new Variable(name.toString(), fqn, Modifier.isFinal(vDeclaration.getModifiers())));
    }
    final ASTNode parent = node.getParent();
    if (parent instanceof TypeDeclaration) {
      final List<Variable> variables = extractFromTypeDeclaration(((TypeDeclaration) parent));
      results.addAll(variables);
    }
    return results;
  }

  private List<Variable> extractFromTypeDeclaration(final TypeDeclaration node) {
    final List<Variable> results = new ArrayList<>();
    for (final FieldDeclaration fieldDeclaration : node.getFields()) {
      final Type type = fieldDeclaration.getType();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(type.toString());
      for (final Object object : fieldDeclaration.fragments()) {
        if (!(object instanceof VariableDeclarationFragment)) {
          throw new RuntimeException("Not Implemented");
        }
        final VariableDeclarationFragment fragment = (VariableDeclarationFragment) object;
        final SimpleName name = fragment.getName();
        results.add(
            new Variable(name.toString(), fqn, Modifier.isFinal(fieldDeclaration.getModifiers())));
      }
    }
    return results;
  }
}
