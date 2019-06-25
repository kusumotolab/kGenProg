package jp.kusumotolab.kgenprog.ga.mutation;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.HeuristicStatementSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class HeuristicMutationTest {

  @Test
  public void testChooseNodeForReuse() {
    final Path path = Paths.get("example/VariableSample03");
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(factory.create());

    final List<GeneratedAST<ProductSourcePath>> generatedASTs = sourceCode.getProductAsts();
    final Random random = new Random(0);
    final HeuristicStatementSelection selection = new HeuristicStatementSelection(random);
    final HeuristicMutation mutation = new HeuristicMutation(0, random, selection, Type.PACKAGE,
        false);
    mutation.setCandidates(generatedASTs);

    final ASTLocation location = generatedASTs.get(0)
        .createLocations()
        .getAll()
        .get(1); // int a=0;\n

    final ASTNode node = mutation.chooseNodeForReuse(location);
    final String expected = "System.out.println(text + String.valueOf(number));\n"; // このノードは存在しない
    assertThat(node.toString()).isEqualTo(expected);
  }
}
