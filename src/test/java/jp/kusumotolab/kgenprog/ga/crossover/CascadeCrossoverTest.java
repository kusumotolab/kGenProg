package jp.kusumotolab.kgenprog.ga.crossover;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.EmptyHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class CascadeCrossoverTest {

  VariantStore store;
  JDTASTLocation locA;
  JDTASTLocation locB;
  JDTASTLocation locC;
  Variant parent1;
  Variant parent2;
  FirstVariantSelectionStrategy strategy1;
  SecondVariantSelectionStrategy strategy2;

  @Before
  public void setup() throws Exception {

    final TargetProject project = TargetProjectFactory.create(Paths.get("example/Crossover01"));
    final Configuration config = new Configuration.Builder(project).build();
    store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    // setup initial variant and locations used in test
    final Variant v0 = store.getInitialVariant();

    locA = getLocation(v0, 0); // n--
    locB = getLocation(v0, 1); // n++
    locC = getLocation(v0, 3); // return n;
    assertThat(locA.getNode()).isSameSourceCodeAs("n--;");
    assertThat(locB.getNode()).isSameSourceCodeAs("n++;");
    assertThat(locC.getNode()).isSameSourceCodeAs("return n;");
  }

  //  public int foo(int n) {
  //    if (n > 0) {
  //      n--;        // locA
  //    } else {
  //      n++;        // locB
  //    }
  //    return n;     // locC
  //  }

  @Test
  public void testForSimpleInsertions() throws Exception {
    // 親1: n--をn--の後ろに3つ追加
    final Base base1a = new Base(locA, new InsertAfterOperation(locA.getNode()));
    final Base base1b = new Base(locA, new InsertAfterOperation(locA.getNode()));
    final Base base1c = new Base(locA, new InsertAfterOperation(locA.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b, base1c));
    parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);

    // 親2: n++をn++の後ろに3つ追加
    final Base base2a = new Base(locB, new InsertAfterOperation(locB.getNode()));
    final Base base2b = new Base(locB, new InsertAfterOperation(locB.getNode()));
    final Base base2c = new Base(locB, new InsertAfterOperation(locB.getNode()));
    final Gene gene2 = new Gene(Arrays.asList(base2a, base2b, base2c));
    parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);

    // always return the two parents for store#getCurrentVariants()
    final VariantStore spyedStore = Mockito.spy(store);
    when(spyedStore.getGeneratedVariants()).thenReturn(Arrays.asList(parent1, parent2));
    when(spyedStore.getCurrentVariants()).thenReturn(Arrays.asList(parent1, parent2));

    // setup mocked strategies for CascadeCrossover constructor
    strategy1 = Mockito.mock(FirstVariantSelectionStrategy.class);
    strategy2 = Mockito.mock(SecondVariantSelectionStrategy.class);
    when(strategy1.exec(any())).thenReturn(parent1); // always select v1 as first parent
    when(strategy2.exec(any(), any())).thenReturn(parent2); // always select v2 as second

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2);

    final List<Variant> variants = crossover.exec(spyedStore);
    assertThat(variants)
        .hasSize(2)
        .doesNotContainNull();

    final Variant v1 = variants.get(0);
    assertThat(getBases(v1))
        .containsExactly(base1a, base1b, base1c, base2a, base2b, base2c);

    final Variant v2 = variants.get(1);
    assertThat(getBases(v2))
        .containsExactly(base2a, base2b, base2c, base1a, base1b, base1c);

    final GeneratedJDTAST<?> ast1 = getAst(v1);
    assertThat(ast1.getRoot()).isSameSourceCodeAs(
        "package example; public class Foo { public void a(int i) {"
            + "if (n > 0 ) {"
            + "  n--; n--; n--; n--;"
            + "} else {"
            + "  n++; n++; n++; n++;"
            + "}"
            + "return n;}}");

    // v2 is same source as v1
    assertThat(v2.isReproduced()).isTrue();
  }


  @Test
  public void testForSimpleInsertions2() throws Exception {
    // 親1: return nをn--の後ろに追加，さらにn++をn--の後ろに追加
    final Base base1a = new Base(locA, new InsertAfterOperation(locC.getNode()));
    final Base base1b = new Base(locA, new InsertAfterOperation(locB.getNode()));
    final Gene gene1 = new Gene(Arrays.asList(base1a, base1b));
    parent1 = store.createVariant(gene1, EmptyHistoricalElement.instance);
    assertThat(getAst(parent1).getRoot()).isSameSourceCodeAs(
        "package example; public class Foo { public void a(int i) {"
            + "if (n > 0 ) {n--;n++;return n;}" // modified
            + "else {n++;}"
            + "return n;}}");

    // 親2: n--をn++の後ろに追加，n--削除，n++の後ろにreturn n追加
    final Base base2a = new Base(locB, new InsertAfterOperation(locA.getNode()));
    final ASTLocation loc2a = base2a.getTargetLocation();
    final Base base2b = new Base(locB, new DeleteOperation());
    final Base base2c = new Base(loc2a, new InsertAfterOperation(locC.getNode()));
    final Gene gene2 = new Gene(Arrays.asList(base2a, base2b, base2c));
    parent2 = store.createVariant(gene2, EmptyHistoricalElement.instance);
    assertThat(getAst(parent2).getRoot()).isSameSourceCodeAs(
        "package example; public class Foo { public void a(int i) {"
            + "if (n > 0 ) {n--;}"
            + "else {n--; return n;}" // modified
            + "return n;}}");

    // always return the two parents for store#getCurrentVariants()
    final VariantStore spyedStore = Mockito.spy(store);
    when(spyedStore.getGeneratedVariants()).thenReturn(Arrays.asList(parent1, parent2));
    when(spyedStore.getCurrentVariants()).thenReturn(Arrays.asList(parent1, parent2));

    // setup mocked strategies for CascadeCrossover constructor
    strategy1 = Mockito.mock(FirstVariantSelectionStrategy.class);
    strategy2 = Mockito.mock(SecondVariantSelectionStrategy.class);
    when(strategy1.exec(any())).thenReturn(parent1); // always select v1 as first parent
    when(strategy2.exec(any(), any())).thenReturn(parent2); // always select v2 as second

    // テスト対象のセットアップ
    Crossover crossover = new CascadeCrossover(strategy1, strategy2);

    final List<Variant> variants = crossover.exec(spyedStore);
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
    assertThat(ast1.getRoot()).isSameSourceCodeAs(
        "package example; public class Foo { public void a(int i) {"
            + "if (n > 0 ) {n--;n++;return n;}" // modified
            + "else {n--; return n;}" // modified
            + "return n;}}");

    // v2 is same source as v1
    assertThat(v2.isReproduced()).isTrue();
  }

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


}
