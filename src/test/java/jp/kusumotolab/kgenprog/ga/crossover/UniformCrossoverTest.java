package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class UniformCrossoverTest {

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
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 10);
    final List<Variant> variants10 = crossover10.exec(testVariants.variantStore, 1);
    assertThat(variants10.size()).isEqualTo(10);

    // バリアントの生成
    final Crossover crossover100 =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 100);
    final List<Variant> variants100 = crossover100.exec(testVariants.variantStore, 1);
    assertThat(variants100.size()).isEqualTo(100);
  }

  @Test
  public void testStopFirst01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // テストデータを初期化
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();

    // 修正プログラムがすでに1つ存在している状態にする
    when(testVariants.variantStore.getFoundSolutionsNumber()).thenReturn(new OrdinalNumber(1));

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 10);
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    assertThat(variants).isEmpty();
  }

  @Test
  public void testStopFirst02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // テストデータを初期化
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();

    // 修正プログラムが必ず生成されるようにモックを設定する
    when(testVariants.variantStore.createVariant(any(), any())).then(ans -> {
      return new Variant(0, 0, ans.getArgument(0), null, null, new SimpleFitness(1.0), null,
          ans.getArgument(1));
    });

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 10);
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    assertThat(variants).hasSize(1);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0) // variantAを選ぶための0
        .thenReturn(0); // variantBを選ぶための1

    // バリアントの生成
    final Crossover crossover = new UniformCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(1) // variantBを選ぶための1
        .thenReturn(1); // variantCを選ぶための1

    // バリアントの生成
    final Crossover crossover = new UniformCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantC);

    // 生成されたバリアントのGeneはvariantCと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants03() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantD);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants04() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantA);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants05() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestComplementaryBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants06() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestComplementaryBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantC，2つ目のバリアントとしてvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 生成されたバリアントのGeneはvariantDと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants07() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new UniformCrossover(random, new FirstVariantEliteSelection(random),
        new SecondVariantEliteSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantA，2つ目のバリアントとしてvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択する一様交叉のテスト
   */
  @Test
  public void testGeneratedVariants08() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(1);

    // バリアントの生成
    final Crossover crossover = new UniformCrossover(random, new FirstVariantEliteSelection(random),
        new SecondVariantEliteSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore, 1);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントとしてvariantB，2つ目のバリアントとしてvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantA);

    // 生成されたバリアントのGeneはvariantAと同じになっているはず
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 交叉の親となりうるバリアントが1つしかない場合のテスト．交叉でバリアントが生成されないことを確認する．
   */
  @Test
  public void testGeneratedVariants09() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1);

    // バリアントのモック
    final CrossoverSingleTestVariant singleTestVariant = new CrossoverSingleTestVariant();

    // バリアントの生成
    final Crossover crossoverEE = new UniformCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantEliteSelection(), 1);
    final List<Variant> variantsEE = crossoverEE.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsEE).isEmpty();

    // バリアントの生成
    final Crossover crossoverER = new UniformCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantRandomSelection(random), 1);
    final List<Variant> variantsER = crossoverER.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsER).isEmpty();

    // バリアントの生成
    final Crossover crossoverEG =
        new UniformCrossover(random, new FirstVariantEliteSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final List<Variant> variantsEG = crossoverEG.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsEG).isEmpty();

    // バリアントの生成
    final Crossover crossoverET = new UniformCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantTestComplementaryBasedSelection(),
        1);
    final List<Variant> variantsET = crossoverET.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsET).isEmpty();

    // バリアントの生成
    final Crossover crossoverRE = new UniformCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantEliteSelection(), 1);
    final List<Variant> variantsRE = crossoverRE.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsRE).isEmpty();

    // バリアントの生成
    final Crossover crossoverRR = new UniformCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1);
    final List<Variant> variantsRR = crossoverRR.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsRR).isEmpty();

    // バリアントの生成
    final Crossover crossoverRG =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final List<Variant> variantsRG = crossoverRG.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsRG).isEmpty();

    // バリアントの生成
    final Crossover crossoverRT =
        new UniformCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestComplementaryBasedSelection(), 1);
    final List<Variant> variantsRT = crossoverRT.exec(singleTestVariant.variantStore, 1);
    assertThat(variantsRT).isEmpty();
  }

}
