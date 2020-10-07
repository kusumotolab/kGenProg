package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
  public void testLocateForTheSameAst01() {
    final String source = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = location.locate(ast.getRoot());

    // of course, target node and located node are the same
    ASTNodeAssert.assertThat(node)
        .isSameSourceCodeAs("i=1;");
    ASTNodeAssert.assertThat(node.getRoot())
        .isSameRootClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForTheSameAst02() {
    final String source = "class A {"
        + "  public void a(int i) {"
        + "    i = 1;" // target
        + "    i = 1;"
        + "    i = 1;"
        + "  }"
        + "}";
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast, 0);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = location.locate(ast.getRoot());

    // of course, target node and located node are the same instance
    ASTNodeAssert.assertThat(node)
        .isSameAs(location.getNode());
    ASTNodeAssert.assertThat(node.getRoot())
        .isSameRootClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForTheSameAst03() {
    final String source = "class A {"
        + "  public void a(int i) {"
        + "    i = 1;"
        + "    i = 1;" // target
        + "    i = 1;"
        + "  }"
        + "}";
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = location.locate(ast.getRoot());

    // there are the same nodes as target node, so located node is not target node but first one
    ASTNodeAssert.assertThat(node)
        .isNotSameAs(location.getNode());
    ASTNodeAssert.assertThat(node)
        .isSameAs(getLocation(ast, 0).getNode());
    ASTNodeAssert.assertThat(node.getRoot())
        .isSameRootClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForTheSameAst04() {
    final String source = "class A {"
        + "  public void a(int i) {"
        + "    if (true) {"
        + "      i = 1;" // target
        + "      if (true) {"
        + "        i = 1;" // target
        + "      } else {"
        + "        i = 1;" // target
        + "      }"
        + "    } else {"
        + "      i = 1;" // target
        + "      if (true) {"
        + "        i = 1;" // target
        + "      } else {"
        + "        i = 1;" // target
        + "      }"
        + "    }"
        + "}";
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    final List<Integer> ids = Arrays.asList(1, 2, 3, 6, 7, 8); // These nodes have i=1

    for (int i : ids) {
      // extract target location
      final JDTASTLocation location = getLocation(ast, i);
      ASTNodeAssert.assertThat(location.getNode())
          .isSameSourceCodeAs("i=1;");

      // try locate() for the same ast root
      final ASTNode node = location.locate(ast.getRoot());

      // located node is the same instance as target node
      ASTNodeAssert.assertThat(node)
          .isSameAs(location.getNode());
      ASTNodeAssert.assertThat(node.getRoot())
          .isSameRootClassAs(ast.getRoot());
    }
  }

  @Test
  public void testLocateForSameContentAst() {
    final String source = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";

    // generate two asts (contents are the same but they are different instances)
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    ASTNodeAssert.assertThat(node)
        .isSameSourceCodeAs("i=1;");
    ASTNodeAssert.assertThat(node)
        .isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst01() {
    final String source1 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";
    final String source2 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 2;"
        + "    i = 1;" // target
        + "  }"
        + "}";

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    ASTNodeAssert.assertThat(node)
        .isSameSourceCodeAs("i=1;");
    ASTNodeAssert.assertThat(node)
        .isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst02() {
    final String source1 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";
    final String source2 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    if (true) {"
        + "      i = 2;"
        + "    }"
        + "  }"
        + "}";

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // located node is the same as target node
    ASTNodeAssert.assertThat(node)
        .isSameSourceCodeAs("i=1;");
    ASTNodeAssert.assertThat(node)
        .isSameRootClassAs(ast2.getRoot());
  }

  @Test
  public void testLocateForDifferentAst03() {
    final String source1 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";
    final String source2 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    if (true) {"
        + "      i = 1;" // target
        + "    }"
        + "    i = 2;"
        + "  }"
        + "}";

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // failed to locate
    ASTNodeAssert.assertThat(node)
        .isNull();
  }

  @Test
  public void testLocateForDifferentAst04() {
    final String source1 = "class A {"
        + "  public void a(int i) {"
        + "    i = 0;"
        + "    i = 1;" // target
        + "    i = 2;"
        + "  }"
        + "}";
    final String source2 = "class A {"
        + "  public void a(int i) {"
        + "  }"
        + "}";

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    ASTNodeAssert.assertThat(location.getNode())
        .isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = location.locate(ast2.getRoot());

    // failed to locate
    ASTNodeAssert.assertThat(node)
        .isNull();
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

}
