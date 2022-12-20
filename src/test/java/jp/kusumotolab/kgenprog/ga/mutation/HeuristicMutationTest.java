package jp.kusumotolab.kgenprog.ga.mutation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.HeuristicStatementSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class HeuristicMutationTest {

  @Test
  public void testChooseNodeForReuse() {
    final Path path = Paths.get("example/Variable03");
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(factory.create());

    final List<GeneratedAST<ProductSourcePath>> generatedASTs = sourceCode.getProductAsts();
    final Random random = mock(Random.class, withSettings().withoutAnnotations());
    when(random.nextDouble()).thenReturn(0.0);

    final HeuristicStatementSelection selection = new HeuristicStatementSelection(random);
    final HeuristicMutation mutation = new HeuristicMutation(0, random, selection, 1, Type.PACKAGE);
    mutation.setCandidates(generatedASTs);

    final ASTLocation location = generatedASTs.get(0)
        .createLocations()
        .getAll()
        .get(0); // return number;

    // ここで使える操作はReplaceかInsertBeforeのみであり，モックによってInsertBefore選択される
    final Operation operation = mutation.makeOperation(location);
    assertThat(operation).isInstanceOf(InsertBeforeOperation.class);

    final InsertBeforeOperation insertBeforeOperation = (InsertBeforeOperation) operation;

    // その場で使える変数名に書き換える
    final String expected = "System.out.println(text + String.valueOf(number));\n"; // このノードは存在しない
    assertThat(insertBeforeOperation.getTargetSnippet()).isEqualTo(expected);
  }
}
