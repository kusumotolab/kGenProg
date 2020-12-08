package jp.kusumotolab.kgenprog.ga.crossover;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.EmptyHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class CascadeCrossoverTest {

  VariantStore store;
  JDTASTLocation loc0;
  JDTASTLocation loc1;
  JDTASTLocation loc2;

  @Before
  public void setup() {
    final TargetProject project = TargetProjectFactory.create(Paths.get("example/Crossover01"));
    final Configuration config = new Configuration.Builder(project).build();
    store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    // setup initial variant and locations used in test
    final Variant v0 = store.getInitialVariant();
    loc0 = getLocation(v0, 0);
    loc1 = getLocation(v0, 1);
    loc2 = getLocation(v0, 3);
    assertThat(loc0.getNode()).isSameSourceCodeAs("n=0;");
    assertThat(loc1.getNode()).isSameSourceCodeAs("n=1;");
    assertThat(loc2.getNode()).isSameSourceCodeAs("n=2;");
  }

//    public void a(int n) {
//      if (n) {
//        n = 0      // loc0
//      } else {
//        n = 1      // loc1
//      }
//      n = 2        // loc2
//    }

  /**
   * 単純な挿入塩基を持つ2つの親のテスト
   * 2つの親の塩基は独立したブロックへの操作である最も単純なケース
   */
  @Test
  public void testForSimpleInsertions01() {
    // 親1: n=0の後ろにn=2を2つ追加
    final Base base1a = new Base(loc0, new InsertAfterOperation(loc2.getNode()));
    final Base base1b = new Base(loc0, new InsertAfterOperation(loc2.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    final Variant parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=2; n=2;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // 親2: n=1の後ろにn=2を2つ追加
    final Base base2a = new Base(loc1, new InsertAfterOperation(loc2.getNode()));
    final Base base2b = new Base(loc1, new InsertAfterOperation(loc2.getNode()));
    final Gene gene2 = new Gene(Arrays.asList(base2a, base2b));
    final Variant parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0;"
        + "} else {"
        + "  n=1; n=2; n=2;" // modified
        + "}"
        + "n=2;}}");

    // setup mocked objects used in crossover
    final VariantStore spiedStore = createMockedStore(parent1, parent2);
    final FirstVariantSelectionStrategy strategy1 = createMocked1stStrategy(parent1);
    final SecondVariantSelectionStrategy strategy2 = createMocked2ndStrategy(parent2);

    // テスト対象のセットアップ
    final Crossover crossover = new CascadeCrossover(strategy1, strategy2, 1);

    final List<Variant> variants = crossover.exec(spiedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base2a, base2b);

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base2a, base2b, base1a, base1b);

    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=2; n=2;" // modified
        + "} else {"
        + "  n=1; n=2; n=2;" // modified
        + "}"
        + "n=2;}}");

    // v2 is same source as v1
    assertThat(v2.isReproduced()).isTrue();
  }

  /**
   * 単純な挿入塩基を持つ2つの親のテスト
   * 2つの親の改変箇所が同じブロック内であるケース
   */
  @Test
  public void testForSimpleInsertions02() {
    // 親1: n=0の後ろにn=2を追加，さらにn=0の後ろにn=1を追加
    final Base base1a = new Base(loc0, new InsertAfterOperation(loc2.getNode()));
    final Base base1b = new Base(loc0, new InsertAfterOperation(loc1.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    final Variant parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // 親2: n=0の前にn=2を追加，さらにn=0の前にn=1を追加
    final Base base2a = new Base(loc0, new InsertBeforeOperation(loc2.getNode()));
    final Base base2b = new Base(loc0, new InsertBeforeOperation(loc1.getNode()));
    final Gene gene2 = new Gene(Arrays.asList(base2a, base2b));
    final Variant parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=2; n=1; n=0;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // setup mocked objects used in crossover
    final VariantStore spiedStore = createMockedStore(parent1, parent2);
    final FirstVariantSelectionStrategy strategy1 = createMocked1stStrategy(parent1);
    final SecondVariantSelectionStrategy strategy2 = createMocked2ndStrategy(parent2);

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2, 1);

    final List<Variant> variants = crossover.exec(spiedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base2a, base2b);

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base2a, base2b, base1a, base1b);

    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=2; n=1; n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // v2 is same source as v1
    assertThat(v2.isReproduced()).isTrue();
  }

  /**
   * 削除を含む複雑な操作を持つ親
   */
  @Test
  public void testForComplicatedMutations() {
    // 親1: n=0の後ろにn=2を追加，さらにn=0の後ろにn=1を追加
    final Base base1a = new Base(loc0, new InsertAfterOperation(loc2.getNode()));
    final Base base1b = new Base(loc0, new InsertAfterOperation(loc1.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    final Variant parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // 親2: n=1の後ろにn=0を追加，n=1の後ろにn=2追加．n=1削除，
    final Base base2a = new Base(loc1, new InsertAfterOperation(loc0.getNode()));
    final Base base2b = new Base(loc1, new InsertAfterOperation(loc2.getNode()));
    final Base base2c = new Base(loc1, new DeleteOperation());
    final Gene gene2 = new Gene(Arrays.asList(base2a, base2b, base2c));
    final Variant parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0;"
        + "} else {"
        + "  n=2; n=0;" // modified
        + "}"
        + "n=2;}}");

    // setup mocked objects used in crossover
    final VariantStore spiedStore = createMockedStore(parent1, parent2);
    final FirstVariantSelectionStrategy strategy1 = createMocked1stStrategy(parent1);
    final SecondVariantSelectionStrategy strategy2 = createMocked2ndStrategy(parent2);

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2, 1);

    final List<Variant> variants = crossover.exec(spiedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base2a, base2b, base2c);

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base2a, base2b, base2c, base1a, base1b);

    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=2; n=0;" // modified
        + "}"
        + "n=2;}}");

    // v2 is same source as v1
    assertThat(v2.isReproduced()).isTrue();
    assertThat(v2.isBuildSucceeded()).isFalse();
  }

  /**
   * 2つの親が矛盾する塩基を持つケース．
   * 親2によってlocate先のstmtが消えてしまうため子2の生成に失敗
   */
  @Test
  public void testForInconsistentParents() {
    // 親1: n=0の後ろにn=0を追加，n=0の後ろにn=1を追加
    final Base base1a = new Base(loc0, new InsertAfterOperation(loc0.getNode()));
    final Base base1b = new Base(loc0, new InsertAfterOperation(loc1.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    final Variant parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=0;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // 親2: n=0を削除
    final Base base2a = new Base(loc0, new DeleteOperation());
    final Gene gene2 = new Gene(Collections.singletonList(base2a));
    final Variant parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  " // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // setup mocked objects used in crossover
    final VariantStore spiedStore = createMockedStore(parent1, parent2);
    final FirstVariantSelectionStrategy strategy1 = createMocked1stStrategy(parent1);
    final SecondVariantSelectionStrategy strategy2 = createMocked2ndStrategy(parent2);

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2, 1);

    final List<Variant> variants = crossover.exec(spiedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base2a);
    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=1; n=0;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base2a, base1a, base1b);
    // the second variant was failed to create due to the missing location by parent2
    assertThat(v2.isReproduced()).isFalse();
    assertThat(v2.isBuildSucceeded()).isFalse();
  }


  /**
   * 2つの親が共通する塩基を持つケース．
   * さらに，親2のある塩基が上記の共通塩基を操作対象箇所として持つ．
   */
  @Test
  public void testForDerivedParents() {
    // 親1: n=0の後ろにn=1を追加，n=1の後ろにn=2を追加
    final Base base1a = new Base(loc0, new InsertAfterOperation(loc1.getNode()));
    final Base base1b = new Base(loc1, new InsertAfterOperation(loc2.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    final Variant parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1;" // modified
        + "} else {"
        + "  n=1; n=2;" // modified
        + "}"
        + "n=2;}}");

    // 親2のセットアップ．親1から派生させるために親1の新規追加nodeを取得
    final JDTASTLocation locX = getLocation(parent1, 2);
    assertThat(locX.getNode()).isSameSourceCodeAs("n=1;");

    // 親2: n=0の後ろにn=1を追加（親1と同じ塩基），先のn=1の後ろにn=2を追加
    final Base base2a = new Base(locX, new InsertAfterOperation(loc2.getNode()));
    final Gene gene2 = new Gene(Arrays.asList(base1a, base2a));
    final Variant parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=1;"
        + "}"
        + "n=2;}}");

    // setup mocked objects used in crossover
    final VariantStore spiedStore = createMockedStore(parent1, parent2);
    final FirstVariantSelectionStrategy strategy1 = createMocked1stStrategy(parent1);
    final SecondVariantSelectionStrategy strategy2 = createMocked2ndStrategy(parent2);

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2, 1);

    final List<Variant> variants = crossover.exec(spiedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base2a); // base1a is only one
    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(""
        + "public class Foo { public void a(int n) {"
        + "if (n) {"
        + "  n=0; n=1; n=2;" // modified
        + "} else {"
        + "  n=1; n=2;" //modified
        + "}"
        + "n=2;}}");

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base1a, base2a, base1b);
    assertThat(v2.isReproduced()).isTrue();
    assertThat(v2.isBuildSucceeded()).isFalse();
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
        new CascadeCrossover(new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final List<Variant> variants = crossover.exec(testVariants.variantStore);

    // 交叉でバリアントを生成しないはず
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
    when(testVariants.variantStore.createVariant(any(), any())).then(
        ans -> new Variant(0, 0, ans.getArgument(0), null, null, new SimpleFitness(1.0), null,
            ans.getArgument(1)));

    // バリアントの生成
    final Crossover crossover =
        new CascadeCrossover(new FirstVariantRandomSelection(random),
            new SecondVariantGeneSimilarityBasedSelection(random), 1);
    final List<Variant> variants = crossover.exec(testVariants.variantStore);

    // 交叉で1つだけ変異プログラムを生成するはず
    assertThat(variants).hasSize(1);
  }

  // helpers

  private List<Base> getBases(final Variant v) {
    return v.getGene()
        .getBases();
  }

  private GeneratedJDTAST<?> getAst(final Variant v) {
    return (GeneratedJDTAST<?>) v.getGeneratedSourceCode()
        .getProductAsts()
        .get(0);
  }

  private JDTASTLocation getLocation(final Variant v, int idx) {
    final GeneratedAST<?> ast = getAst(v);
    return (JDTASTLocation) ast.createLocations()
        .getAll()
        .get(idx);
  }

  private VariantStore createMockedStore(final Variant v1, final Variant v2) {
    // always return the specified variants for store#getCurrentVariants()
    final VariantStore spiedStore = Mockito.spy(store);
    when(spiedStore.getGeneratedVariants()).thenReturn(Arrays.asList(v1, v2));
    when(spiedStore.getCurrentVariants()).thenReturn(Arrays.asList(v1, v2));
    return spiedStore;
  }

  private FirstVariantSelectionStrategy createMocked1stStrategy(final Variant v) {
    final FirstVariantSelectionStrategy strategy1 =
        Mockito.mock(FirstVariantSelectionStrategy.class);
    when(strategy1.exec(any())).thenReturn(v); // always select v1 as first parent
    return strategy1;
  }

  private SecondVariantSelectionStrategy createMocked2ndStrategy(final Variant v) {
    final SecondVariantSelectionStrategy strategy2 =
        Mockito.mock(SecondVariantSelectionStrategy.class);
    try {
      when(strategy2.exec(any(), any())).thenReturn(v); // always select v2 as second
    } catch (final CrossoverInfeasibleException e) {
      e.printStackTrace();
    }
    return strategy2;
  }

}
