package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.ProgramElementVisitor;

public abstract class RouletteSelection implements CandidateSelection {

  private final Random random;
  private Roulette<ReuseCandidate<ASTNode>> projectRoulette;
  private final Multimap<String, ReuseCandidate<ASTNode>> packageNameStatementMultimap = ArrayListMultimap.create();
  private final Multimap<FullyQualifiedName, ReuseCandidate<ASTNode>> fqnStatementMultiMap = ArrayListMultimap.create();
  private final Map<String, Roulette<ReuseCandidate<ASTNode>>> packageNameRouletteMap = new HashMap<>();
  private final Map<FullyQualifiedName, Roulette<ReuseCandidate<ASTNode>>> fqnRouletteMap = new HashMap<>();

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public RouletteSelection(final Random random) {
    this.random = random;
  }

  /**
   * ソースコードに含まれるプログラム要素を探索し，見つけた要素を保持する
   *
   * @param candidates 再利用するソースコードのリスト
   * @param visitor プログラム要素収集用のビジター
   */
  protected void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates,
      final ProgramElementVisitor visitor) {
    final List<ReuseCandidate<ASTNode>> reuseCandidates = candidates.stream()
        .flatMap(e -> {
          final FullyQualifiedName fqn = e.getPrimaryClassName();
          final CompilationUnit unit = ((GeneratedJDTAST<ProductSourcePath>) e).getRoot();
          visitor.analyzeElements(unit);
          final List<ASTNode> elements = visitor.getElements();
          return elements.stream()
              .map(s -> new ReuseCandidate<>(s, fqn.getPackageName(), fqn));
        })
        .collect(Collectors.toList());

    putMaps(reuseCandidates);

    projectRoulette = createRoulette(reuseCandidates);
  }

  /**
   * 各ステートメントの重みを計算するメソッド
   *
   * @param reuseCandidate 重みを計算したいステートメント
   * @return 重み
   */
  public abstract double getElementWeight(final ReuseCandidate<ASTNode> reuseCandidate);

  /**
   * 再利用するステートメントを重みに基づいて選択し，返すメソッド
   *
   * @param query クエリ
   * @return 再利用するステートメント
   */
  @Override
  public ASTNode exec(final Query query) {
    final Scope scope = query.getScope();
    final Roulette<ReuseCandidate<ASTNode>> roulette = getRoulette(scope);
    final ReuseCandidate<ASTNode> candidate = roulette.exec();
    return candidate.getValue();
  }

  private void putMaps(final List<ReuseCandidate<ASTNode>> reuseCandidates) {
    for (final ReuseCandidate<ASTNode> reuseCandidate : reuseCandidates) {
      packageNameStatementMultimap.put(reuseCandidate.getPackageName(), reuseCandidate);
      fqnStatementMultiMap.put(reuseCandidate.getFqn(), reuseCandidate);
    }
  }

  private Roulette<ReuseCandidate<ASTNode>> getRouletteInProjectScope() {
    return projectRoulette;
  }

  private Roulette<ReuseCandidate<ASTNode>> getRouletteInPackage(final String packageName) {
    return getRoulette(packageName, packageNameRouletteMap, packageNameStatementMultimap);
  }

  private Roulette<ReuseCandidate<ASTNode>> getRouletteInFile(final FullyQualifiedName fqn) {
    return getRoulette(fqn, fqnRouletteMap, fqnStatementMultiMap);
  }

  private <T> Roulette<ReuseCandidate<ASTNode>> getRoulette(final T key,
      final Map<T, Roulette<ReuseCandidate<ASTNode>>> rouletteMap,
      final Multimap<T, ReuseCandidate<ASTNode>> candidateMap) {
    Roulette<ReuseCandidate<ASTNode>> roulette = rouletteMap.get(key);
    if (roulette != null) {
      return roulette;
    }
    final Collection<ReuseCandidate<ASTNode>> candidates = candidateMap.get(key);
    roulette = createRoulette(new ArrayList<>(candidates));
    rouletteMap.put(key, roulette);
    return roulette;
  }

  Roulette<ReuseCandidate<ASTNode>> getRoulette(final Scope scope) {
    final FullyQualifiedName fqn = scope.getFqn();
    switch (scope.getType()) {
      case PROJECT:
        return getRouletteInProjectScope();
      case PACKAGE:
        return getRouletteInPackage(fqn.getPackageName());
      case FILE:
        return getRouletteInFile(fqn);
    }
    throw new IllegalArgumentException("This scope is not implemented.");
  }

  private Roulette<ReuseCandidate<ASTNode>> createRoulette(
      final List<ReuseCandidate<ASTNode>> candidates) {
    final Function<ReuseCandidate<ASTNode>, Double> weightFunction = this::getElementWeight;
    return new Roulette<>(candidates, weightFunction, random);
  }
}
