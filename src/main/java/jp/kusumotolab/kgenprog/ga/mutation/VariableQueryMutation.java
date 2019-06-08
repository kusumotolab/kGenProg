package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class VariableQueryMutation extends RandomMutation<List<Variable>> {

  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();

  public VariableQueryMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection<List<Variable>> candidateSelection, final Type type) {
    super(mutationGeneratingCount, random, candidateSelection, type);
  }

  @Override
  public Base makeBase(final Suspiciousness suspiciousness) {
    final ASTLocation location = suspiciousness.getLocation();
    return new Base(location, makeOperationAtRandom(location));
  }

  private Operation makeOperationAtRandom(final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertOperation(chooseNodeForReuse(location));
      case 2:
        return new ReplaceOperation(chooseNodeForReuse(location));
    }
    return new NoneOperation();
  }

  protected ASTNode chooseNodeForReuse(final ASTLocation location) {
    final List<Variable> variables = variableSearcher.exec(location);
    return candidateSelection.exec(variables);
  }
}
