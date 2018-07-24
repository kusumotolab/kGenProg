package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;

public class ReplaceOperationTest {

  private final String source = new StringBuilder().append("")
      .append("class A {")
      .append("  public void a() {")
      .append("    int i = 0;")
      .append("    i = 1;")
      .append("  }")
      .append("}")
      .toString();

  @Test
  public void testReplaceStatement() {

    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Block block = method.getBody();
    final JDTASTLocation location = new JDTASTLocation(path, block);

    // 置換対象生成
    final Block replaceBlock = createReplacementBlockTarget();

    final ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST newAST = (GeneratedJDTAST) code.getAsts()
        .get(0);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        // .append(" int i = 0;") // this block is expected to be replaced
        // .append(" i = 1;")
        .append("    xxx();") // as this
        .append("  }")
        .append("}")
        .toString();

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);
  }

  @Test
  public void testReplaceStatementInList() {
    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));

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

    // 置換対象生成
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("xxx"));
    final Statement replaceStatement = jdtAST.newExpressionStatement(invocation);

    final ReplaceOperation operation = new ReplaceOperation(replaceStatement);

    final GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST newAST = (GeneratedJDTAST) code.getAsts()
        .get(0);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append(" int i = 0;")
        // .append(" i = 1;") // this statement is expected to be replaced
        .append("    xxx();") // as this
        .append("  }")
        .append("}")
        .toString();

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);
  }


  @Test
  public void testReplaceStatementDirectly() {
    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Block block = method.getBody();
    final JDTASTLocation location = new JDTASTLocation(path, block);

    // 置換対象生成
    final Block replaceBlock = createReplacementBlockTarget();
    final ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    operation.applyDirectly(generatedSourceCode, location);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        // .append(" int i = 0;") // this block is expected to be replaced
        // .append(" i = 1;")
        .append("    xxx();") // as this
        .append("  }")
        .append("}")
        .toString();

    assertThat(ast.getRoot()).isSameSourceCodeAs(expected);
  }

  @Test
  public void testReplaceStatementInListDirectly() {
    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));

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

    // 置換対象生成
    final Statement replaceStatement = createReplacementTarget();
    final ReplaceOperation operation = new ReplaceOperation(replaceStatement);

    operation.applyDirectly(generatedSourceCode, location);


    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append(" int i = 0;")
        // .append(" i = 1;") // this statement is expected to be replaced
        .append("    xxx();") // as this
        .append("  }")
        .append("}")
        .toString();

    assertThat(ast.getRoot()).isSameSourceCodeAs(expected);
  }

  private Statement createReplacementTarget() {
    final String source = new StringBuilder().append("")
        .append("class B {")
        .append("  public void b() {")
        .append("    xxx();")
        .append("  }")
        .append("}")
        .toString();

    final TargetSourcePath path = new TargetSourcePath(Paths.get("B.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    return (Statement) type.getMethods()[0].getBody()
        .statements()
        .get(0);
  }

  private Block createReplacementBlockTarget() {
    final String source = new StringBuilder().append("")
        .append("class B {")
        .append("  public void b() {")
        .append("    xxx();")
        .append("  }")
        .append("}")
        .toString();

    final TargetSourcePath path = new TargetSourcePath(Paths.get("B.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    return type.getMethods()[0].getBody();
  }
}
