package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class StatementAndConditionVisitorTest {

  private static final String FILE_NAME_FOR = "For.java";
  private static final String SOURCE_FOR = new StringBuilder()
      // Line breaks must be included to execute GeneratedJDTAST#inferLocation.
      .append("public class For {\n")
      .append("  public int method() {\n")
      .append("    for (int i = 0; ; i++) {\n")
      .append("      if(i == 10) {\n")
      .append("        break;\n")
      .append("      }\n")
      .append("    }\n")
      .append("    return i;\n")
      .append("  }\n")
      .append("}\n")
      .toString();

  @Test
  public void test_consumeStatement01() {

    // generating an AST
    // ASTを作成
    final ProductSourcePath productSourcePath =
        new ProductSourcePath(Paths.get("."), Paths.get(FILE_NAME_FOR));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(
        productSourcePath, SOURCE_FOR, StandardCharsets.UTF_8);

    // assuming that any instance of Block is not included in statements
    // Blockが含まれていないはず
    final StatementAndConditionVisitor visitor = new StatementAndConditionVisitor();
    visitor.analyzeElements(ast.getRoot());
    final List<ASTNode> statements = visitor.getElements();
    assertThat(statements).noneMatch(s -> Block.class == s.getClass());
  }
}

