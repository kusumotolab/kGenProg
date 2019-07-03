package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.Variable;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class HeuristicStatementSelectionTest {

  private List<GeneratedAST<ProductSourcePath>> generatedASTs;

  @Before
  public void setUp() {
    final Path path = Paths.get("example/Variable02");
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final TargetProject targetProject = factory.create();
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(
        targetProject);
    generatedASTs = sourceCode.getProductAsts();
  }

  @Test
  public void testExec() {
    final HeuristicStatementSelection selection = new HeuristicStatementSelection(new Random(0));
    selection.setCandidates(generatedASTs);
    final Query query = new Query(Arrays.asList(
        new Variable("text", new TargetFullyQualifiedName("String"), false),
        new Variable("number", new TargetFullyQualifiedName("int"), false)
    ));
    final ASTNode node = selection.exec(query);

    assertThat(node).isSameSourceCodeAs("System.out.println(str + String.valueOf(num));\n");
  }

  @Test
  public void testExecForFilter() {
    final HeuristicStatementSelection selection = new HeuristicStatementSelection(new Random(0));
    selection.setCandidates(generatedASTs);
    final Query query = new Query(Collections.singletonList(
        new Variable("number", new TargetFullyQualifiedName("bool"), false)
    ));
    final ASTNode node = selection.exec(query);
    // boolの型はないので，変数宣言のみしか再利用候補にならない
    assertThat(node).isSameSourceCodeAs("int a=0;\n");
  }
}
