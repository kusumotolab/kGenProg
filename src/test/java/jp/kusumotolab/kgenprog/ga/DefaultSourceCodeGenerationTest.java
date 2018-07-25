package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class DefaultSourceCodeGenerationTest {

  @Test
  public void testExec() {
    final Path rootDir = Paths.get("example/example01");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    final Base base = new Base(null, new NoneOperation());
    final SimpleGene gene = new SimpleGene(Collections.singletonList(base));
    final DefaultSourceCodeGeneration defaultSourceCodeGeneration = new DefaultSourceCodeGeneration();

    // 1回目の生成は正しく生成される
    final GeneratedSourceCode firstGeneratedSourceCode = defaultSourceCodeGeneration.exec(gene,
        targetProject);
    assertThat(firstGeneratedSourceCode).isNotEqualTo(GenerationFailedSourceCode.instance);

    // 2回目の生成は失敗する
    final GeneratedSourceCode secondGeneratedSourceCode = defaultSourceCodeGeneration.exec(gene,
        targetProject);
    assertThat(secondGeneratedSourceCode).isEqualTo(GenerationFailedSourceCode.instance);
  }

  @Test
  public void noneOperationTest() {
    final TargetProject targetProject = TargetProjectFactory.create(Paths.get("example/example01"));
    final Variant initialVariant = targetProject.getInitialVariant();
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();

    final Gene simpleGene = new SimpleGene(new ArrayList<>());
    final Base noneBase = new Base(null, new NoneOperation());
    final List<Gene> genes = simpleGene.generateNextGenerationGenes(Arrays.asList(noneBase));

    // noneBaseを適用した単一のGeneを取り出す
    final Gene gene = genes.get(0);

    final GeneratedSourceCode generatedSourceCode = sourceCodeGeneration.exec(gene, targetProject);
    final GeneratedSourceCode initialSourceCode = initialVariant.getGeneratedSourceCode();

    assertThat(generatedSourceCode.getAsts()).hasSameSizeAs(initialSourceCode.getAsts());

    // NoneOperationにより全てのソースコードが初期ソースコードと等価であるはず
    for (int i = 0; i < targetProject.getSourcePaths()
        .size(); i++) {
      // TODO list内部要素の順序が変更されたらバグる
      final String expected = initialSourceCode.getAsts()
          .get(i)
          .getSourceCode();
      final String actual = generatedSourceCode.getAsts()
          .get(i)
          .getSourceCode();
      assertThat(actual).isEqualTo(expected);
    }
  }

  // TODO: None以外のOperationでテストする必要有り
}
