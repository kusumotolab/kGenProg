package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class JDTLocationTest {

  @Test
  public void testLocateForTheSameAst() {
    final String source = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 1;") // target
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation locate = getLocation(ast, 1);
    assertThat(locate.getNode()).isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = locate.locate(ast.getRoot());

    // of course, target node and located node are the same
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node.getRoot()).hasSameClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForSameContentAst() {
    final String source = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 1;") // target
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();

    // generate two asts (contents are the same but they are different object)
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    assertThat(location.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node).isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst01() {
    final String source1 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 1;") // target
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();
    final String source2 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 2;")
        .append("    i = 1;") // target
        .append("  }")
        .append("}")
        .toString();

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    assertThat(location.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node).isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst02() {
    final String source1 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 1;") // target
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();
    final String source2 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    if (true) {")
        .append("      i = 1;") // target
        .append("    }")
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    assertThat(location.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node).isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst03() {
    final String source1 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 0;")
        .append("    i = 1;") // target
        .append("    i = 2;")
        .append("  }")
        .append("}")
        .toString();
    final String source2 = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("  }")
        .append("}")
        .toString();

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    assertThat(location.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node).isSameRootClassAs(ast2.getRoot());
  }

  private GeneratedJDTAST<ProductSourcePath> createAst(final String source) {
    final String fname = source.hashCode() + ".java"; // dummy file name
    final ProductSourcePath path = new ProductSourcePath(Paths.get("."), Paths.get(fname));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    return constructor.constructAST(path, source);
  }

  private JDTASTLocation getLocation(GeneratedJDTAST<ProductSourcePath> ast, int idx) {
    final ASTLocations locs = ast.createLocations();
    return (JDTASTLocation) locs.getAll()
        .get(idx);
  }

  @Test
  public void testInferLineNumbers() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);

    final ProductSourcePath productSourcePath = new ProductSourcePath(rootPath, Src.FOO);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) generatedSourceCode.getProductAst(productSourcePath);

    final CompilationUnit root = ast.getRoot();
    final TypeDeclaration type = (TypeDeclaration) root.types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];

    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(0);
    final ASTLocation location1 = new JDTASTLocation(null, statement1, ast);

    assertThat(location1.inferLineNumbers()).isEqualTo(new LineNumberRange(4, 9));

    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(1);
    final ASTLocation location2 = new JDTASTLocation(null, statement2, ast);

    assertThat(location2.inferLineNumbers()).isEqualTo(new LineNumberRange(10, 10));
  }

}
