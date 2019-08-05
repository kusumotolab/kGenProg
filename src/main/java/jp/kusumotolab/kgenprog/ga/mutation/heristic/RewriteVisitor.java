package jp.kusumotolab.kgenprog.ga.mutation.heristic;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SimpleName;
import jp.kusumotolab.kgenprog.ga.mutation.Variable;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 変数名の書き換えをするASTVisitor
 */
public class RewriteVisitor extends ASTVisitor {

  private final ASTNode targetNode;
  private final Map<String, FullyQualifiedName> nameToFqnMap;
  private final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap;
  private final Random random;

  /**
   * コンストラクタ
   * @param node 書き換える対象のノード
   * @param nameToFqnMap 変数名と型のマッピング
   * @param fqnToNamesMap 書き換える場所で使える型と変数名のマッピング
   * @param random 乱数生成器
   */
  public RewriteVisitor(final ASTNode node, final Map<String, FullyQualifiedName> nameToFqnMap,
      final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap, final Random random) {
    // コピーした ASTNode を書き換えていく
    this.targetNode = ASTNode.copySubtree(node.getAST(), node);
    this.nameToFqnMap = nameToFqnMap;
    this.fqnToNamesMap = fqnToNamesMap;
    this.random = random;

    targetNode.accept(this);
  }

  @Override
  public boolean visit(final SimpleName node) {
    // 宣言部分(左辺)は無視する
    if (node.isDeclaration()) {
      return true;
    }

    // 書き換え対象の変数名を型に変換する
    final FullyQualifiedName fqn = nameToFqnMap.get(node.toString());
    if (fqn == null) {
      return true;
    }

    // 再利用先でアクセスできる変数名の一覧に変換
    List<Variable> variables = fqnToNamesMap.get(fqn);
    if (variables == null) {
      return true;
    }

    if (isLeftHandSideInAssignment(node)) {
      // 代入文の左辺なので final 修飾子がついてない変数をフィルターする
      variables = variables.stream()
          .filter(e -> !e.isFinal())
          .collect(Collectors.toList());
    }

    if (!variables.isEmpty()) {
      // ランダムに変数名を書き換える
      final Variable newName = variables.get(random.nextInt(variables.size()));
      node.setIdentifier(newName.getName());
    }

    return true;
  }

  private boolean isLeftHandSideInAssignment(final ASTNode node) {
    final ASTNode parent = node.getParent();
    return parent instanceof Assignment
        && ((Assignment) parent).getLeftHandSide()
        .equals(node);
  }

  /**
   * @return 変数名が書き換えられたノード
   */
  public ASTNode getRewritedNode() {
    return targetNode;
  }
}
