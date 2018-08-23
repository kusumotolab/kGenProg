package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation extends Mutation {

  private static final Logger log = LoggerFactory.getLogger(RandomMutation.class);

  public RandomMutation(final int numberOfBase,
      final Random random,
      final CandidateSelection candidateSelection) {
    super(numberOfBase, random, candidateSelection);
  }

  public List<Variant> exec(final List<Variant> variants) {
    log.debug("enter exec(List<Variant>)");

    final List<Variant> generatedVariants = new ArrayList<>();

    for(Variant variant : variants) {
      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);
  
      final Roulette<Suspiciousness> roulette =
          new Roulette<>(variant.getSuspiciousnesses(), weightFunction, random);
  
      for (int i = 0; i < numberOfBase; i++) {
        final Suspiciousness suspiciousness = roulette.exec();
        final Base base = makeBase(suspiciousness);
        final Gene gene = makeGene(variant.getGene(), base);
        generatedVariants.add(new Variant(gene));
      }
    
    }

    log.debug("exit exec(List<>)");
    return generatedVariants;
  }

  private Base makeBase(final Suspiciousness suspiciousness) {
    log.debug("enter makeBase(Suspiciousness)");
    return new Base(suspiciousness.getLocation(), makeOperationAtRandom());
  }

  private Operation makeOperationAtRandom() {
    log.debug("enter makeOperationAtRandom()");
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
    log.debug("enter chooseNodeAtRandom()");
    return candidateSelection.exec();
  }
  
  private Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new SimpleGene(bases);
  }
}
