package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

/**
 * 乱数に基づいて変異処理をするクラス
 *
 * @see Mutation
 */
public class SimpleMutation extends Mutation {

  protected final Type type;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param type 選択する候補のスコープ
   * @param noHistoryRecord 個体が生成される過程を記録するか否か
   */
  public SimpleMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type,
      final boolean noHistoryRecord) {
    super(mutationGeneratingCount, random, candidateSelection, noHistoryRecord);
    this.type = type;
  }

  protected Operation makeOperation(final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return random.nextBoolean() ?
            new InsertAfterOperation(chooseNodeForReuse(location, InsertAfterOperation.class))
            : new InsertBeforeOperation(chooseNodeForReuse(location, InsertBeforeOperation.class));
      case 2:
        final ASTNode node = chooseNodeForReuse(location, ReplaceOperation.class);
        return new ReplaceOperation(node);
    }
    return new NoneOperation();
  }

  protected ASTNode chooseNodeForReuse(final ASTLocation location,
      final Class<? extends Operation> operationClass) {
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final Query query = new Query(scope);
    return candidateSelection.exec(query);
  }
}
