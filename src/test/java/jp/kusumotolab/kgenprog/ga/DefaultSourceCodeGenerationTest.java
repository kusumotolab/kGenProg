package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class DefaultSourceCodeGenerationTest {

  @Test
  public void testExec() {
    final Path rootDir = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);
    final VariantStore variantStore = getVariantStore(initialVariant);
    final Base base = new Base(null, new NoneOperation());
    final Gene gene = new Gene(Collections.singletonList(base));
    final DefaultSourceCodeGeneration defaultSourceCodeGeneration =
        new DefaultSourceCodeGeneration();

    // 1回目の生成は正しく生成される
    final GeneratedSourceCode firstGeneratedSourceCode =
        defaultSourceCodeGeneration.exec(variantStore, gene);
    assertThat(firstGeneratedSourceCode).isNotInstanceOf(GenerationFailedSourceCode.class);

    // 2回目の生成は失敗する
    final GeneratedSourceCode secondGeneratedSourceCode =
        defaultSourceCodeGeneration.exec(variantStore, gene);
    assertThat(secondGeneratedSourceCode).isInstanceOf(GenerationFailedSourceCode.class);
    assertThat(secondGeneratedSourceCode.getGenerationMessage()).isEqualTo("duplicate sourcecode");
  }

  @Test
  public void testExecDupicateInitialVariant() {
    final Path rootDir = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);
    final VariantStore variantStore = getVariantStore(initialVariant);
    final Base base = new Base(null, new NoneOperation());
    final Gene gene = new Gene(Collections.singletonList(base));
    final DefaultSourceCodeGeneration defaultSourceCodeGeneration =
        new DefaultSourceCodeGeneration();

    // 初期化（initialVariantをSetに追加）
    defaultSourceCodeGeneration.initialize(initialVariant);

    // NoneOperationではソースコードは変わらないので失敗するはず
    final GeneratedSourceCode secondGeneratedSourceCode =
        defaultSourceCodeGeneration.exec(variantStore, gene);
    assertThat(secondGeneratedSourceCode).isInstanceOf(GenerationFailedSourceCode.class);
    assertThat(secondGeneratedSourceCode.getGenerationMessage()).isEqualTo("duplicate sourcecode");
  }

  @Test
  public void noneOperationTest() {
    final TargetProject targetProject =
        TargetProjectFactory.create(Paths.get("example/BuildSuccess01"));
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);
    final VariantStore variantStore = getVariantStore(initialVariant);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final Gene initialGene = new Gene(new ArrayList<>());
    final Base noneBase = new Base(null, new NoneOperation());
    final List<Gene> genes = initialGene.generateNextGenerationGenes(Arrays.asList(noneBase));

    // noneBaseを適用した単一のGeneを取り出す
    final Gene gene = genes.get(0);
    final GeneratedSourceCode generatedSourceCode = sourceCodeGeneration.exec(variantStore, gene);
    final GeneratedSourceCode initialSourceCode = initialVariant.getGeneratedSourceCode();

    assertThat(generatedSourceCode.getProductAsts())
        .hasSameSizeAs(initialSourceCode.getProductAsts());

    // NoneOperationにより全てのソースコードが初期ソースコードと等価であるはず
    for (int i = 0; i < targetProject.getProductSourcePaths()
        .size(); i++) {
      // TODO list内部要素の順序が変更されたらバグる
      final String expected = initialSourceCode.getProductAsts()
          .get(i)
          .getSourceCode();
      final String actual = generatedSourceCode.getProductAsts()
          .get(i)
          .getSourceCode();
      assertThat(actual).isEqualTo(expected);
    }
  }

  // TODO: None以外のOperationでテストする必要有り

  @SuppressWarnings("deprecation")
  private VariantStore getVariantStore(final Variant initialVariant) {
    return new VariantStore(initialVariant);
  }
}
