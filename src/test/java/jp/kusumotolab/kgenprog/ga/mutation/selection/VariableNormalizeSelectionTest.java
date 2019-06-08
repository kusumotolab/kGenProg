package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class VariableNormalizeSelectionTest {

  @Test
  public void testExec02_1() {
    final List<GeneratedAST<ProductSourcePath>> generatedASTs = constructASTs("02");
    final VariableNormalizeSelection selection = new VariableNormalizeSelection(new Random(0));
    selection.setCandidates(generatedASTs);
    final ASTNode node = selection.exec(Arrays.asList(
        new Variable("text", new TargetFullyQualifiedName("String")),
        new Variable("number", new TargetFullyQualifiedName("int"))
    ));
    assertThat(node).returns("System.out.println(text + String.valueOf(number));\n",
        ASTNode::toString);
  }

  @Test
  public void testExec02_2() {
    final List<GeneratedAST<ProductSourcePath>> generatedASTs = constructASTs("02");
    final VariableNormalizeSelection selection = new VariableNormalizeSelection(new Random(0));
    selection.setCandidates(generatedASTs);
    final ASTNode node = selection.exec(Collections.singletonList(
        new Variable("number", new TargetFullyQualifiedName("bool"))
    ));
    // boolの型はないので，変数宣言のみしか再利用候補にならない
    assertThat(node).returns("int a=0;\n",
        ASTNode::toString);
  }

  private List<GeneratedAST<ProductSourcePath>> constructASTs(final String projectCode) {
    final Path path = Paths.get("example", "VariableSample" + projectCode);
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final TargetProject targetProject = factory.create();
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(
        targetProject);
    return sourceCode.getProductAsts();
  }
}