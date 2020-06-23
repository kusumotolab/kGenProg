package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultVariantSelectionTest {

  private Random random;

  @Before
  public void setup() {
    this.random = new Random(0);
  }

  @Test
  public void testExec() {
    final int variantSize = 5;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> variants = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final double divider = (i % 2 == 0) ? 10 : 20;
      final double value = (double) i / divider;
      final SimpleFitness fitness = new SimpleFitness(value);
      variants.add(createVariant(fitness));
    }
    final List<Variant> selectedVariants = variantSelection.exec(Collections.emptyList(), variants);

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getSingularValue)
        .hasSize(10)
        .containsExactly(0.00d, 0.05d, 0.20d, 0.15d, 0.40d, 0.25d, 0.60d, 0.35d, 0.80d, 0.45d);

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getSingularValue)
        .hasSize(5)
        .containsExactly(0.80d, 0.60d, 0.45d, 0.40d, 0.35d);
  }

  @Test
  public void testExecForEmptyVariants() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);
    final List<Variant> variants1 = Collections.emptyList();
    final List<Variant> variants2 = Collections.emptyList();
    final List<Variant> resultVariants = variantSelection.exec(variants1, variants2);
    assertThat(resultVariants).hasSize(0);
  }

  @Test
  public void testExecForNan() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);
    final List<Variant> variants = new ArrayList<>();

    final List<Variant> nanVariants = IntStream.range(0, 10)
        .mapToObj(e -> new SimpleFitness(Double.NaN))
        .map(this::createVariant)
        .collect(Collectors.toList());

    variants.addAll(nanVariants);

    final List<Variant> result1 = variantSelection.exec(Collections.emptyList(), variants);

    assertThat(result1).hasSize(10);

    final Variant normalVariant = createVariant(new SimpleFitness(0.5d));
    variants.add(normalVariant);
    final List<Variant> result2 = variantSelection.exec(Collections.emptyList(), variants);
    assertThat(result2).hasSize(10);
    assertThat(result2.get(0)).isEqualTo(normalVariant);
  }

  @Test
  public void testExecForNanCompare() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10, random);

    final List<Variant> nanVariants = IntStream.range(0, 100)
        .mapToObj(e -> {
          if (e == 50) {
            return new SimpleFitness(SimpleFitness.MAXIMUM_VALUE);
          }
          return new SimpleFitness(Double.NaN);
        })
        .map(this::createVariant)
        .collect(Collectors.toList());

    try {
      final List<Variant> result = variantSelection.exec(Collections.emptyList(), nanVariants);
      assertThat(result).hasSize(10);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * 個体の選択を正しく行えているかテストする.<br>
   * Fitnessが同値の個体があればその中からランダムに選択する.<br>
   * 各Variantにユニークなidを付加し，それを元にして確認を行う.
   */
  @Test
  public void testOrderOfVariants() {
    final int variantSize = 5;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> current = new ArrayList<>();
    final List<Variant> generated = new ArrayList<>();

    setupLists(current, generated, 10, e -> new TestResults());

    final List<Variant> selectedVariants = variantSelection.exec(current, generated);

    assertThat(current).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getSingularValue)
        .hasSize(10)
        .containsExactly(0.00d, 0.20d, 0.20d, 0.40d, 0.40d, 0.60d, 0.60d, 0.80d, 0.80d, 1.00d);

    assertThat(generated).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getSingularValue)
        .hasSize(10)
        .containsExactly(0.00d, 0.20d, 0.20d, 0.40d, 0.40d, 0.60d, 0.60d, 0.80d, 0.80d, 1.00d);

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getId)
        .doesNotContainSequence(9L, 19L, 7L, 8L, 17L);
  }

  /**
   * BulidFailedの個体が選択されるかどうかをテストする.
   */
  @Test
  public void testBuildFailed() {
    final int variantSize = 12;
    final DefaultVariantSelection variantSelection =
        new DefaultVariantSelection(variantSize, random);
    final List<Variant> current = new ArrayList<>();
    final List<Variant> generated = new ArrayList<>();

    // 2個に1個はbuildFailedの個体にする
    setupLists(current, generated, 10,
        e -> e % 2 == 0 ? new EmptyTestResults("build failed.") : new TestResults());

    final List<Variant> selectedVariants = variantSelection.exec(current, generated);

    // 12個選択するが，buildSuccessが10個しかないため10個しか返ってこない
    assertThat(selectedVariants).hasSize(10)
        .allMatch(Variant::isBuildSucceeded);
  }

  /**
   * Variantを生成するメソッド.Fitnessのみを指定する.
   *
   * @param fitness Variantの持つFitness
   * @return variant 生成したVariant
   */
  private Variant createVariant(final Fitness fitness) {
    final TestResults testResults = new TestResults();
    return createVariant(fitness, 0, testResults);
  }

  /**
   * Variantを生成するメソッド.Fitness, Id, TestResultsを指定する.
   *
   * @param fitness Variantの持つFitness
   * @param id Variantに固有の値
   * @param testResults Variantのテスト情報
   * @return variant 生成したVariant
   */
  private Variant createVariant(final Fitness fitness, final int id,
      final TestResults testResults) {
    return new Variant(id, 0, null, null, testResults, fitness, null, null);
  }

  /**
   * テスト用のリストを設定するメソッド.
   *
   * @param current variantSelectionに渡すcurrentリスト
   * @param generated variantSelectionに渡すgeneratedリスト
   * @param num リストに追加する要素の数
   * @param testResultsCreator testResultsの渡し方を指定するFuntionオブジェクト（intを受け取りTestResultsを返す）
   */
  private void setupLists(final List<Variant> current, final List<Variant> generated, final int num,
      final Function<Integer, TestResults> testResultsCreator) {
    for (int i = 0; i < num; i++) {
      final double value = (1.0d * (i + (i % 2))) / (double) num;
      final SimpleFitness fitness = new SimpleFitness(value);
      current.add(createVariant(fitness, i, testResultsCreator.apply(i)));
      generated.add(createVariant(fitness, i + num, testResultsCreator.apply(i)));
    }
  }
}
