package jp.kusumotolab.kgenprog.ga.crossover;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.EmptyHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
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
    // setup project
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
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
    final Variant generatedVariant1 = variants.get(0);
    assertThat(generatedVariant1)
        .isNotNull();
    assertThat(generatedVariant1.getGene())
        .extracting(Gene::getBases)
        .containsExactly(base1a, base1b, base1c, base2a, base2b, base2c);

    System.out.println(variants);

    variants.forEach(v -> System.out.println(v.getGeneratedSourceCode()
        .getAllAsts()
        .get(0)
        .getSourceCode()));

  }

  private GeneratedAST<?> getAst(final Variant v) {
    return v.getGeneratedSourceCode()
        .getProductAsts()
        .get(0);
  }

  private JDTASTLocation getLocation(final Variant v, int idx) {
    final GeneratedAST<?> ast = v.getGeneratedSourceCode()
        .getProductAsts()
        .get(0);
    return (JDTASTLocation) ast.createLocations()
        .getAll()
        .get(idx);
  }


}
