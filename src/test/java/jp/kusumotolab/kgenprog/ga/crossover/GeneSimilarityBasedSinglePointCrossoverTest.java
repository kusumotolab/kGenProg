package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;

public class GeneSimilarityBasedSinglePointCrossoverTest {


  private static Random random;

  @Before
  public void setup() {
    random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(1);
  }

  @Test
  public void testCrossover() {
    final List<Variant> variants = execCrossover(10);
    final List<Gene> genes = variants.stream()
        .map(Variant::getGene)
        .collect(Collectors.toList());

    assertThat(genes).anyMatch(this::containNoneOperationAndInsertOperation);
  }

  @Test
  public void testGeneratingVariantsSize() {
    List<Variant> variants = execCrossover(10);
    assertThat(variants).hasSize(10);

    variants = execCrossover(13);
    assertThat(variants).hasSize(13);
  }

  @Test
  public void testHistoricalElement() {
    final Base noneOperationBase = new Base(null, new NoneOperation());
    final Base insertOperationBase = new Base(null, new InsertOperation(null));

    final List<Base> noneBases = Arrays.asList(noneOperationBase, noneOperationBase,
        noneOperationBase, noneOperationBase, noneOperationBase);
    final List<Base> insertBases = Arrays.asList(insertOperationBase, insertOperationBase,
        insertOperationBase, insertOperationBase, insertOperationBase);

    final Variant noneOperationVariant =
        new Variant(0, 0, new Gene(noneBases), null, null, null, null, null);
    final Variant insertOperationVariant =
        new Variant(0, 0, new Gene(insertBases), null, null, null, null, null);
    final VariantStore variantStore =
        makeVariantStore(noneOperationVariant, insertOperationVariant);

    final SinglePointCrossover singlePointCrossover =
        new GeneSimilarityBasedSinglePointCrossover(random, 10);
    final List<Variant> variants = singlePointCrossover.exec(variantStore);

    final Variant variant = variants.get(0);
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element).isInstanceOf(CrossoverHistoricalElement.class);

    final CrossoverHistoricalElement cElement = (CrossoverHistoricalElement) element;
    assertThat(cElement.getParents()).containsExactly(insertOperationVariant, noneOperationVariant);
    assertThat(cElement.getCrossoverPoint()).isEqualTo(2);
  }

  private List<Variant> execCrossover(final int crossoverGeneratingCount) {
    final Base noneOperationBase = new Base(null, new NoneOperation());
    final Base insertOperationBase = new Base(null, new InsertOperation(null));

    final List<Base> noneBases = Arrays.asList(noneOperationBase, noneOperationBase,
        noneOperationBase, noneOperationBase, noneOperationBase);
    final List<Base> insertBases = Arrays.asList(insertOperationBase, insertOperationBase,
        insertOperationBase, insertOperationBase, insertOperationBase);

    final Variant noneOperationVariant =
        new Variant(0, 0, new Gene(noneBases), null, null, null, null, null);
    final Variant insertOperationVariant =
        new Variant(0, 0, new Gene(insertBases), null, null, null, null, null);
    final VariantStore variantStore =
        makeVariantStore(noneOperationVariant, insertOperationVariant);

    final SinglePointCrossover singlePointCrossover =
        new GeneSimilarityBasedSinglePointCrossover(random, crossoverGeneratingCount);
    return singlePointCrossover.exec(variantStore);
  }

  private VariantStore makeVariantStore(final Variant noneOperationVariant,
      final Variant insertOperationVariant) {
    final VariantStore variantStore = Mockito.mock(VariantStore.class);
    when(variantStore.getCurrentVariants())
        .thenReturn(Arrays.asList(noneOperationVariant, insertOperationVariant));
    when(variantStore.createVariant(any(), any())).thenAnswer(invocation -> {
      final Gene gene = invocation.getArgument(0);
      final HistoricalElement element = invocation.getArgument(1);
      return new Variant(0, 0, gene, null, null, null, null, element);
    });
    return variantStore;
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
