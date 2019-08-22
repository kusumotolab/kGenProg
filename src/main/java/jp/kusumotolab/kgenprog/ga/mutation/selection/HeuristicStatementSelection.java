package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jp.kusumotolab.kgenprog.ga.mutation.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.Variable;
import jp.kusumotolab.kgenprog.ga.mutation.heuristic.ASTAnalyzer;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

/**
 * 型を考慮してStatementを選ぶ．
 */
public class HeuristicStatementSelection extends StatementSelection {

  private final AccessibleVariableSearcher accessibleVariableSearcher = new AccessibleVariableSearcher();
  private final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
  private final List<Candidate> nonControlCandidates = new ArrayList<>();
  private final List<Candidate> returnCandidates = new ArrayList<>();
  private final Multimap<FullyQualifiedName, Candidate> returnStatementMultimap = HashMultimap.create();
  private final Random random;
  private Statement emptyStatement; // 検索結果が空だった場合，emptyStatementを返す

  /**
   * @param random 乱数生成器
   */
  public HeuristicStatementSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> generatedASTs) {
    for (final GeneratedAST<ProductSourcePath> generatedAST : generatedASTs) {
      final GeneratedJDTAST jdtast = (GeneratedJDTAST) generatedAST;
      final List<Statement> statements = new StatementVisitor(
          ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot()).getStatements();
      final FullyQualifiedName fqn = jdtast.getPrimaryClassName();

      for (final Statement statement : statements) {
        // 各ステートメントに対して，その位置からアクセスできる変数の一覧を探索
        final List<Variable> accessibleVariables = accessibleVariableSearcher.exec(statement);
        final AccessibleNameVisitor nameVisitor = new AccessibleNameVisitor(statement,
            accessibleVariables);
        // そのステートメントに含まれる SimpleName の一覧を取得
        final List<String> names = nameVisitor.names;

        // (アクセスできる変数名) かつ (そのステートメントに含まれる SimpleName)
        // => そのステートメントに含まれる変数 (と推測)
        final List<Variable> variables = accessibleVariables.stream()
            .filter(e -> names.contains(e.getName()))
            .collect(Collectors.toList());
        final Candidate candidate = new Candidate(statement, fqn, variables);

        if (statement instanceof ReturnStatement) {
          returnCandidates.add(candidate);
          final FullyQualifiedName returnType = astAnalyzer.getReturnType(statement);
          if (returnType != null) {
            returnStatementMultimap.put(returnType, candidate);
          }
        } else if (statement instanceof ContinueStatement
            || statement instanceof BreakStatement
            || statement instanceof ThrowStatement) {
          // 特に何もしない
          // TODO: - Throwは再利用したほうがよさそうだが、breakとcontinueは再利用するべきか...？
        } else {
          nonControlCandidates.add(candidate);
        }
      }
    }

    // emptyStatement の準備
    final GeneratedAST<ProductSourcePath> generatedAST = generatedASTs.get(0);
    if (generatedAST instanceof GeneratedJDTAST) {
      final GeneratedJDTAST<ProductSourcePath> jdtast = (GeneratedJDTAST<ProductSourcePath>) generatedAST;
      emptyStatement = jdtast.getRoot()
          .getAST()
          .newEmptyStatement();
    }
  }

  @Override
  public Statement exec(final Query query) {
    final List<Candidate> matchedCandidates = searchCandidates(query);

    if (matchedCandidates.isEmpty()) {
      // 検索結果が空だった場合，emptyStatementを返す
      return emptyStatement;
    }

    final Candidate candidate = matchedCandidates.get(random.nextInt(matchedCandidates.size()));
    return candidate.getValue();
  }

  private List<Candidate> searchCandidates(final Query query) {
    final List<Variable> variables = query.getVariables();
    // queryFQNs に含まれない型の変数は再利用しない
    final List<FullyQualifiedName> queryFQNs = extractFQNs(variables);

    return createCandidates(query).stream()
        .filter(candidate -> {
          final List<FullyQualifiedName> includingFQNs = candidate.includingVariables.stream()
              .map(Variable::getFqn)
              .collect(Collectors.toList());
          return queryFQNs.containsAll(includingFQNs);
        })
        .collect(Collectors.toList());
  }

  private List<Candidate> createCandidates(final Query query) {
    final List<Candidate> candidates = new ArrayList<>();

    if (query.canReuseNonControlStatement()) {
      candidates.addAll(nonControlCandidates);
    }
    if (query.canReuseReturnStatement()) {
      final FullyQualifiedName fqn = query.getReturnFQN();
      if (fqn == null) {
        candidates.addAll(returnCandidates);
      } else {
        final Collection<Candidate> returnCandidates = returnStatementMultimap.get(fqn);
        candidates.addAll(returnCandidates);
      }
    }

    if (candidates.isEmpty()) {
      return candidates;
    }

    final AST ast = candidates.get(0)
        .getValue()
        .getAST();

    if (query.canReuseBreakStatement()) {
      final BreakStatement statement = ast.newBreakStatement();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName("");
      candidates.add(new Candidate(statement, fqn, Collections.emptyList()));
    }

    if (query.canReuseContinueStatement()) {
      final ContinueStatement statement = ast.newContinueStatement();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName("");
      candidates.add(new Candidate(statement, fqn, Collections.emptyList()));
    }
    return candidates;
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
    public boolean visit(final SimpleName node) {
      if (accessVariableNames.contains(node.getIdentifier())) {
        names.add(node.toString());
      }
      return true;
    }
  }
}
