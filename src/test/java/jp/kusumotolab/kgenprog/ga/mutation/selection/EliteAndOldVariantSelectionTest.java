package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.selection.EliteAndOldVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * EliteAndOldVariantSelectionに関するテストクラス.
 */
public class EliteAndOldVariantSelectionTest {

  /**
   * Variantの算出を正しく行えているかテストする.
   */
  @Test
  public void testExec() {
    final int variantSize = 5;
    final EliteAndOldVariantSelection variantSelection = new EliteAndOldVariantSelection(
        variantSize);
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
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.05", "0.2", "0.15", "0.4", "0.25", "0.6", "0.35", "0.8", "0.45");

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(5)
        .containsExactly("0.8", "0.6", "0.45", "0.4", "0.35");
  }

  /**
   * 空のリストに対するVariantの算出を正しく行えているかテストする.
   * <br>
   * 結果として空のリストを得られることを期待する.
   */
  @Test
  public void testExecForEmptyVariants() {
    final EliteAndOldVariantSelection variantSelection = new EliteAndOldVariantSelection(10);
    final List<Variant> variants1 = Collections.emptyList();
    final List<Variant> variants2 = Collections.emptyList();
    final List<Variant> resultVariants = variantSelection.exec(variants1, variants2);
    assertThat(resultVariants).hasSize(0);
  }

  /**
   * NaNが含まれるリストに対するVariantの算出を正しく行えているかテストする.
   * <br>
   * 結果としてNaNよりも通常の値が優先されることを期待する.
   */
  @Test
  public void testExecForNan() {
    final EliteAndOldVariantSelection variantSelection = new EliteAndOldVariantSelection(10);
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

  /**
   * NaNが多数含まれるリストに対するVariantの比較を正しく行えているかテストする.
   * <br>
   * 結果として個体数を制限し，NaNよりも通常の値が優先されることを期待する.
   */
  @Test
  public void testExecForNanCompare() {
    final EliteAndOldVariantSelection variantSelection = new EliteAndOldVariantSelection(10);

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
   * 個体の選択を正しく行えているかテストする.
   * <br>
   * Fitnessが同値の個体同士ではより古い個体を優先して残す.<br>
   * 各Variantにユニークなidを付加し，それを元にして確認を行う.
   */
  @Test
  public void testOrderOfVariants() {
    final int variantSize = 5;
    final EliteAndOldVariantSelection variantSelection = new EliteAndOldVariantSelection(
        variantSize);
    final List<Variant> current = new ArrayList<>();
    final List<Variant> generated = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final double divider = 10;
      final double value = (1.0d * (i + (i % 2))) / divider;
      final SimpleFitness fitness = new SimpleFitness(value);
      current.add(createVariant(fitness, i));
      generated.add(createVariant(fitness, i + 10));
    }

    final List<Variant> selectedVariants = variantSelection.exec(current, generated);

    assertThat(current).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.2", "0.2", "0.4", "0.4", "0.6", "0.6", "0.8", "0.8", "1.0");

    assertThat(generated).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::toString)
        .hasSize(10)
        .containsExactly("0.0", "0.2", "0.2", "0.4", "0.4", "0.6", "0.6", "0.8", "0.8", "1.0");

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getId)
        .containsSequence(9L, 19L, 7L, 8L, 17L);
  }

  /**
   * Variantを生成するメソッド. Fitnessのみを指定する.
   *
   * @param fitness
   * @return variant
   */
  private Variant createVariant(final Fitness fitness) {
    final Variant variant = new Variant(0, 0, null, null, null, fitness, null, null);
    return variant;
  }

  /**
   * Variantを生成するメソッド. FitnessとIdを指定する.
   *
   * @param fitness
   * @param id
   * @return variant
   */
  private Variant createVariant(final Fitness fitness, final int id) {
    final Variant variant = new Variant(id, 0, null, null, null, fitness, null, null);
    return variant;
  }
}
