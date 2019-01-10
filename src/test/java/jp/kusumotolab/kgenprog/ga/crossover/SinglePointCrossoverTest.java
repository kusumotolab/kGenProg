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

public class SinglePointCrossoverTest {

  /**
   * 生成するバリアントの数をテストするテストケース
   */
  @Test
  public void testNumberOfGeneratedVariants() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // テストデータを初期化
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();

    // バリアントの生成
    final Crossover crossover10 =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(), 10);
    final List<Variant> variants10 = crossover10.exec(testVariants.variantStore);
    assertThat(variants10.size()).isEqualTo(10);

    // バリアントの生成
    final Crossover crossover100 =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(), 100);
    final List<Variant> variants100 = crossover100.exec(testVariants.variantStore);
    assertThat(variants100.size()).isEqualTo(100);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0) // variantAを選ぶための0
        .thenReturn(1) // variantBを選ぶための1
        .thenReturn(0); // 交叉ポイントを1にするための0

    // バリアントの生成
    final Crossover crossover = new SinglePointCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 交叉ポイントが1なので，0はvariantAと同じ，123はvariantBと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2) // variantCを選ぶための2
        .thenReturn(3) // variantDを選ぶための3
        .thenReturn(1); // 交叉ポイントを2にするための1

    // バリアントの生成
    final Crossover crossover = new SinglePointCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 交叉ポイントは3なので，012はvariantCと同じ，3はvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants03() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantD);

    // 交叉ポイントが1なので，0はvariantAと同じ，123はvariantDと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants04() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantA);

    // 交叉ポイントは3なので，012はvariantCと同じ，3はvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants05() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 交叉ポイントは1なので，0はvariantAと同じ，123はvariantBと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants06() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 交叉ポイントは3なので，012はvariantCと同じ，3はvariantDと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants07() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new SinglePointCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantEliteSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 交叉ポイントは1なので，0はvariantAと同じ，123はvariantBと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択する一点交叉のテスト
   */
  @Test
  public void testGeneratedVariants08() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1);

    // バリアントの生成
    final Crossover crossover = new SinglePointCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantEliteSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantB，2つ目のバリアントとしてvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantA);

    // 交叉ポイントは2なので，01はvariantBと同じ，23はvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }
}
