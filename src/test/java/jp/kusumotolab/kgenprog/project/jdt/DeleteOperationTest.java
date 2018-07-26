package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class DeleteOperationTest {

  final String source = new StringBuilder().append("")
      .append("class A {")
      .append("  public void a() {")
      .append("    int i = 0;")
      .append("    i = 1;")
      .append("  }")
      .append("}")
      .toString();

  @Test
  public void testDeleteStatement() {
    final ProductSourcePath path = new ProductSourcePath(Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTASTLocation location = new JDTASTLocation(path, statement);
    final DeleteOperation operation = new DeleteOperation();

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST newAST = (GeneratedJDTAST) code.getAsts()
        .get(0);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        // .append(" i = 1;") // deleted statement
        .append("  }")
        .append("}")
        .toString();

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);
  }

  @Test
  public void testDeleteStatementDirectly() {
    final ProductSourcePath path = new ProductSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTASTLocation location = new JDTASTLocation(path, statement);
    final DeleteOperation operation = new DeleteOperation();

    operation.applyDirectly(generatedSourceCode, location);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        // .append(" i = 1;") // deleted statement
        .append("  }")
        .append("}")
        .toString();

    assertThat(ast.getRoot()).isSameSourceCodeAs(expected);
  }

}
