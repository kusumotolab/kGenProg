package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.UniformCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;

public class UniformCrossoverTest {

  private static Base noneOperationBase;
  private static Base insertOperationBase;
  private static Variant noneOperationVariant;
  private static Variant insertOperationVariant;
  private static VariantStore variantStore;

  @Before
  public void setup() {
    noneOperationBase = new Base(null, new NoneOperation());
    insertOperationBase = new Base(null, new InsertOperation(null));

    final List<Base> noneBases =
        Arrays.asList(noneOperationBase, noneOperationBase, noneOperationBase, noneOperationBase);
    final List<Base> insertBases = Arrays.asList(insertOperationBase, insertOperationBase,
        insertOperationBase, insertOperationBase);

    noneOperationVariant = new Variant(0, 0, new Gene(noneBases), null, null, null, null, null);
    insertOperationVariant = new Variant(0, 0, new Gene(insertBases), null, null, null, null, null);
    variantStore = makeVariantStore(noneOperationVariant, insertOperationVariant);
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

  @Test
  public void testGeneratingVariantsSize() {

    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    List<Variant> variants = execCrossover(random, 10);
    assertThat(variants).hasSize(10);

    variants = execCrossover(random, 13);
    assertThat(variants).hasSize(13);
  }

  @Test
  public void testVariants() {

    final Random random = Mockito.mock(Random.class);

    // 常に一つ目のバリアントのBaseを返すはず
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0)
        .thenReturn(1);
    final Variant variant1 = execCrossover(random, 1).get(0);
    assertThat(variant1.getGene()
        .getBases()).containsExactly(noneOperationBase, noneOperationBase, noneOperationBase,
            noneOperationBase);

    // 常に二つ目のバリアントのBaseを返すはず
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(0)
        .thenReturn(1);
    final Variant variant2 = execCrossover(random, 1).get(0);
    assertThat(variant2.getGene()
        .getBases()).containsExactly(insertOperationBase, insertOperationBase, insertOperationBase,
            insertOperationBase);

    // 一つ目と二つ目のバリアントのBaseを交互に返すはず
    when(random.nextBoolean()).thenReturn(true)
        .thenReturn(false)
        .thenReturn(true)
        .thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(0)
        .thenReturn(1);
    final Variant variant3 = execCrossover(random, 1).get(0);
    assertThat(variant3.getGene()
        .getBases())
            .isEqualTo(Arrays.asList(noneOperationBase, insertOperationBase, noneOperationBase,
                insertOperationBase));
  }

  @Test
  public void testHistoricalElements() {

    final Random random = Mockito.mock(Random.class);

    // nonOperationVariantを2つの親として持つバリアントを生成
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0); // noneOperationVariantを親として選ぶモック
    final Variant variant1 = execCrossover(random, 1).get(0);
    final HistoricalElement element1 = variant1.getHistoricalElement();
    assertThat(element1).isInstanceOf(UniformCrossoverHistoricalElement.class);
    final UniformCrossoverHistoricalElement uElement1 =
        (UniformCrossoverHistoricalElement) element1;
    assertThat(uElement1.getParents()).containsExactly(noneOperationVariant, noneOperationVariant);

    // insertOperationVariantを2つの親として持つバリアントを生成
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1); // insertOperationVariantを親として選ぶモック
    final Variant variant2 = execCrossover(random, 1).get(0);
    final HistoricalElement element2 = variant2.getHistoricalElement();
    assertThat(element2).isInstanceOf(UniformCrossoverHistoricalElement.class);
    final UniformCrossoverHistoricalElement uElement2 =
        (UniformCrossoverHistoricalElement) element2;
    assertThat(uElement2.getParents()).containsExactly(insertOperationVariant,
        insertOperationVariant);

    // noneOperationVariantとinsertOperationVariantを2つの親として持つバリアントを生成
    when(random.nextBoolean()).thenReturn(true)
        .thenReturn(false)
        .thenReturn(true)
        .thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(0) // 一つ目の親としてnoneOperationVariantを選ぶ
        .thenReturn(1); // 二つ目の親としてinsertOperationVariantを親として選ぶ
    final Variant variant3 = execCrossover(random, 1).get(0);
    final HistoricalElement element3 = variant3.getHistoricalElement();
    assertThat(element3).isInstanceOf(UniformCrossoverHistoricalElement.class);
    final UniformCrossoverHistoricalElement uElement3 =
        (UniformCrossoverHistoricalElement) element3;
    assertThat(uElement3.getParents()).containsExactly(noneOperationVariant,
        insertOperationVariant);
  }

  private List<Variant> execCrossover(final Random random, final int crossoverGeneratingCount) {
    final UniformCrossover crossover = new UniformCrossover(random, crossoverGeneratingCount);
    return crossover.exec(variantStore);
  }

}
