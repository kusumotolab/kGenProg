package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import com.google.common.collect.Lists;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.ASTAnalyzer;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.DeleteOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.InsertAfterOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.InsertBeforeOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.OperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.ReplaceOperationGenerator;
import jp.kusumotolab.kgenprog.ga.mutation.heristic.RewriteVisitor;
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

  protected final Type type;
  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();
  private final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
  private final List<OperationGenerator> operationGenerators = Lists.newArrayList(
      new DeleteOperationGenerator(1.0d),
      new InsertBeforeOperationGenerator(0.5d),
      new InsertAfterOperationGenerator(0.5d),
      new ReplaceOperationGenerator(1.0d)
  );

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
    super(mutationGeneratingCount, random, candidateSelection, needHistoricalElement);
    this.type = type;
  }

  @Override
  protected Operation makeOperation(final ASTLocation location) {
    if (!(location instanceof JDTASTLocation)) {
      throw new IllegalArgumentException("location must be JDTASTLocation");
    }

    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final List<OperationGenerator> operationGenerators = this.operationGenerators.stream()
        .filter(e -> e.enable(jdtastLocation))
        .collect(Collectors.toList());

    final int randomNumber = random.nextInt(operationGenerators.size());
    final OperationGenerator operationGenerator = operationGenerators.get(randomNumber);
    final ASTNode nodeForReuse = operationGenerator.chooseNodeForReuse(candidateSelection, location,
        type);
    final ASTNode rewritedNode = rewrite(nodeForReuse, variableSearcher.exec(location));
    return operationGenerator.generate(jdtastLocation, rewritedNode);
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
