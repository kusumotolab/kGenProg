package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation extends Mutation {

  public RandomMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    super(mutationGeneratingCount, random, candidateSelection);
  }

  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> generatedVariants = new ArrayList<>();

    final List<Variant> currentVariants = variantStore.getCurrentVariants();

    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getValue();
      return Double.isNaN(value) ? 0 : value + 1;
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
      final HistoricalElement element = new MutationHistoricalElement(variant, base);
      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  private Base makeBase(final Suspiciousness suspiciousness) {
    return new Base(suspiciousness.getLocation(), makeOperationAtRandom());
  }

  private Operation makeOperationAtRandom() {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertOperation(chooseNodeAtRandom());
      case 2:
        return new ReplaceOperation(chooseNodeAtRandom());
    }
    return new NoneOperation();
  }

  private ASTNode chooseNodeAtRandom() {
    return candidateSelection.exec();
  }

  private Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }
}
