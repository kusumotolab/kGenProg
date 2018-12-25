package jp.kusumotolab.kgenprog.ga.variant;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;

public class GeneTest {

  @Test
  public void test_getSimilarity() {
    final Base noneOperationBase1 = new Base(null, new NoneOperation());
    final Base noneOperationBase2 = new Base(null, new NoneOperation());
    final Base noneOperationBase3 = new Base(null, new NoneOperation());
    final Base noneOperationBase4 = new Base(null, new NoneOperation());
    final Base noneOperationBase5 = new Base(null, new NoneOperation());
    final Base insertOperationBase1 = new Base(null, new InsertOperation(null));
    final Base insertOperationBase2 = new Base(null, new InsertOperation(null));
    final Base insertOperationBase3 = new Base(null, new InsertOperation(null));
    final Base insertOperationBase4 = new Base(null, new InsertOperation(null));
    final Base insertOperationBase5 = new Base(null, new InsertOperation(null));

    final List<Base> noneBases = Arrays.asList(noneOperationBase1, noneOperationBase2,
        noneOperationBase3, noneOperationBase4, noneOperationBase5);
    final List<Base> insertBases = Arrays.asList(insertOperationBase1, insertOperationBase2,
        insertOperationBase3, insertOperationBase4, insertOperationBase5);

    final Gene nonBasesGene = new Gene(noneBases);
    final Gene insertBasesGene = new Gene(insertBases);

    final double similarity1 = Gene.getSimilarity(nonBasesGene, insertBasesGene);
    assertThat(similarity1).isEqualTo(0.0d);

    final double similarity2 = Gene.getSimilarity(nonBasesGene, nonBasesGene);
    assertThat(similarity2).isEqualTo(1.0d);
  }
}
