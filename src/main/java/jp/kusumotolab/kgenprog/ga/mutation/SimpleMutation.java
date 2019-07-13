package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

/**
 * 乱数に基づいて変異処理をするクラス
 *
 * @see Mutation
 */
public class SimpleMutation extends Mutation {

  protected final Type type;
  private final boolean needHistoricalElement;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param type 選択する候補のスコープ
   * @param needHistoricalElement 個体が生成される過程を記録するか否か
   */
  public SimpleMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type, final boolean needHistoricalElement) {
    super(mutationGeneratingCount, random, candidateSelection);
    this.type = type;
    this.needHistoricalElement = needHistoricalElement;
  }

  /**
   * 乱数に基づいて選択された Variant に対して変異処理をする Variant の選択は fitness の逆数に基づいて行われる 変異処理された Variant を
   * mutationGeneratingCount 分だけ返す
   *
   * @param variantStore Variant の情報を格納するオブジェクト
   * @return 変異された Gene を持った Variant のリスト
   */
  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> generatedVariants = new ArrayList<>();

    final List<Variant> currentVariants = variantStore.getCurrentVariants();

    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getValue();
      return Double.isNaN(value) ? 0 : value;
    }, random);

    for (int i = 0; i < mutationGeneratingCount; i++) {
      final Variant variant = variantRoulette.exec();
      final List<Suspiciousness> suspiciousnesses = variant.getSuspiciousnesses();
      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);

      if (suspiciousnesses.isEmpty()) {
        continue;
      }
      final Roulette<Suspiciousness> roulette =
          new Roulette<>(suspiciousnesses, weightFunction, random);

      final Suspiciousness suspiciousness = roulette.exec();
      final Base base = makeBase(suspiciousness);
      final Gene gene = makeGene(variant.getGene(), base);
      final HistoricalElement element;
      if (needHistoricalElement) {
        element = new MutationHistoricalElement(variant, base);
      } else {
        element = null;
      }
      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  protected Base makeBase(final Suspiciousness suspiciousness) {
    final ASTLocation location = suspiciousness.getLocation();
    return new Base(location, makeOperation(location));
  }

  protected Operation makeOperation(final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertAfterOperation(chooseNodeForReuse(location));
      case 2:
        return new ReplaceOperation(chooseNodeForReuse(location));
    }
    return new NoneOperation();
  }

  protected ASTNode chooseNodeForReuse(final ASTLocation location) {
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final Query query = new Query(scope);
    return candidateSelection.exec(query);
  }

  protected Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }
}
