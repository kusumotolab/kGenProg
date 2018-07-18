package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;

public class SinglePointCrossoverTest {

  private class TestNumberGeneration extends RandomNumberGeneration {

    private int counter = 0;

    @Override
    public boolean getBoolean() {
      return false;
    }

    @Override
    public int getInt(int divisor) {
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

    final Variant noneOperationVariant = new Variant(new SimpleGene(noneBases), null, null);
    final Variant insertOperationVariant = new Variant(new SimpleGene(insertBases), null, null);

    final RandomNumberGeneration randomNumberGeneration = new TestNumberGeneration();
    final SinglePointCrossover singlePointCrossover = new SinglePointCrossover(
        randomNumberGeneration);

    final List<Gene> genes = singlePointCrossover.exec(
        Arrays.asList(noneOperationVariant, insertOperationVariant));

    final boolean result = genes.stream()
        .anyMatch(this::containNoneOperationAndInsertOperation);

    assertThat(result, is(true));
  }

  private boolean containNoneOperationAndInsertOperation(final Gene gene) {
    final List<Operation> operations = gene.getBases()
        .stream()
        .map(Base::getOperation)
        .collect(Collectors.toList());

    final boolean containNoneOperation = operations.stream()
        .anyMatch(e -> e instanceof NoneOperation);
    final boolean containInsertOperation = operations.stream()
        .anyMatch(
            e -> e instanceof InsertOperation);

    return containNoneOperation && containInsertOperation;
  }
}
