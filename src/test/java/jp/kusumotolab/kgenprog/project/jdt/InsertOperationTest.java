package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
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

public class InsertOperationTest {

  @Test
  public void testInsertStatement() {
    final String source = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        .append("    i = 1;")
        .append("  }")
        .append("}")
        .toString();

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);
    @SuppressWarnings("unchecked")
    final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTASTLocation location = new JDTASTLocation(sourcePath, statement, ast);

    // 挿入対象生成
    final Statement insertStatement = createInsertionTarget();
    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> newAST =
        (GeneratedJDTAST<ProductSourcePath>) code.getProductAsts()
            .get(0);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        .append("    i = 1;")
        .append("    xxx();") // inserted statement
        .append("  }")
        .append("}")
        .toString();

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);

    // TestASTがそのまま受け継がれているか確認
    assertThat(code.getTestAsts()).hasSize(1);
    assertThat(code.getTestAsts()
        .get(0)).isSameAs(mockAst);
  }

  private Statement createInsertionTarget() {
    final String source = new StringBuilder().append("")
        .append("class B {")
        .append("  public void b() {")
        .append("    xxx();")
        .append("  }")
        .append("}")
        .toString();

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("B.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    return (Statement) type.getMethods()[0].getBody()
        .statements()
        .get(0);
  }
}
