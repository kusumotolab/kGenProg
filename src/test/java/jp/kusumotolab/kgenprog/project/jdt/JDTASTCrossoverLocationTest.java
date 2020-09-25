package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class JDTASTCrossoverLocationTest {

  @Test
  public void testLocateForTheSameAst01() {
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
    final JDTASTLocation location = getLocation(ast, 1);
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = cLocation.locate(ast.getRoot());

    // of course, target node and located node are the same
    assertThat(node).isSameSourceCodeAs("i=1;");
    assertThat(node.getRoot()).isSameRootClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForTheSameAst02() {
    final String source = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 1;") // target
        .append("    i = 1;")
        .append("    i = 1;")
        .append("  }")
        .append("}")
        .toString();
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast, 0);
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = cLocation.locate(ast.getRoot());

    // of course, target node and located node are the same
    assertThat(node).isEqualTo(location.getNode());
    assertThat(node.getRoot()).isSameRootClassAs(ast.getRoot());
  }

  @Test
  public void testLocateForTheSameAst03() {
    final String source = new StringBuilder()
        .append("class A {")
        .append("  public void a(int i) {")
        .append("    i = 1;")
        .append("    i = 1;") // target
        .append("    i = 1;")
        .append("  }")
        .append("}")
        .toString();
    final GeneratedJDTAST<ProductSourcePath> ast = createAst(source);

    // extract target location
    final JDTASTLocation location = getLocation(ast, 1);
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try locate() for the same ast root
    final ASTNode node = cLocation.locate(ast.getRoot());

    // there are the same nodes as target node, so located node is the first one
    assertThat(node).isNotEqualTo(location.getNode());
    assertThat(node.getRoot()).isSameRootClassAs(ast.getRoot());
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
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = cLocation.locate(ast2.getRoot());

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
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = cLocation.locate(ast2.getRoot());

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
        .append("    i = 1;") // target
        .append("    if (true) {")
        .append("      i = 2;")
        .append("    }")
        .append("  }")
        .append("}")
        .toString();

    // generate two asts
    final GeneratedJDTAST<ProductSourcePath> ast1 = createAst(source1);
    final GeneratedJDTAST<ProductSourcePath> ast2 = createAst(source2);

    // extract target location
    final JDTASTLocation location = getLocation(ast1, 1);
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = cLocation.locate(ast2.getRoot());

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
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = cLocation.locate(ast2.getRoot());

    // failed to locate
    assertThat(node).isNull();
  }

  @Test
  public void testLocateForDifferentAst04() {
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
    final JDTASTCrossoverLocation cLocation = new JDTASTCrossoverLocation(location);
    assertThat(cLocation.getNode()).isSameSourceCodeAs("i=1;");

    // try loc1.locate()
    final ASTNode node = cLocation.locate(ast2.getRoot());

    // failed to locate
    assertThat(node).isNull();
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
