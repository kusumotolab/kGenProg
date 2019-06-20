package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.mutation.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.Variable;
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

    final Optional<Variable> int_1 = extractVariableFromName(variables, "int_1");
    assertThat(int_1).isPresent()
        .hasValue(new Variable("int_1", "int", true));

    final Optional<Variable> double_1 = extractVariableFromName(variables, "double_1");
    assertThat(double_1).isPresent()
        .hasValue(new Variable("double_1", "double", true));


    final Optional<Variable> double_2_1 = extractVariableFromName(variables, "double_2_1");
    assertThat(double_2_1).isPresent()
        .hasValue(new Variable("double_2_1", "double", false));

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

  private Optional<Variable> extractVariableFromName(final List<Variable> variables,
      final String name) {
    return variables.stream()
        .filter(e -> e.getName()
            .equals(name))
        .findFirst();
  }
}
