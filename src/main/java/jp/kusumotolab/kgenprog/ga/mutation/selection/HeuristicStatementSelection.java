package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

/**
 * 型を考慮してStatementを選ぶ． Statementを選んで返す時に変数名を書き換えて返す
 */
public class HeuristicStatementSelection extends StatementSelection {

  private final AccessibleVariableSearcher accessibleVariableSearcher = new AccessibleVariableSearcher();
  private final List<Candidate> candidates = new ArrayList<>();
  private final Random random;

  /**
   * @param random 乱数生成器
   */
  public HeuristicStatementSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> generatedASTS) {
    for (final GeneratedAST<ProductSourcePath> generatedAST : generatedASTS) {
      final GeneratedJDTAST jdtast = (GeneratedJDTAST) generatedAST;
      final List<Statement> statements = new StatementVisitor(
          ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot()).getStatements();
      final FullyQualifiedName fqn = jdtast.getPrimaryClassName();

      for (final Statement statement : statements) {
        final List<Variable> accessibleVariables = accessibleVariableSearcher.exec(statement);
        final AccessibleNameVisitor nameVisitor = new AccessibleNameVisitor(statement,
            accessibleVariables);
        final List<String> names = nameVisitor.names;

        final List<Variable> variables = accessibleVariables.stream()
            .filter(e -> names.contains(e.getName()))
            .collect(Collectors.toList());
        final Candidate candidate = new Candidate(statement, fqn, variables);
        candidates.add(candidate);
      }
    }
  }

  @Override
  public Statement exec(final Query query) {
    final List<Candidate> matchedCandidates = searchCandidates(query);
    if (matchedCandidates.isEmpty()) {
      return candidates.get(0)
          .getValue()
          .getAST()
          .newEmptyStatement();
    }
    final Candidate candidate = matchedCandidates.get(random.nextInt(matchedCandidates.size()));
    return candidate.getValue();
  }

  private List<Candidate> searchCandidates(final Query query) {
    final List<Variable> variables = query.getVariables();
    // queryFQN に含まれない型の変数は再利用しない
    final List<FullyQualifiedName> queryFQNs = extractFQNs(variables);

    return candidates.stream()
        .filter(candidate -> {
          final List<FullyQualifiedName> includingFQNs = candidate.includingVariables.stream()
              .map(Variable::getFqn)
              .collect(Collectors.toList());
          return queryFQNs.containsAll(includingFQNs);
        })
        .collect(Collectors.toList());
  }

  private List<FullyQualifiedName> extractFQNs(final List<Variable> variables) {
    return variables.stream()
        .map(Variable::getFqn)
        .collect(Collectors.toList());
  }

  // ================================ inner class ================================

  private class Candidate extends ReuseCandidate<Statement> {

    final List<Variable> includingVariables;

    public Candidate(final Statement value, final FullyQualifiedName fqn,
        final List<Variable> includingVariables) {
      super(value, fqn);
      this.includingVariables = includingVariables;
    }
  }


  private class AccessibleNameVisitor extends ASTVisitor {

    private final List<String> names = new ArrayList<>();
    private final List<String> accessVariableNames;

    AccessibleNameVisitor(final Statement statement, final List<Variable> accessibleVariables) {
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
}
