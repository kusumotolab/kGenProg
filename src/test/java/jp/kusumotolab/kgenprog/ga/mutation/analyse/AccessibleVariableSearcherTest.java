package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class AccessibleVariableSearcherTest {

  @Test
  public void test01() {
    final List<GeneratedAST<ProductSourcePath>> asts = constructASTs("01");
    assertThat(asts).isNotEmpty();

    final AccessibleVariableSearcher searcher = new AccessibleVariableSearcher();
    final GeneratedAST<ProductSourcePath> ast = asts.get(0);
    final List<ASTLocation> locations = ast.createLocations()
        .getAll();

    final ASTLocation line7 = locations.get(1); // String str_1 = ""
    final List<Variable> variables = searcher.exec(line7);
    assertThat(variables).hasSize(4);

    assertThat(variables.get(0)).returns("int", v -> v.getFqn()
        .toString());
    assertThat(variables.get(0)).returns("int_1", Variable::getName);

    final ASTLocation line9 = locations.get(4); // String str_2_1, str2_2 = "";
    assertThat(searcher.exec(line9)).hasSize(5);

    final ASTLocation line11 = locations.get(7); // String str_3 = ""
    assertThat(searcher.exec(line11)).hasSize(7);

    final ASTLocation line13 = locations.get(9); // String str_4 = ""
    assertThat(searcher.exec(line13)).hasSize(7);

    final ASTLocation line15 = locations.get(10); // String str_5 = ""
    assertThat(searcher.exec(line15)).hasSize(7);

    final ASTLocation line20 = locations.get(14); // String str_6 = ""
    assertThat(searcher.exec(line20)).hasSize(5);

    final ASTLocation line22 = locations.get(15); // String str_7 = ""
    assertThat(searcher.exec(line22)).hasSize(5);
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