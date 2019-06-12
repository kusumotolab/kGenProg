package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * 型を考慮してStatementを選ぶ． Statementを選んで返す時に変数名を書き換えて返す
 */
public class VariableNormalizeSelection implements CandidateSelection<List<Variable>> {

  private final AccessibleVariableSearcher accessibleVariableSearcher = new AccessibleVariableSearcher();
  private final List<Candidate> candidates = new ArrayList<>();
  private final Random random;

  /**
   * @param random 乱数生成器
   */
  public VariableNormalizeSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> generatedASTS) {
    final StatementVisitor visitor = new StatementVisitor(generatedASTS);
    final List<ReuseCandidate<Statement>> statementCandidates = visitor.getReuseCandidateList();

    for (final ReuseCandidate<Statement> statementCandidate : statementCandidates) {
      final Statement statement = statementCandidate.getValue();
      final List<Variable> accessibleVariables = accessibleVariableSearcher.exec(statement);
      final CandidateVisitor candidateVisitor = new CandidateVisitor(statement,
          accessibleVariables);
      final List<String> names = candidateVisitor.names;

      final List<Variable> variables = accessibleVariables.stream()
          .filter(e -> names.contains(e.getName()))
          .collect(Collectors.toList());
      final Candidate candidate = new Candidate(statementCandidate, variables);
      candidates.add(candidate);
    }
  }

  @Override
  public ASTNode exec(final List<Variable> queryVariables) {
    final List<String> queryFQNs = queryVariables.stream()
        .map(Variable::getFqn)
        .map(FullyQualifiedName::toString)
        .collect(Collectors.toList());

    final List<Candidate> matchedCandidates = candidates.stream()
        .filter(candidate -> {
          final List<String> candidateFQNs = candidate.includingVariables.stream()
              .map(Variable::getFqn)
              .map(FullyQualifiedName::toString)
              .collect(Collectors.toList());
          return queryFQNs.containsAll(candidateFQNs);
        })
        .collect(Collectors.toList());

    final Candidate candidate = matchedCandidates.get(random.nextInt(matchedCandidates.size()));

    final Map<String, List<Variable>> fqnToNamesMap = queryVariables.stream()
        .collect(Collectors.groupingBy(e -> e.getFqn()
            .toString()));

    final Map<String, String> nameToFqnMap = candidate.includingVariables.stream()
        .collect(Collectors.toMap(Variable::getName, e -> e.getFqn()
            .toString()));
    final CandidateRewriteVisitor candidateRewriteVisitor = new CandidateRewriteVisitor(
        candidate.reuseCandidate.getValue(), nameToFqnMap, fqnToNamesMap, random);
    return candidateRewriteVisitor.getReplacedStatement();
  }


  private class Candidate {

    final ReuseCandidate<Statement> reuseCandidate;
    final List<Variable> includingVariables;

    Candidate(final ReuseCandidate<Statement> reuseCandidate,
        final List<Variable> includingVariables) {
      this.reuseCandidate = reuseCandidate;
      this.includingVariables = includingVariables;
    }
  }

  private class CandidateVisitor extends ASTVisitor {

    private final List<String> names = new ArrayList<>();
    private final List<String> accessVariableNames;

    CandidateVisitor(final Statement statement, final List<Variable> accessibleVariables) {
      this.accessVariableNames = accessibleVariables.stream()
          .map(Variable::getName)
          .collect(Collectors.toList());
      statement.accept(this);
    }

    @Override
    public boolean visit(final QualifiedName node) {
      return false;
    }

    @Override
    public boolean visit(final SimpleName node) {
      if (accessVariableNames.contains(node.getIdentifier())) {
        names.add(node.toString());
      }
      return true;
    }
  }

  private class CandidateRewriteVisitor extends ASTVisitor {

    private final Statement targetStatement;
    private final Map<String, String> nameToFqnMap;
    private final Map<String, List<Variable>> fqnToNamesMap;
    private final Random random;

    CandidateRewriteVisitor(final Statement node,
        final Map<String, String> nameToFqnMap, final Map<String, List<Variable>> fqnToNamesMap,
        final Random random) {
      this.targetStatement = (Statement) ASTNode.copySubtree(node.getAST(), node);
      this.nameToFqnMap = nameToFqnMap;
      this.fqnToNamesMap = fqnToNamesMap;
      this.random = random;

      targetStatement.accept(this);
    }

    @Override
    public boolean visit(final QualifiedName node) {
      return false;
    }

    @Override
    public boolean visit(final SimpleName node) {
      // 宣言部分(左辺)は無視する
      if (node.isDeclaration()) {
        return true;
      }

      final String fqn = nameToFqnMap.get(node.toString());
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

    public Statement getReplacedStatement() {
      return targetStatement;
    }
  }
}
