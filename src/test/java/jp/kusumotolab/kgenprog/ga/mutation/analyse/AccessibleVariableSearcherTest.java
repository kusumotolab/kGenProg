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

    final ASTLocation line4 = locations.get(1); // String str_1 = ""
    final List<Variable> variables = searcher.exec(line4);
    assertThat(variables).hasSize(1);
    assertThat(variables.get(0)).returns("String", Variable::getFqn);
    assertThat(variables.get(0)).returns("str_1", Variable::getName);


    final ASTLocation line6 = locations.get(4); // String str_2_1, str2_2 = "";
    assertThat(searcher.exec(line6)).hasSize(3);

    final ASTLocation line8 = locations.get(7); // String str_3 = ""
    assertThat(searcher.exec(line8)).hasSize(4);

    final ASTLocation line10 = locations.get(9); // String str_4 = ""
    assertThat(searcher.exec(line10)).hasSize(4);

    final ASTLocation line12 = locations.get(10); // String str_5 = ""
    assertThat(searcher.exec(line12)).hasSize(4);

    final ASTLocation line16 = locations.get(13); // String str_6 = ""
    assertThat(searcher.exec(line16)).hasSize(2);

    final ASTLocation line18 = locations.get(14); // String str_7 = ""
    assertThat(searcher.exec(line18)).hasSize(2);
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