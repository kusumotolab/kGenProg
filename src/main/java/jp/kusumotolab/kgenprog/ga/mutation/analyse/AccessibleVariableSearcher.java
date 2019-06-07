package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class AccessibleVariableSearcher {

  public List<Variable> exec(final ASTLocation location) {
    if (!(location instanceof JDTASTLocation)) {
      throw new IllegalArgumentException("location must be implemented JDTASTLocation");
    }
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    return recursivelySearch(jdtastLocation.getNode());
  }

  private List<Variable> recursivelySearch(final ASTNode node) {
    final ASTNode parent = node.getParent();
    final List<Variable> results = new ArrayList<>();
    if (!(parent instanceof MethodDeclaration)) {
      final List<Variable> parentVariables = recursivelySearch(parent);
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

      if (statement instanceof VariableDeclarationStatement) {
        final VariableDeclarationStatement vdStatement = (VariableDeclarationStatement) statement;
        final List<Variable> extractVariables = extractVariables(vdStatement);
        variables.addAll(extractVariables);
      }

      // 引数で与えられたnodeまでに含まれる変数宣言を探す
      if (statement.equals(node)) {
        break;
      }
    }
    return variables;
  }

  private List<Variable> extractVariables(final VariableDeclarationStatement vdStatement) {
    final List<Variable> variables = new ArrayList<>();
    final Type type = vdStatement.getType();
    for (final Object fragment : vdStatement.fragments()) {
      if (fragment instanceof VariableDeclarationFragment) {
        final VariableDeclarationFragment vdFragment = (VariableDeclarationFragment) fragment;
        final SimpleName name = vdFragment.getName();
        final FullyQualifiedName fqn = new TargetFullyQualifiedName(type.toString());
        final Variable variable = new Variable(name.getIdentifier(), fqn);
        variables.add(variable);
      } else {
        // 基本的にはここには入らないはず
        // 入った場合はその型に応じた処理を書く
        throw new UnsupportedOperationException();
      }
    }
    return variables;
  }
}
