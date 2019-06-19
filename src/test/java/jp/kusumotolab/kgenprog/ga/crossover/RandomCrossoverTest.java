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

public class RandomCrossoverTest {

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
        new RandomCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 10, true);
    final List<Variant> variants10 = crossover10.exec(testVariants.variantStore);
    assertThat(variants10.size()).isEqualTo(10);

    // バリアントの生成
    final Crossover crossover100 =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 100, true);
    final List<Variant> variants100 = crossover100.exec(testVariants.variantStore);
    assertThat(variants100.size()).isEqualTo(100);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0)
        .thenReturn(0)
        .thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 常に2番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1)
        .thenReturn(1)
        .thenReturn(3);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantB，2つ目のバリアントはvariantCが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantC);

    // 常に3番目のBaseを取得するはず．つまり，insert, none, insert, insertになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.noneBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants03() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantGeneSimilarityBasedSelection(random), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantD);

    // 常に0番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants04() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantGeneSimilarityBasedSelection(random), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantC，2つ目のバリアントはvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantA);

    // 常に3番目のBaseを取得するはず．つまり，insert, insert, none, noneになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants05() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantTestComplementaryBasedSelection(), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 常に0番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants06() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantTestComplementaryBasedSelection(), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantC，2つ目のバリアントはvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 常に3番目のBaseを取得するはず．つまり，insert, insert, insert, insertになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants07() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantEliteSelection(random),
        new SecondVariantEliteSelection(), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 常に0番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントを評価関数，二つ目のバリアントも評価関数で選択するランダム交叉のテスト
   */
  @Test
  public void testGeneratedVariants08() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantEliteSelection(random),
        new SecondVariantEliteSelection(), 1, true);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantB，2つ目のバリアントはvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantA);

    // 常に1番目のBaseを取得するはず．つまり，none, none, insert, noneになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.insertBase, testVariants.noneBase);
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
    final Crossover crossoverEE = new RandomCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantEliteSelection(), 1, true);
    final List<Variant> variantsEE = crossoverEE.exec(singleTestVariant.variantStore);
    assertThat(variantsEE).isEmpty();

    // バリアントの生成
    final Crossover crossoverER = new RandomCrossover(random,
        new FirstVariantEliteSelection(random), new SecondVariantRandomSelection(random), 1, true);
    final List<Variant> variantsER = crossoverER.exec(singleTestVariant.variantStore);
    assertThat(variantsER).isEmpty();

    // バリアントの生成
    final Crossover crossoverEG =
        new RandomCrossover(random, new FirstVariantEliteSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1, true);
    final List<Variant> variantsEG = crossoverEG.exec(singleTestVariant.variantStore);
    assertThat(variantsEG).isEmpty();

    // バリアントの生成
    final Crossover crossoverET =
        new RandomCrossover(random, new FirstVariantEliteSelection(random),
            new SecondVariantTestComplementaryBasedSelection(), 1, true);
    final List<Variant> variantsET = crossoverET.exec(singleTestVariant.variantStore);
    assertThat(variantsET).isEmpty();

    // バリアントの生成
    final Crossover crossoverRE = new RandomCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantEliteSelection(), 1, true);
    final List<Variant> variantsRE = crossoverRE.exec(singleTestVariant.variantStore);
    assertThat(variantsRE).isEmpty();

    // バリアントの生成
    final Crossover crossoverRR = new RandomCrossover(random,
        new FirstVariantRandomSelection(random), new SecondVariantRandomSelection(random), 1, true);
    final List<Variant> variantsRR = crossoverRR.exec(singleTestVariant.variantStore);
    assertThat(variantsRR).isEmpty();

    // バリアントの生成
    final Crossover crossoverRG =
        new RandomCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1, true);
    final List<Variant> variantsRG = crossoverRG.exec(singleTestVariant.variantStore);
    assertThat(variantsRG).isEmpty();

    // バリアントの生成
    final Crossover crossoverRT =
        new RandomCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantTestComplementaryBasedSelection(), 1, true);
    final List<Variant> variantsRT = crossoverRT.exec(singleTestVariant.variantStore);
    assertThat(variantsRT).isEmpty();
  }

  /**
   * HistoricalElementを必要としない場合のテスト
   */
  @Test
  public void testNeedNotHistoricalElement() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), 1, false);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // HistoricalElementはnullのはず
    assertThat(variant.getHistoricalElement()).isNull();
  }
}
