package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;


public class ASTStreamTest {

  @Test
  public void test() {
    final String source = new StringBuilder().append("")
        .append("class A {\n")
        .append("  public int a() {\n")
        .append("    int v;\n")
        .append("    return v + 1;\n")
        .append("  }\n")
        .append("}")
        .toString();

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);

    final List<ASTNode> actual = ASTStream.stream(ast.getRoot())
        .collect(Collectors.toList());

    assertThat(actual).extracting(e -> e.getClass()
        .getSimpleName())
        .containsExactly("CompilationUnit", "TypeDeclaration", "SimpleName", "MethodDeclaration",
            "Modifier", "PrimitiveType", "SimpleName", "Block", "VariableDeclarationStatement",
            "PrimitiveType", "VariableDeclarationFragment", "SimpleName", "ReturnStatement",
            "InfixExpression", "SimpleName", "NumberLiteral");;
  }

}
