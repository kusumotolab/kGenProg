package jp.kusumotolab.kgenprog.output;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Test;
import com.google.gson.Gson;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class CrossoverHistoricalElementSerializerTest {

  private final Gson gson = TestUtil.createGson();
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();

  private Variant createVariant(final Fitness fitness, final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject), new EmptyTestResults("for testing."), fitness,
        Collections.emptyList(), new OriginalHistoricalElement());
  }

  private Variant createVariant(final long id, final int generationNumber, final Fitness fitness,
      final GeneratedSourceCode code, final HistoricalElement historicalElement) {
    return new Variant(id, generationNumber, new Gene(Collections.emptyList()), code,
        new EmptyTestResults("for testing."), fitness, Collections.emptyList(), historicalElement);
  }


  /**
   * Variantがシリアライズされているかテストする(交叉)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testCrossover() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 親1
    final Variant parentA = createVariant(1L, 1, new SimpleFitness(0.0d),
        new GenerationFailedSourceCode(""), new MutationHistoricalElement(initialVariant,
            new Base(null, new InsertAfterOperation(null))));

    // 親2
    final Variant parentB = createVariant(2L, 1, new SimpleFitness(0.0d),
        new GenerationFailedSourceCode(""), new MutationHistoricalElement(initialVariant,
            new Base(null, new InsertAfterOperation(null))));

    // 子供
    final HistoricalElement historicalElement = new CrossoverHistoricalElement(parentA, parentB, 1);
    final String serializedHistoricalElement = gson.toJson(historicalElement);

    assertThatJson(serializedHistoricalElement).isObject()
        .containsOnlyKeys(JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS,
            JsonKeyAlias.CrossoverHistoricalElement.NAME,
            JsonKeyAlias.CrossoverHistoricalElement.CROSSOVER_POINT);
    assertThatJson(serializedHistoricalElement).node(
        JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS)
        .isArray()
        .containsOnly(1, 2);
    assertThatJson(serializedHistoricalElement).node(
        JsonKeyAlias.CrossoverHistoricalElement.NAME)
        .isEqualTo("crossover");
  }
}
