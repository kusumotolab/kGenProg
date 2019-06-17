package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class HeuristicMutation extends RandomMutation {

  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();

  public HeuristicMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type) {
    super(mutationGeneratingCount, random, candidateSelection, type);
  }

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
    final List<Variable> variablesOfSelectedNode = variableSearcher.exec(selectedNode);
    final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap = queryVariables.stream()
        .collect(Collectors.groupingBy(Variable::getFqn));

    final Map<String, FullyQualifiedName> nameToFqnMap = variablesOfSelectedNode.stream()
        .collect(Collectors.toMap(Variable::getName, Variable::getFqn));

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

      final FullyQualifiedName fqn = nameToFqnMap.get(node.toString());
      if (fqn == null) {
        return true;
      }

      List<Variable> variables = fqnToNamesMap.get(fqn);
      if (variables == null) {
        return true;
      }

      final ASTNode parent = node.getParent();
      if (parent instanceof Assignment && ((Assignment) parent).getLeftHandSide()
          .equals(node)) {
        variables = variables.stream()
            .filter(e -> !e.isFinal())
            .collect(Collectors.toList());
      }

      if (!variables.isEmpty()) {
        final Variable newName = variables.get(random.nextInt(variables.size()));
        node.setIdentifier(newName.getName());
      }

      return true;
    }

    public ASTNode getRewritedNode() {
      return targetNode;
    }
  }
}
