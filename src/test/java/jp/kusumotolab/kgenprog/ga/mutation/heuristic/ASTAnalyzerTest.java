package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class ASTAnalyzerTest {

  @Test
  public void testIsEndStatement() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "CloseToZero03", "src", "example", "CloseToZero.java");
    final GeneratedJDTAST<ProductSourcePath> ast = creatAST(path);
    final TypeDeclaration typeDeclaration = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration methodDeclaration = typeDeclaration.getMethods()[1]; // int reuse_me1(int n)
    final Block body = methodDeclaration.getBody();
    final List statements = body.statements();

    // "if (n == 0) { return n; }"
    assertThat(astAnalyzer.isEndStatement(((Statement) statements.get(0)))).isFalse();
    // ""return 0;"
    assertThat(astAnalyzer.isEndStatement(((Statement) statements.get(1)))).isTrue();

    // "return n;"
    final IfStatement ifStatement = (IfStatement) statements.get(0);
    final Statement statement = (Statement) ((Block) ifStatement.getThenStatement()).statements()
        .get(0);
    assertThat(astAnalyzer.isEndStatement(statement)).isTrue();
  }

  @Test
  public void testIsVoidMethod() {
  }

  @Test
  public void testGetReturnType() {
  }

  @Test
  public void testCanInsertAfter() {
  }

  @Test
  public void testCanBreak() {
  }

  @Test
  public void testIsInLoopOrSWitch() {
  }

  @Test
  public void testCanContinue() {
  }

  @Test
  public void testIsLastStatement() {
  }

  private GeneratedJDTAST<ProductSourcePath> creatAST(final Path path) {
    final HeuristicProjectFactory projectFactory = new HeuristicProjectFactory(path);
    final TargetProject targetProject = projectFactory.create();
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(targetProject);
    return (GeneratedJDTAST<ProductSourcePath>) sourceCode.getProductAsts()
        .get(0);
  }
}