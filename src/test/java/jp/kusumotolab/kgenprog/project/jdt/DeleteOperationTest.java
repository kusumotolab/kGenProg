package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class DeleteOperationTest {

  final String source = new StringBuilder()
      .append("class A {")
      .append("  public void a() {")
      .append("    int i = 0;")
      .append("    i = 1;")
      .append("  }")
      .append("}")
      .toString();

  @Test
  public void testDeleteStatement() {
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source, StandardCharsets.UTF_8);
    @SuppressWarnings("unchecked")
    final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(
        GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTASTLocation location = new JDTASTLocation(path, statement, ast);
    final DeleteOperation operation = new DeleteOperation();

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> newAST =
        (GeneratedJDTAST<ProductSourcePath>) code.getProductAsts()
            .get(0);

    final String expected = new StringBuilder()
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        // .append(" i = 1;") // deleted statement
        .append("  }")
        .append("}")
        .toString();

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);

    // TestASTがそのまま受け継がれているか確認
    assertThat(code.getTestAsts()).hasSize(1);
    assertThat(code.getTestAsts()
        .get(0)).isSameAs(mockAst);
  }
}
