package jp.kusumotolab.kgenprog.ga.mutation;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class AccessibleVariableSearcherTest {

  private List<GeneratedAST<ProductSourcePath>> asts;

  @Before
  public void setUp() {
    final Path path = Paths.get("example", "Variable01");
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final TargetProject targetProject = factory.create();
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(
        targetProject);
    this.asts = sourceCode.getProductAsts();
  }

  @Test
  public void test01() {
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

    final ASTLocation line11 = locations.get(6); // String str_3 = ""
    assertThat(searcher.exec(line11)).hasSize(7);

    final ASTLocation line13 = locations.get(7); // String str_4 = ""
    assertThat(searcher.exec(line13)).hasSize(7);

    final ASTLocation line15 = locations.get(8); // String str_5 = ""
    assertThat(searcher.exec(line15)).hasSize(7);

    final ASTLocation line20 = locations.get(11); // String str_6 = ""
    assertThat(searcher.exec(line20)).hasSize(5);

    final ASTLocation line22 = locations.get(12); // String str_7 = ""
    assertThat(searcher.exec(line22)).hasSize(5);
  }

  @Test
  public void testForForStatement() {
    final GeneratedAST<ProductSourcePath> ast = asts.get(0);
    final List<ASTLocation> locations = ast.createLocations()
        .getAll();
    final AccessibleVariableSearcher searcher = new AccessibleVariableSearcher();

    final ASTLocation location = locations.get(15); // System.out.println(i);
    final List<Variable> variables = searcher.exec(location);
    assertThat(variables).hasSize(4);

    final Optional<Variable> variable = extractVariableFromName(variables, "i");
    assertThat(variable).isPresent()
        .hasValue(new Variable("i", "int", false));
  }

  @Test
  public void testForEnhancedForStatement() {
    final GeneratedAST<ProductSourcePath> ast = asts.get(0);
    final List<ASTLocation> locations = ast.createLocations()
        .getAll();
    final AccessibleVariableSearcher searcher = new AccessibleVariableSearcher();

    final ASTLocation location = locations.get(18); // System.out.println(string);
    final List<Variable> variables = searcher.exec(location);
    assertThat(variables).hasSize(5);

    final Optional<Variable> variable = extractVariableFromName(variables, "string");
    assertThat(variable).isPresent()
        .hasValue(new Variable("string", "String", true));
  }

  private Optional<Variable> extractVariableFromName(final List<Variable> variables,
      final String name) {
    return variables.stream()
        .filter(e -> e.getName()
            .equals(name))
        .findFirst();
  }
}
