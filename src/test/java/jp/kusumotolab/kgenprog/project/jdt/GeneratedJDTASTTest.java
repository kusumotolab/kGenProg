package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class GeneratedJDTASTTest {

  private static final String FILE_NAME = "A.java";
  private static final String SOURCE = new StringBuilder()
      // Line breaks must be included to execute GeneratedJDTAST#inferLocation.
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

  private GeneratedJDTAST<ProductSourcePath> ast;

  @Before
  public void setup() {
    final ProductSourcePath productSourcePath =
        new ProductSourcePath(Paths.get("."), Paths.get(FILE_NAME));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    this.ast = constructor.constructAST(productSourcePath, SOURCE, StandardCharsets.UTF_8);
  }

  @Test
  public void testInferASTNode01() {
    final ASTLocations astLocations = ast.createLocations();
    final List<ASTLocation> locations = astLocations.infer(3);
    final List<String> expects = new ArrayList<>();
    expects.add("int n = 0;");

    assertThat(locations).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(0)), atIndex(0));
  }

  @Test
  public void testInferASTNode02() {
    final ASTLocations astLocations = ast.createLocations();
    final List<ASTLocation> locations = astLocations.infer(5);
    final List<String> expects = new ArrayList<>();
    expects.add("System.out.println(n);");

    assertThat(locations).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(0)), atIndex(0));
  }

  @Test
  public void testInferASTNode03() {
    final ASTLocations astLocations = ast.createLocations();
    final List<ASTLocation> locations = astLocations.infer(1);

    assertThat(locations).hasSize(0);
  }

  @Test
  public void testInferASTNode04() {
    final ASTLocations astLocations = ast.createLocations();
    final List<ASTLocation> locations = astLocations.infer(9);
    final List<String> expects = new ArrayList<>();
    expects.add("return -n;");
    expects.add("n < 0");

    assertThat(locations).hasSize(2)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(0)), atIndex(0))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(1)), atIndex(1));
  }

  @Test
  public void testgetPrimaryClassName01() {
    final String source = "package a.b.c; class T1{} public class T2{}";
    final ProductSourcePath path =
        new ProductSourcePath(Paths.get("."), Paths.get("a/b/c/T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source,
        StandardCharsets.UTF_8);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.T2");
  }

  @Test
  public void testgetPrimaryClassName02() {
    final String source = "class T1{} public class T2{}";
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get("T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source,
        StandardCharsets.UTF_8);

    assertThat(ast.getPrimaryClassName()).isEqualTo("T2");
  }

  @Test
  public void testgetPrimaryClassName03() {
    final String source = "package a.b.c; class T1{} class T2{} class T3{}";
    final ProductSourcePath path =
        new ProductSourcePath(Paths.get("."), Paths.get("a/b/c/T2.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source,
        StandardCharsets.UTF_8);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.T1");
  }

  @Test
  public void testgetPrimaryClassName04() {
    final String source = "package a.b.c;";
    final ProductSourcePath path =
        new ProductSourcePath(Paths.get("."), Paths.get("a/b/c/package-info.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source,
        StandardCharsets.UTF_8);

    assertThat(ast.getPrimaryClassName()).isEqualTo("a.b.c.package-info");
  }

  @Test
  public void testStaticImport() {
    final String source = "import static java.lang.Math.max; class StaticImport{ }";
    final ProductSourcePath path =
        new ProductSourcePath(Paths.get("."), Paths.get("StaticImport.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(path, source,
        StandardCharsets.UTF_8);

    @SuppressWarnings("unchecked") final List<ImportDeclaration> imports = ast.getRoot()
        .imports();

    assertThat(imports).hasSize(1)
        .extracting(ImportDeclaration::isStatic)
        .containsOnly(true);
  }

  @Test
  public void testGetAllLocations() {
    final ASTLocations astLocations = ast.createLocations();
    final List<ASTLocation> locations = astLocations.getAll();
    final List<String> expects = new ArrayList<>();
    expects.add("int n = 0;");
    expects.add("System.out.println(n);");
    expects.add("n == 1");
    expects.add("return -n;");
    expects.add("n < 0");
    expects.add("return n;");

    assertThat(locations).hasSize(6)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(0)), atIndex(0))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(1)), atIndex(1))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(2)), atIndex(2))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(3)), atIndex(3))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(4)), atIndex(4))
        .satisfies(j -> assertThat(j).isSameSourceCodeAs(expects.get(5)), atIndex(5));
  }

  @Test
  public void testInferLocationAfterInsertOperation() {
    final String foo = "example/BuildSuccess01/src/example/Foo.java";
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get(foo));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode =
        constructor.constructAST(Collections.singletonList(path), Collections.emptyList());
    final List<GeneratedAST<ProductSourcePath>> asts = generatedSourceCode.getProductAsts();
    final GeneratedJDTAST<ProductSourcePath> jdtAst =
        (GeneratedJDTAST<ProductSourcePath>) asts.get(0);
    final ASTLocations astLocations = jdtAst.createLocations();

    assertThat(astLocations.infer(10)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(path, statement, ast);

    // 挿入対象生成
    final AST ast = jdtAst.getRoot()
        .getAST();
    final MethodInvocation methodInvocation = ast.newMethodInvocation();
    methodInvocation.setName(ast.newSimpleName("a"));
    final Statement insertStatement = ast.newExpressionStatement(methodInvocation);
    final InsertAfterOperation operation = new InsertAfterOperation(insertStatement);

    final GeneratedJDTAST<ProductSourcePath> newJdtAst =
        (GeneratedJDTAST<ProductSourcePath>) operation.apply(generatedSourceCode, location)
            .getProductAsts()
            .get(0);
    final ASTLocations newAstLocations = newJdtAst.createLocations();

    assertThat(newAstLocations.infer(10)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("a();"), atIndex(0));

    assertThat(newAstLocations.infer(11)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> (JDTASTLocation) loc)
        .extracting(loc -> loc.node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));
  }

  @Test
  public void testInferLocationAfterDeleteOperation() {
    final Path root = Paths.get("example/BuildSuccess01/");
    final ProductSourcePath path = new ProductSourcePath(root, Src.FOO);

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode =
        constructor.constructAST(Collections.singletonList(path), Collections.emptyList());
    final List<GeneratedAST<ProductSourcePath>> asts = generatedSourceCode.getProductAsts();
    final GeneratedJDTAST<ProductSourcePath> jdtAst =
        (GeneratedJDTAST<ProductSourcePath>) asts.get(0);
    final ASTLocations astLocations = jdtAst.createLocations();

    assertThat(astLocations.infer(10)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));

    // 削除位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(path, statement, ast);
    final DeleteOperation operation = new DeleteOperation();

    final GeneratedJDTAST<ProductSourcePath> newJdtAst =
        (GeneratedJDTAST<ProductSourcePath>) operation.apply(generatedSourceCode, location)
            .getProductAsts()
            .get(0);
    final ASTLocations newAstLocations = newJdtAst.createLocations();

    assertThat(newAstLocations.infer(4)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));
  }

  @Test
  public void testInferLocationAfterReplaceOperation() {
    final Path root = Paths.get("example/BuildSuccess01/");
    final ProductSourcePath path = new ProductSourcePath(root, Src.FOO);

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode =
        constructor.constructAST(Collections.singletonList(path), Collections.emptyList());
    final List<GeneratedAST<ProductSourcePath>> asts = generatedSourceCode.getProductAsts();
    final GeneratedJDTAST<ProductSourcePath> jdtAst =
        (GeneratedJDTAST<ProductSourcePath>) asts.get(0);
    final ASTLocations astLocations = jdtAst.createLocations();

    assertThat(astLocations.infer(10)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));

    // 置換位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) jdtAst.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(path, statement, ast);

    // 置換対象の生成
    final AST ast = jdtAst.getRoot()
        .getAST();
    final MethodInvocation methodInvocationA = ast.newMethodInvocation();
    methodInvocationA.setName(ast.newSimpleName("a"));
    final MethodInvocation methodInvocationB = ast.newMethodInvocation();
    methodInvocationB.setName(ast.newSimpleName("b"));
    final Block block = ast.newBlock();

    @SuppressWarnings("unchecked") final List<Statement> blockStatementList = block.statements();

    blockStatementList.add(ast.newExpressionStatement(methodInvocationA));
    blockStatementList.add(ast.newExpressionStatement(methodInvocationB));

    final ReplaceOperation operation = new ReplaceOperation(block);

    final GeneratedJDTAST<ProductSourcePath> newJdtAst =
        (GeneratedJDTAST<ProductSourcePath>) operation.apply(generatedSourceCode, location)
            .getProductAsts()
            .get(0);
    final ASTLocations newAstLocations = newJdtAst.createLocations();

    assertThat(newAstLocations.infer(8)).hasSize(1)
        .allMatch(loc -> loc instanceof JDTASTLocation)
        .extracting(loc -> ((JDTASTLocation) loc).node)
        .satisfies(j -> assertThat(j).isSameSourceCodeAs("return n;"), atIndex(0));
  }

  @Test
  public void testGetMessageDigest01() {
    final Path root = Paths.get("example/BuildSuccess01/");
    final ProductSourcePath path = new ProductSourcePath(root, Src.FOO);

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode =
        constructor.constructAST(Collections.singletonList(path), Collections.emptyList());
    final List<GeneratedAST<ProductSourcePath>> asts = generatedSourceCode.getProductAsts();
    final GeneratedJDTAST<ProductSourcePath> jdtAst =
        (GeneratedJDTAST<ProductSourcePath>) asts.get(0);

    assertThat(jdtAst.getMessageDigest()).isEqualTo("203afcb54181c234b8450eb5e02efdf8");
  }

  @Test
  public void testGetMessageDigest02() {
    final String source1 = "class A { public void a() { b(1); } public void b(int v){}}";
    final String source2 = "class A { public void a() { b(1); } public void b(int v){}}\n\n";
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast1 = constructor.constructAST(path, source1,
        StandardCharsets.UTF_8);
    final GeneratedJDTAST<ProductSourcePath> ast2 = constructor.constructAST(path, source2,
        StandardCharsets.UTF_8);

    assertThat(ast1.getMessageDigest()).isEqualTo(ast2.getMessageDigest());
  }

  @Test
  public void testGetMessageDigest03() {
    final String source1 = "class A { public void a() { b(1); } public void b(int v){}}";
    final String source2 = "class A { public void a() { b(2); } public void b(int v){}}";
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast1 = constructor.constructAST(path, source1,
        StandardCharsets.UTF_8);
    final GeneratedJDTAST<ProductSourcePath> ast2 = constructor.constructAST(path, source2,
        StandardCharsets.UTF_8);

    assertThat(ast1.getMessageDigest()).isNotEqualTo(ast2.getMessageDigest());
  }

  @Test
  public void testGetNumberOfLines() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);

    final GeneratedAST<ProductSourcePath> productAst = generatedSourceCode.getProductAsts()
        .get(0);
    assertThat(productAst.getNumberOfLines()).isEqualTo(12);

    final GeneratedAST<TestSourcePath> testAst = generatedSourceCode.getTestAsts()
        .get(0);
    assertThat(testAst.getNumberOfLines()).isEqualTo(17);
  }
}
