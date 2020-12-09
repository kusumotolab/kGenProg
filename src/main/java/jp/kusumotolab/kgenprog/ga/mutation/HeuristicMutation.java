package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import com.google.common.collect.Lists;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.DeleteOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.InsertAfterOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.InsertBeforeOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.OperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.ReplaceOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.RewriteVisitor;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

/**
 * ヒューリティクスを適用してビルドサクセスの数を増やす Mutation
 * 現状，修正対象の行でアクセスできる変数名に書き換えて変異処理を行う
 */
public class HeuristicMutation extends Mutation {

  protected final Scope.Type scopeType;
  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();
  private final List<OperationGenerator> operationGenerators;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する Variant の数
   * @param random 乱数生成器
   * @param candidateSelection 再利用候補の行を選択するクラス
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   * @param scopeType 再利用するスコープのタイプ
   */
  public HeuristicMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final int requiredSolutions,
      final Scope.Type scopeType) {
    super(mutationGeneratingCount, random, candidateSelection, requiredSolutions);
    this.scopeType = scopeType;

    operationGenerators = Lists.newArrayList(
        new DeleteOperationGenerator(1.0d),
        new InsertBeforeOperationGenerator(0.5d),
        new InsertAfterOperationGenerator(0.5d),
        new ReplaceOperationGenerator(1.0d)
    );
  }

  @Override
  protected Operation makeOperation(final ASTLocation location) {
    if (!(location instanceof JDTASTLocation)) {
      throw new IllegalArgumentException("location must be JDTASTLocation");
    }

    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    // その場で使える操作だけを取り出す
    final List<OperationGenerator> usableOperationGenerators = this.operationGenerators.stream()
        .filter(e -> e.canBeApply(jdtastLocation))
        .collect(Collectors.toList());

    // 使える操作に対して重みをつけてルーレット選択
    final Roulette<OperationGenerator> roulette = new Roulette<>(usableOperationGenerators,
        OperationGenerator::getWeight, random);
    final OperationGenerator operationGenerator = roulette.exec();

    final ASTNode nodeForReuse = operationGenerator.chooseNodeForReuse(candidateSelection, location,
        scopeType); // 再利用するノードの選択
    final ASTNode rewritedNode = rewrite(nodeForReuse,
        variableSearcher.exec(location)); // 再利用するノードを書き換え
    return operationGenerator.generate(jdtastLocation, rewritedNode); // 操作の生成
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
}
