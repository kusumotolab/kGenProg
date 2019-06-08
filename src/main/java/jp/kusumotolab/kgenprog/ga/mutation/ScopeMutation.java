package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class ScopeMutation extends RandomMutation<Scope> {

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param type 選択する候補のスコープ
   */
  public ScopeMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection<Scope> candidateSelection,
      final Type type) {
    super(mutationGeneratingCount, random, candidateSelection, type);
  }

  @Override
  public Base makeBase(final Suspiciousness suspiciousness) {
    final ASTLocation location = suspiciousness.getLocation();
    final GeneratedAST<?> generatedAST = location.getGeneratedAST();
    final FullyQualifiedName fqn = generatedAST.getPrimaryClassName();
    return new Base(location, makeOperationAtRandom(fqn, location));
  }

  private Operation makeOperationAtRandom(final FullyQualifiedName fqn, final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertOperation(chooseNodeForReuse(fqn, location));
      case 2:
        return new ReplaceOperation(chooseNodeForReuse(fqn, location));
    }
    return new NoneOperation();
  }

  protected ASTNode chooseNodeForReuse(final FullyQualifiedName fqn, final ASTLocation location) {
    final Scope scope = new Scope(type, fqn);
    return candidateSelection.exec(scope);
  }
}
