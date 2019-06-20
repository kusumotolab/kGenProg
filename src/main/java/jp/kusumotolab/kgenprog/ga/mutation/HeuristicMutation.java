package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SimpleName;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * ヒューリティクスを適用してビルドサクセスの数を増やす Mutation 現状，修正対象の行でアクセスできる変数名に書き換えて変異処理を行う
 */
public class HeuristicMutation extends RandomMutation {

  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する Variant の数
   * @param random 乱数生成器
   * @param candidateSelection 再利用候補の行を選択するクラス
   * @param type 再利用するスコープのタイプ
   */
  public HeuristicMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type,
      final boolean needHistoricalElement) {
    super(mutationGeneratingCount, random, candidateSelection, type, needHistoricalElement);
  }

  /**
   * 再利用候補の ASTNode を返すメソッド CandidateSelection で選択したステートメントその場で利用できる変数名に書き換えて返す
   *
   * @param location 再利用先
   * @return 再利用されるステートメント
   */
  @Override
  protected ASTNode chooseNodeForReuse(final ASTLocation location) {
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final List<Variable> variables = variableSearcher.exec(location);
    final Query query = new Query(variables, scope);
    final ASTNode selectedNode = candidateSelection.exec(query);
    return rewrite(selectedNode, variables);
  }

  private ASTNode rewrite(final ASTNode selectedNode, final List<Variable> queryVariables) {
    // 「再利用先で使える型」を「再利用先で使える変数名のリスト」に変換するマップ
    final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap = queryVariables.stream()
        .collect(Collectors.groupingBy(Variable::getFqn));

    final List<Variable> variablesOfSelectedNode = variableSearcher.exec(selectedNode);
    // 「再利用するノード内にある変数名」を「型」に変換するマップ
    final Map<String, FullyQualifiedName> nameToFqnMap = variablesOfSelectedNode.stream()
        .collect(Collectors.toMap(Variable::getName, Variable::getFqn, (o1, o2) -> o1));

    final RewriteVisitor rewriteVisitor = new RewriteVisitor(selectedNode, nameToFqnMap,
        fqnToNamesMap, random);
    return rewriteVisitor.getRewritedNode();
  }

  private class RewriteVisitor extends ASTVisitor {

    private final ASTNode targetNode;
    private final Map<String, FullyQualifiedName> nameToFqnMap;
    private final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap;
    private final Random random;

    RewriteVisitor(final ASTNode node, final Map<String, FullyQualifiedName> nameToFqnMap,
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

    public ASTNode getRewritedNode() {
      return targetNode;
    }
  }
}
