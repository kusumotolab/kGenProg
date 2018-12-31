package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class TestSimilarityBasedUniformCrossoverTest {

  @Test
  public void test01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new TestSimilarityBasedUniformCrossover(random, 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  @Test
  public void test02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new TestSimilarityBasedUniformCrossover(random, 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 生成されたバリアントのGeneはvariantDと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }
}
