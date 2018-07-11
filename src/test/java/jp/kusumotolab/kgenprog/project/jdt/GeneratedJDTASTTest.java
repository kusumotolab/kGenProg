package jp.kusumotolab.kgenprog.project.jdt;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

public class GeneratedJDTASTTest {

  private static final String TEST_SOURCE_FILE_NAME = "A.java";
  private static final String TEST_SOURCE = "class A {\n" + "   public void a() {\n"
      + "       int a = 0;\n" + "       if (a == 1) {\n" + "           System.out.println(a);\n"
      + "       }\n" + "   }\n" + "   public int b(int a) {\n"
      + "       if (a < 0) { return -a; }\n" + "       return a;\n" + "   }\n" + "}\n" + "";

  private GeneratedJDTAST ast;

  @Before
  public void setup() {
    SourceFile testSourceFile = new TargetSourceFile(Paths.get(TEST_SOURCE_FILE_NAME));
    JDTASTConstruction constructor = new JDTASTConstruction();
    this.ast = constructor.constructAST(testSourceFile, TEST_SOURCE);
  }

  @Test
  public void testInferASTNode01() {
    List<Location> locations = ast.inferLocations(3);

    assertThat(locations, hasSize(2));
    testLocation(locations.get(0),
        "{\n  int a=0;\n  if (a == 1) {\n    System.out.println(a);\n  }\n}\n");
    testLocation(locations.get(1), "int a=0;\n");
  }

  @Test
  public void testInferASTNode02() {
    List<Location> locations = ast.inferLocations(5);

    assertThat(locations, hasSize(4));
    testLocation(locations.get(0),
        "{\n  int a=0;\n  if (a == 1) {\n    System.out.println(a);\n  }\n}\n");
    testLocation(locations.get(1), "if (a == 1) {\n  System.out.println(a);\n}\n");
    testLocation(locations.get(2), "{\n  System.out.println(a);\n}\n");
    testLocation(locations.get(3), "System.out.println(a);\n");
  }

  @Test
  public void testInferASTNode03() {
    List<Location> locations = ast.inferLocations(1);

    assertThat(locations.size(), is(0));
  }

  @Test
  public void testInferASTNode04() {
    List<Location> locations = ast.inferLocations(9);

    testLocation(locations.get(0), "{\n  if (a < 0) {\n    return -a;\n  }\n  return a;\n}\n");
    testLocation(locations.get(1), "if (a < 0) {\n  return -a;\n}\n");
    testLocation(locations.get(2), "{\n  return -a;\n}\n");
    testLocation(locations.get(3), "return -a;\n");
  }

  private void testLocation(Location target, String expected) {
    assertThat(target, instanceOf(JDTLocation.class));
    JDTLocation jdtLocation = (JDTLocation) target;
    assertThat(jdtLocation.node.toString(), is(expected));
  }

  @Test
  public void testgetPrimaryClassName01() {
    String testSource = "package a.b.c; class T1{} public class T2{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);

    assertThat(ast.getPrimaryClassName(), is("a.b.c.T2"));
  }

  @Test
  public void testgetPrimaryClassName02() {
    String testSource = "class T1{} public class T2{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);

    assertThat(ast.getPrimaryClassName(), is("T2"));
  }

  @Test
  public void testgetPrimaryClassName03() {
    String testSource = "package a.b.c; class T1{} class T2{} class T3{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);

    assertThat(ast.getPrimaryClassName(), is("a.b.c.T1"));
  }

  @Test
  public void testgetPrimaryClassName04() {
    String testSource = "package a.b.c;";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "package-info.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);

    assertThat(ast.getPrimaryClassName(), is("a.b.c.package-info"));
  }

  @Test
  public void testStaticImport() {
    String testSource = "import static java.lang.Math.max; class StaticImport{ }";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("StaticImport.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);

    @SuppressWarnings("unchecked")
    List<ImportDeclaration> imports = ast.getRoot()
        .imports();
    assertThat(imports.size(), is(1));
    assertThat(imports.get(0)
        .isStatic(), is(true));
  }

  @Test
  public void testGetAllLocations() {
    final List<Location> locations = ast.getAllLocations();
    assertThat(locations, hasSize(10));

    testLocation(locations.get(0),
        "{\n  int a=0;\n  if (a == 1) {\n    System.out.println(a);\n  }\n}\n");
    testLocation(locations.get(1), "int a=0;\n");
    testLocation(locations.get(2), "if (a == 1) {\n  System.out.println(a);\n}\n");
    testLocation(locations.get(3), "{\n  System.out.println(a);\n}\n");
    testLocation(locations.get(4), "System.out.println(a);\n");

    testLocation(locations.get(5), "{\n  if (a < 0) {\n    return -a;\n  }\n  return a;\n}\n");
    testLocation(locations.get(6), "if (a < 0) {\n  return -a;\n}\n");
    testLocation(locations.get(7), "{\n  return -a;\n}\n");
    testLocation(locations.get(8), "return -a;\n");
    testLocation(locations.get(9), "return a;\n");
  }

  @Test
  public void testInferLocationAfterInsertOperation() {
    final SourceFile testSourceFile = new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts =
        constructor.constructAST(Collections.singletonList(testSourceFile));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);
    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;\n");

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 挿入対象生成
    final AST ast = jdtAst.getRoot()
        .getAST();
    final MethodInvocation methodInvocation = ast.newMethodInvocation();
    methodInvocation.setName(ast.newSimpleName("a"));
    final Statement insertStatement = ast.newExpressionStatement(methodInvocation);
    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedJDTAST newJdtAst =
        (GeneratedJDTAST) operation.apply(generatedSourceCode, location)
            .getFiles()
            .get(0);
    testLocation(newJdtAst.inferLocations(10)
        .get(1), "a();\n");
    testLocation(newJdtAst.inferLocations(11)
        .get(1), "return n;\n");


  }

  @Test
  public void testInferLocationAfterDeleteOperation() {
    final SourceFile testSourceFile = new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts =
        constructor.constructAST(Collections.singletonList(testSourceFile));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);
    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;\n");

    // 削除位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(testSourceFile, statement);
    final DeleteOperation operation = new DeleteOperation();

    final GeneratedJDTAST newJdtAst =
        (GeneratedJDTAST) operation.apply(generatedSourceCode, location)
            .getFiles()
            .get(0);
    testLocation(newJdtAst.inferLocations(4)
        .get(1), "return n;\n");


  }

  @Test
  public void testInferLocationAfterReplaceOperation() {
    final SourceFile testSourceFile = new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final List<GeneratedAST> asts =
        constructor.constructAST(Collections.singletonList(testSourceFile));
    final GeneratedJDTAST jdtAst = (GeneratedJDTAST) asts.get(0);
    final GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(asts);
    testLocation(jdtAst.inferLocations(10)
        .get(1), "return n;\n");

    // 置換位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTLocation location = new JDTLocation(testSourceFile, statement);


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
            .getFiles()
            .get(0);
    testLocation(newJdtAst.inferLocations(8)
        .get(1), "return n;\n");


  }
}
