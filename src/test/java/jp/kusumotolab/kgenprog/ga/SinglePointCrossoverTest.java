package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;

public class SinglePointCrossoverTest {

  @SuppressWarnings("serial")
  private class MockRandom extends Random {

    private int counter = 0;

    @Override
    public boolean nextBoolean() {
      return false;
    }

    @Override
    public int nextInt(int divisor) {
      counter += 1;
      return counter % divisor;
    }
  }

  @Test
  public void testExec() {
    final Base noneOperationBase = new Base(null, new NoneOperation());
    final Base insertOperationBase = new Base(null, new InsertOperation(null));

    final List<Base> noneBases = Arrays.asList(noneOperationBase, noneOperationBase,
        noneOperationBase, noneOperationBase, noneOperationBase);
    final List<Base> insertBases = Arrays.asList(insertOperationBase, insertOperationBase,
        insertOperationBase, insertOperationBase, insertOperationBase);

    final Variant noneOperationVariant = new Variant(new SimpleGene(noneBases), null, null, null, null);
    final Variant insertOperationVariant = new Variant(new SimpleGene(insertBases), null, null, null, null);

    final Random random = new MockRandom();
    random.setSeed(0);
    final SinglePointCrossover singlePointCrossover = new SinglePointCrossover(random);
    final VariantStore variantStore = new MockVariantStore(Arrays.asList(noneOperationVariant, insertOperationVariant));

    final List<Gene> genes =
        singlePointCrossover.exec(variantStore)
            .stream()
            .map(Variant::getGene)
            .collect(Collectors.toList());

    assertThat(genes).anyMatch(this::containNoneOperationAndInsertOperation);
  }

  private boolean containNoneOperationAndInsertOperation(final Gene gene) {
    final List<Operation> operations = gene.getBases()
        .stream()
        .map(Base::getOperation)
        .collect(Collectors.toList());

    final boolean containNoneOperation = operations.stream()
        .anyMatch(e -> e instanceof NoneOperation);
    final boolean containInsertOperation = operations.stream()
        .anyMatch(e -> e instanceof InsertOperation);

    return containNoneOperation && containInsertOperation;
  }
}
