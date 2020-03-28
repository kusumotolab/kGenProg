package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class StatementListVisitorTest {

  private static final String FILE_NAME_A = "A.java";
  private static final String SOURCE_A = new StringBuilder()
      // Line breaks must be included to execute GeneratedJDTAST#inferLocation.
      .append("class A {\n")
      .append("  public void a() {\n")
      .append("    if (true) {\n")
      .append("      int i = 0;\n")
      .append("    }\n")
      .append("  }\n")
      .append("}\n")
      .toString();

  private static final String FILE_NAME_B = "B.java";
  private static final String SOURCE_B = new StringBuilder()
      // Line breaks must be included to execute GeneratedJDTAST#inferLocation.
      .append("class B {\n")
      .append("  public void a() {\n")
      .append("    if (true) {\n")
      .append("      int j = 1;\n")
      .append("      int k = 2;\n")
      .append("    }\n")
      .append("  }\n")
      .append("}\n")
      .toString();

  @Test
  public void test_consumeStatement01() {

    // generating an AST
    // ASTを作成
    final ProductSourcePath productSourcePath =
        new ProductSourcePath(Paths.get("."), Paths.get(FILE_NAME_A));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(
        productSourcePath, SOURCE_A);

    // assuming that any instance of Block is not included in statements
    // Blockが含まれていないはず
    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeElements(ast.getRoot());
    final List<ASTNode> statements = visitor.getElements();
    assertThat(statements).noneMatch(s -> Block.class == s.getClass());
  }

  @Test
  public void test_consumeStatement02() {

    // generating an AST
    // ASTを作成
    final ProductSourcePath productSourcePath =
        new ProductSourcePath(Paths.get("."), Paths.get(FILE_NAME_B));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(
        productSourcePath, SOURCE_B);

    // assuming that any instance of Block is included in statements
    // Blockが含まれているはず
    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeElements(ast.getRoot());
    final List<ASTNode> statements = visitor.getElements();
    assertThat(statements).anyMatch(s -> Block.class == s.getClass());
  }
}
