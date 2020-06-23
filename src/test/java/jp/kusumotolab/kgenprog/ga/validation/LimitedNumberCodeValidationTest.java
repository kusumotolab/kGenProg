package jp.kusumotolab.kgenprog.ga.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation.Input;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class LimitedNumberCodeValidationTest {

  @Test
  public void testExec() {

    // 親バリアントの準備
    final Gene parentGene = new Gene(Arrays.asList(new Base(null, new NoneOperation())));
    final TestResults parentTestResults = mock(TestResults.class);
    when(parentTestResults.getSuccessRate()).thenReturn(0.8d);
    final Input parentInput = mock(Input.class);
    when(parentInput.getGene()).thenReturn(parentGene);
    when(parentInput.getTestResults()).thenReturn(parentTestResults);

    // 評価用LimitedNumberCodeValidationの生成，リミットは3．
    final LimitedNumberCodeValidation validation = new LimitedNumberCodeValidation(3);

    // 親バリアントの評価（一回目）
    final Fitness parentFitness = validation.exec(parentInput);
    assertThat(parentFitness.getSingularValue()).isEqualTo(0.8d);

    // 子バリアントを4つ生成
    final List<Gene> childGenes = parentGene.generateNextGenerationGenes(Arrays.asList(//
        new Base(null, new InsertAfterOperation(null)), //
        new Base(null, new InsertBeforeOperation(null)), //
        new Base(null, new DeleteOperation()), //
        new Base(null, new ReplaceOperation(null))));
    final Input[] childInputs = new Input[childGenes.size()];
    for (int index = 0; index < childInputs.length; index++) {
      final TestResults testResults = mock(TestResults.class);
      when(testResults.getSuccessRate()).thenReturn(0.3d); // 全員バカ息子
      childInputs[index] = mock(Input.class);
      when(childInputs[index].getGene()).thenReturn(childGenes.get(index));
      when(childInputs[index].getTestResults()).thenReturn(testResults);
    }

    // バカ息子0の評価後，親の評価はまだ高いはず
    final Fitness childFitness0 = validation.exec(childInputs[0]);
    assertThat(childFitness0.getSingularValue()).isEqualTo(0.3d);
    assertThat(parentFitness.getSingularValue()).isEqualTo(0.8d);

    // バカ息子1の評価後，親の評価はまだ高いはず
    final Fitness childFitness1 = validation.exec(childInputs[1]);
    assertThat(childFitness1.getSingularValue()).isEqualTo(0.3d);
    assertThat(parentFitness.getSingularValue()).isEqualTo(0.8d);

    // バカ息子2の評価後，親の評価は低くなっているはず
    final Fitness childFitness2 = validation.exec(childInputs[2]);
    assertThat(childFitness2.getSingularValue()).isEqualTo(0.3d);
    assertThat(parentFitness.getSingularValue()).isEqualTo(0.0d);

    // バカ息子3の評価後，親の評価は相変わらず低いはず
    final Fitness childFitness3 = validation.exec(childInputs[3]);
    assertThat(childFitness3.getSingularValue()).isEqualTo(0.3d);
    assertThat(parentFitness.getSingularValue()).isEqualTo(0.0d);

    // 無関係のバリアントが来ても，その評価値は0にならないはず
    final Gene anotherGene = new Gene(Arrays.asList(//
        new Base(null, new DeleteOperation()), //
        new Base(null, new DeleteOperation())));
    final TestResults anotherTestResults = mock(TestResults.class);
    when(anotherTestResults.getSuccessRate()).thenReturn(0.6d);
    final Input anotherInput = mock(Input.class);
    when(anotherInput.getGene()).thenReturn(anotherGene);
    when(anotherInput.getTestResults()).thenReturn(anotherTestResults);
    final Fitness anotherFitness = validation.exec(anotherInput);
    assertThat(anotherFitness.getSingularValue()).isEqualTo(0.6d);
  }
}
