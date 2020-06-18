package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
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
import jp.kusumotolab.kgenprog.project.jdt.ProgramElementVisitor;

/**
 * This class is for selecting a program element for reuse.
 * The features of this class are as follows.
 * - Considering types of program elements to be reused according to a given reuse location
 * - Replacing variables in a reused program element to ones that can used in a given reuse location
 *
 * 以下の情報を利用して再利用するプログラム要素を選択するためのクラス．
 * - 再利用先の位置に基づいて再利用可能なプログラム要素を考慮
 * - 再利用するプログラム要素に含まれる変数を再利用先で利用可能な変数に変換
 */
public abstract class HeuristicSelection implements CandidateSelection {


  private final AccessibleVariableSearcher accessibleVariableSearcher = new AccessibleVariableSearcher();
  private final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
  private final List<Candidate> nonControlCandidates = new ArrayList<>();
  private final List<Candidate> returnCandidates = new ArrayList<>();
  private final Multimap<FullyQualifiedName, Candidate> returnStatementMultimap = HashMultimap.create();
  private final Random random;
  private Statement emptyStatement; // 検索結果が空だった場合，emptyStatementを返す

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public HeuristicSelection(final Random random) {
    this.random = random;
  }

  /**
   * ソースコードに含まれるプログラム要素を探索し，見つけた要素を保持する
   *
   * @param generatedASTs 再利用数するソースコードのリスト
   * @param visitor プログラム要素収集用のビジター
   */
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> generatedASTs,
      final ProgramElementVisitor visitor) {

    for (final GeneratedAST<ProductSourcePath> generatedAST : generatedASTs) {

      final GeneratedJDTAST jdtast = (GeneratedJDTAST) generatedAST;

      final CompilationUnit unit = ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot();
      visitor.analyzeElements(unit);
      final List<ASTNode> elements = visitor.getElements();
      final FullyQualifiedName fqn = jdtast.getPrimaryClassName();

      for (final ASTNode element : elements) {
        // 各プログラム要素に対して，その位置からアクセスできる変数の一覧を探索
        final List<Variable> accessibleVariables = accessibleVariableSearcher.exec(element);
        final AccessibleNameVisitor nameVisitor = new AccessibleNameVisitor(element,
            accessibleVariables);
        // そのプログラム要素に含まれる SimpleName の一覧を取得
        final List<String> names = nameVisitor.names;

        // (アクセスできる変数名) かつ (そのプログラム要素に含まれる SimpleName)
        // => そのステートメントに含まれる変数 (と推測)
        final List<Variable> variables = accessibleVariables.stream()
            .filter(e -> names.contains(e.getName()))
            .collect(Collectors.toList());
        final Candidate candidate = new Candidate(element, fqn, variables);

        if (element instanceof ReturnStatement) {
          returnCandidates.add(candidate);
          final FullyQualifiedName returnType = astAnalyzer.getReturnType(element);
          if (returnType != null) {
            returnStatementMultimap.put(returnType, candidate);
          }
        } else if (element instanceof ContinueStatement
            || element instanceof BreakStatement
            || element instanceof ThrowStatement) {
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

  /**
   * Returning a program element for reuse.
   *
   * 再利用するプログラム要素を返す．
   *
   * @param query 再利用する候補のクエリ
   * @return
   */
  @Override
  public ASTNode exec(final Query query) {
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
        candidates.addAll(returnStatementMultimap.get(fqn));
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

  private class Candidate extends ReuseCandidate<ASTNode> {

    final List<Variable> includingVariables;

    public Candidate(final ASTNode value, final FullyQualifiedName fqn,
        final List<Variable> includingVariables) {
      super(value, fqn);
      this.includingVariables = includingVariables;
    }
  }


  private class AccessibleNameVisitor extends ASTVisitor {

    private final List<String> names = new ArrayList<>();
    private final List<String> accessVariableNames;

    AccessibleNameVisitor(final ASTNode element, final List<Variable> accessibleVariables) {
      this.accessVariableNames = accessibleVariables.stream()
          .map(Variable::getName)
          .collect(Collectors.toList());
      element.accept(this);
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
