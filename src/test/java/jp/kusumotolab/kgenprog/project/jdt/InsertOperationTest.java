package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;

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

    final TargetSourcePath path = new TargetSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTLocation location = new JDTLocation(path, statement);

    // 挿入対象生成
    final Statement insertStatement = createInsertionTarget();
    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST newAST = (GeneratedJDTAST) code.getAsts()
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
  }

  @Test
  public void testInsertStatementDirectly() {
    final String source = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        .append("    i = 1;")
        .append("  }")
        .append("}")
        .toString();

    final TargetSourcePath sourcePath = new TargetSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(sourcePath, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    final JDTLocation location = new JDTLocation(sourcePath, statement);

    // 挿入対象生成
    final Statement insertStatement = createInsertionTarget();
    final InsertOperation operation = new InsertOperation(insertStatement);

    operation.applyDirectly(generatedSourceCode, location);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("    int i = 0;")
        .append("    i = 1;")
        .append("    xxx();") // inserted statement
        .append("  }")
        .append("}")
        .toString();

    assertThat(ast.getRoot()).isSameSourceCodeAs(expected);
  }

  private Statement createInsertionTarget() {
    final String source = new StringBuilder().append("")
        .append("class B {")
        .append("  public void b() {")
        .append("    xxx();")
        .append("  }")
        .append("}")
        .toString();

    final TargetSourcePath testSourcePath = new TargetSourcePath(Paths.get("B.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(testSourcePath, source);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    return (Statement) type.getMethods()[0].getBody()
        .statements()
        .get(0);
  }
}
