package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;

public class GeneratedJDTASTTest {

  private static final String TEST_SOURCE_FILE_NAME = "A.java";
  private static final String TEST_SOURCE = new StringBuilder().append("")
      // Line breaks must be included to inferLocation.
      .append("class A {\n")
      .append("  public void a() {\n")
      .append("    int n = 0;\n")
      .append("    if (n == 1) {\n")
      .append("      System.out.println(n);\n")
      .append("    }\n")
      .append("  }\n")
      .append("  public int b(int n) {\n")
      .append("    if (n < 0) { return -n; }\n")
      .append("    return n;\n")
      .append("  }\n")
      .append("}\n")
      .toString();

  private GeneratedJDTAST ast;

  @Before
  public void setup() {
    final SourcePath testSourcePath = new TargetSourcePath(Paths.get(TEST_SOURCE_FILE_NAME));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    this.ast = constructor.constructAST(testSourcePath, TEST_SOURCE);
  }

  @Test
  public void testInferASTNode01() {
    final List<Location> locations = ast.inferLocations(3);

    assertThat(locations).hasSize(2);
    testLocation(locations.get(0), "{ int n = 0; if (n == 1) { System.out.println(n); }}");
    testLocation(locations.get(1), "int n = 0;");
  }

  @Test
  public void testInferASTNode02() {
    final List<Location> locations = ast.inferLocations(5);

    assertThat(locations).hasSize(4);
    testLocation(locations.get(0), "{ int n = 0; if (n == 1) { System.out.println(n); }}");
    testLocation(locations.get(1), "if (n == 1) { System.out.println(n); }");
    testLocation(locations.get(2), "{ System.out.println(n); }");
    testLocation(locations.get(3), "System.out.println(n);");
  }

  @Test
  public void testInferASTNode03() {
    final List<Location> locations = ast.inferLocations(1);

    assertThat(locations).hasSize(0);
  }

  @Test
  public void testInferASTNode04() {
    final List<Location> locations = ast.inferLocations(9);

    testLocation(locations.get(0), "{ if (n < 0) { return -n; } return n;}");
    testLocation(locations.get(1), "if (n < 0) { return -n;}");
    testLocation(locations.get(2), "{ return -n;}");
    testLocation(locations.get(3), "return -n;");
  }

  private void testLocation(final Location target, final String expected) {
    assertThat(target).isInstanceOf(JDTLocation.class);
    final JDTLocation jdtLocation = (JDTLocation) target;
    assertThat(jdtLocation.node).isSameSourceCodeAs(expected);
  }

  @Test
  public void testgetPrimaryClassName01() {
    final String source = "package a.b.c; class T1{} public class T2{}";
    final SourcePath path = new TargetSourcePath(Paths.get("a/b/c/T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.T2");
  }

  @Test
  public void testgetPrimaryClassName02() {
    final String source = "class T1{} public class T2{}";
    final SourcePath path = new TargetSourcePath(Paths.get("T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    assertThat(ast.getPrimaryClassName()).isEqualTo("T2");
  }

  @Test
  public void testgetPrimaryClassName03() {
    final String source = "package a.b.c; class T1{} class T2{} class T3{}";
    final SourcePath path = new TargetSourcePath(Paths.get("a/b/c/T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.T1");
  }

  @Test
  public void testgetPrimaryClassName04() {
    final String source = "package a.b.c;";
    final SourcePath path = new TargetSourcePath(Paths.get("a/b/c/package-info.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(path, source);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.package-info");
  }

  @Test
  public void testStaticImport() {
    final String testSource = "import static java.lang.Math.max; class StaticImport{ }";
    final SourcePath testSourcePath = new TargetSourcePath(Paths.get("StaticImport.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast = constructor.constructAST(testSourcePath, testSource);

    @SuppressWarnings("unchecked")
    final List<ImportDeclaration> imports = ast.getRoot()
        .imports();
    assertThat(imports).hasSize(1);
    assertThat(imports.get(0)
        .isStatic()).isTrue();
  }

  @Test
  public void testGetAllLocations() {
    final List<Location> locations = ast.getAllLocations();
    assertThat(locations).hasSize(10);

    testLocation(locations.get(0), "{ int n = 0; if (n == 1) { System.out.println(n); }}");
    testLocation(locations.get(1), "int n = 0;");
    testLocation(locations.get(2), "if (n == 1) { System.out.println(n); }");
    testLocation(locations.get(3), "{ System.out.println(n); }");
    testLocation(locations.get(4), "System.out.println(n);");
    testLocation(locations.get(5), "{ if (n < 0) { return -n; } return n;}");
    testLocation(locations.get(6), "if (n < 0) { return -n;}");
    testLocation(locations.get(7), "{ return -n;}");
    testLocation(locations.get(8), "return -n;");
    testLocation(locations.get(9), "return n;");
  }

  @Test
  public void testInferLocationAfterInsertOperation() {
    final String bc = "example/example01/src/jp/kusumotolab/BuggyCalculator.java";
    final SourcePath path = new TargetSourcePath(Paths.get(bc));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts = constructor.constructAST(Collections.singletonList(path));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);

    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;");

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(path, statement);

    // 挿入対象生成
    final AST ast = jdtAst.getRoot()
        .getAST();
    final MethodInvocation methodInvocation = ast.newMethodInvocation();
    methodInvocation.setName(ast.newSimpleName("a"));
    final Statement insertStatement = ast.newExpressionStatement(methodInvocation);
    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedJDTAST newJdtAst =
        (GeneratedJDTAST) operation.apply(generatedSourceCode, location)
            .getAsts()
            .get(0);

    testLocation(newJdtAst.inferLocations(10)
        .get(1), "a();");
    testLocation(newJdtAst.inferLocations(11)
        .get(1), "return n;");


  }

  @Test
  public void testInferLocationAfterDeleteOperation() {
    final String bc = "example/example01/src/jp/kusumotolab/BuggyCalculator.java";
    final SourcePath path = new TargetSourcePath(Paths.get(bc));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts = constructor.constructAST(Collections.singletonList(path));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);

    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;");

    // 削除位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(path, statement);
    final DeleteOperation operation = new DeleteOperation();

    final GeneratedJDTAST newJdtAst =
        (GeneratedJDTAST) operation.apply(generatedSourceCode, location)
            .getAsts()
            .get(0);

    testLocation(newJdtAst.inferLocations(4)
        .get(1), "return n;");


  }

  @Test
  public void testInferLocationAfterReplaceOperation() {
    final String bc = "example/example01/src/jp/kusumotolab/BuggyCalculator.java";
    final SourcePath path = new TargetSourcePath(Paths.get(bc));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts = constructor.constructAST(Collections.singletonList(path));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);

    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;");

    // 置換位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(path, statement);


    // 置換対象の生成
    final AST ast = jdtAst.getRoot()
        .getAST();
    final MethodInvocation methodInvocationA = ast.newMethodInvocation();
    methodInvocationA.setName(ast.newSimpleName("a"));
    final MethodInvocation methodInvocationB = ast.newMethodInvocation();
    methodInvocationB.setName(ast.newSimpleName("b"));
    final Block block = ast.newBlock();

    @SuppressWarnings("unchecked")
    final List<Statement> blockStatementList = block.statements();

    blockStatementList.add(ast.newExpressionStatement(methodInvocationA));
    blockStatementList.add(ast.newExpressionStatement(methodInvocationB));

    final ReplaceOperation operation = new ReplaceOperation(block);

    final GeneratedJDTAST newJdtAst =
        (GeneratedJDTAST) operation.apply(generatedSourceCode, location)
            .getAsts()
            .get(0);

    testLocation(newJdtAst.inferLocations(8)
        .get(1), "return n;");
  }

  @Test
  public void testGetMessageDigest01() {
    final String bc = "example/example01/src/jp/kusumotolab/BuggyCalculator.java";
    final SourcePath path = new TargetSourcePath(Paths.get(bc));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts = constructor.constructAST(Collections.singletonList(path));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);

    assertThat(jdtAst.getMessageDigest()).isEqualTo("2770DD8D6E41A26A02F95939D03E89DF");
  }

  @Test
  public void testGetMessageDigest02() {
    final String source1 = "class A { public void a() { b(1); } public void b(int v){}}";
    final String source2 = "class A { public void a() { b(1); } public void b(int v){}}         ";
    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast1 = constructor.constructAST(path, source1);
    final GeneratedJDTAST ast2 = constructor.constructAST(path, source2);

    assertThat(ast1.getMessageDigest()).isEqualTo(ast2.getMessageDigest());
  }

  @Test
  public void testGetMessageDigest03() {
    final String source1 = "class A { public void a() { b(1); } public void b(int v){}}";
    final String source2 = "class A { public void a() { b(2); } public void b(int v){}}";
    final SourcePath path = new TargetSourcePath(Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST ast1 = constructor.constructAST(path, source1);
    final GeneratedJDTAST ast2 = constructor.constructAST(path, source2);

    assertThat(ast1.getMessageDigest()).isNotEqualTo(ast2.getMessageDigest());
  }
}
