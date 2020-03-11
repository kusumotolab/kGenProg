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
import org.eclipse.jdt.core.dom.WhileStatement;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class ASTAnalyzerTest {

  @Test
  public void testIsLastStatement() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "CloseToZero03", "src", "example", "CloseToZero.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);

    final MethodDeclaration methodDeclaration = typeDeclaration.getMethods()[1]; // int reuse_me1(int n)
    final Block body = methodDeclaration.getBody();
    final List statements = body.statements();

    // "if (n == 0) { return n; }"
    assertThat(astAnalyzer.isLastStatement(((Statement) statements.get(0)))).isFalse();
    // ""return 0;"
    assertThat(astAnalyzer.isLastStatement(((Statement) statements.get(1)))).isTrue();

    // "return n;"
    final IfStatement ifStatement = (IfStatement) statements.get(0);
    final Statement statement = (Statement) ((Block) ifStatement.getThenStatement()).statements()
        .get(0);
    assertThat(astAnalyzer.isLastStatement(statement)).isFalse();
  }

  @Test
  public void testIsVoidMethod() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "CloseToZero03", "src", "example", "CloseToZero.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);

    // public int close_to_zero(int n)
    {
      final MethodDeclaration method = typeDeclaration.getMethods()[0];
      final Statement statement = (Statement) method.getBody()
          .statements()
          .get(0);
      final boolean isVoidMethod = astAnalyzer.isVoidMethod(statement);
      assertThat(isVoidMethod).isFalse();
    }

    // public void reuse_me2(int n)
    {
      final MethodDeclaration method = typeDeclaration.getMethods()[2];
      final Statement statement = (Statement) method.getBody()
          .statements()
          .get(0);
      final boolean isVoidMethod = astAnalyzer.isVoidMethod(statement);
      assertThat(isVoidMethod).isTrue();
    }
  }

  @Test
  public void testGetReturnType() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "CloseToZero03", "src", "example", "CloseToZero.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);

    // public int close_to_zero(int n)
    {
      final MethodDeclaration method = typeDeclaration.getMethods()[0];
      final Statement statement = (Statement) method.getBody()
          .statements()
          .get(0);
      final FullyQualifiedName type = astAnalyzer.getReturnType(statement);
      assertThat(type).isEqualTo("int");
    }

    // public void reuse_me2(int n)
    {
      final MethodDeclaration method = typeDeclaration.getMethods()[2];
      final Statement statement = (Statement) method.getBody()
          .statements()
          .get(0);
      final FullyQualifiedName type = astAnalyzer.getReturnType(statement);
      assertThat(type).isEqualTo("void");
    }
  }

  @Test
  public void testCanInsertAfter() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "CloseToZero03", "src", "example", "CloseToZero.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[0];
    final List statements = method.getBody()
        .statements();

    /*
     *  if (n == 0) {
     *    return n;
     *  }
     *  //  <- ここが実行される可能性があるかどうか
     */
    {
      final Statement statement = (Statement) statements.get(0);
      final boolean canInsertAfter = astAnalyzer.canInsertAfter(statement);
      assertThat(canInsertAfter).isTrue();
    }


    /*
     *  if (n == 0) {
     *    return n;
     *    //  <- ここが実行される可能性があるかどうか
     *  }
     */
    {
      final Statement statement = (Statement) statements.get(1);
      final boolean canInsertAfter = astAnalyzer.canInsertAfter(statement);
      assertThat(canInsertAfter).isFalse();
    }
  }


  @Test
  public void testCanInsertAfterForIfStatement() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "GCD01", "src", "example", "GreatestCommonDivider.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[1];
    final List statements = method.getBody()
        .statements();

    /*
     *  if (a > b) {
     *    return a;
     *  } else if (a < b) {
     *    return b;
     *  } else {
     *    return 0;
     *  }
     *    <- ここにStatementを挿入できるかどうか(できない)
     */
    final Statement statement = (Statement) statements.get(0);
    final boolean canInsertAfter = astAnalyzer.canInsertAfter(statement);
    assertThat(canInsertAfter).isFalse();
  }

  @Test
  public void testCanInsertBreak() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "QuickSort01", "src", "example", "QuickSort.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[1];
    final List statements = method.getBody()
        .statements();

    // if (j + 1 < right) quicksort(value, j + 1, right);
    {
      final Statement statement = (Statement) statements.get(5);
      final boolean canBreak = astAnalyzer.canInsertBreak(statement);
      assertThat(canBreak).isFalse();
    }

    final WhileStatement whileStatement = (WhileStatement) statements.get(3);
    final Block block = (Block) whileStatement.getBody();
    final List statementsInWhile = block.statements();

    // j--;
    {
      final Statement lastStatement = (Statement) statementsInWhile.get(5);
      assertThat(astAnalyzer.canInsertBreak(lastStatement)).isTrue();
    }

    // i++;
    {
      final Statement statement = (Statement) statementsInWhile.get(4);
      assertThat(astAnalyzer.canInsertBreak(statement)).isFalse();
    }
  }

  @Test
  public void testIsInLoopOrSWitch() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "GCD01", "src", "example", "GreatestCommonDivider.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[0];
    final List statements = method.getBody()
        .statements();

    // if (a == 0) { return 0; } <- ここがルーブの中かどうか
    {
      final Statement statement = (Statement) statements.get(0);
      final boolean inLoopOrSWitch = astAnalyzer.isInLoopOrSWitch(statement);
      assertThat(inLoopOrSWitch).isFalse();
    }

    /*
     * while(b != 0) {
     *   if (a > b) { *** } <- ここがルーブの中かとどうか
     *   else { *** }
     * }
     */
    {
      final WhileStatement whileStatement = (WhileStatement) statements.get(1);
      final Block body = (Block) whileStatement.getBody();
      final List statementsInLoop = body.statements();
      final boolean inLoop = astAnalyzer.isInLoopOrSWitch(((Statement) statementsInLoop.get(0)));
      assertThat(inLoop).isTrue();
    }
  }

  @Test
  public void testCanInsertContinue() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "GCD01", "src", "example", "GreatestCommonDivider.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[0];
    final List statements = method.getBody()
        .statements();

    /*
     *  public int gcd(int a, int b) {
     *    if (a == 0) { *** }
     *    //  <- ここにcontinueを挿入できるか
     *    return a;
     *  }
     */
    {
      final Statement statement = (Statement) statements.get(0);
      final boolean canContinue = astAnalyzer.canInsertContinue(statement);
      assertThat(canContinue).isFalse();
    }

    /*
     *  public int gcd(int a, int b) {
     *    if (a == 0) { *** }
     *    while (b != 0) {
     *      if (a > b) { *** }
     *      else { *** }
     *      //  <- ここにcontinueを挿入できるか
     *    }
     *    return a;
     *  }
     */
    {
      final WhileStatement whileStatement = (WhileStatement) statements.get(1);
      final Block body = (Block) whileStatement.getBody();
      final List statementsInLoop = body.statements();
      final boolean canContinue = astAnalyzer.canInsertContinue(
          ((Statement) statementsInLoop.get(0)));
      assertThat(canContinue).isTrue();
    }
  }

  @Test
  public void testIsLastStatementInParent() {
    final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
    final Path path = Paths.get("example", "GCD01", "src", "example", "GreatestCommonDivider.java");
    final TypeDeclaration typeDeclaration = creatTypeDeclaration(path);
    final MethodDeclaration method = typeDeclaration.getMethods()[0];
    final List statements = method.getBody()
        .statements();

    {
      final Statement statement = (Statement) statements.get(1);
      final boolean isLastStatement = astAnalyzer.isLastStatementInParent(statement);
      assertThat(isLastStatement).isFalse();
    }

    {
      final Statement statement = (Statement) statements.get(statements.size() - 1);
      final boolean isLastStatement = astAnalyzer.isLastStatementInParent(statement);
      assertThat(isLastStatement).isTrue();
    }
  }

  private TypeDeclaration creatTypeDeclaration(final Path path) {
    final HeuristicProjectFactory projectFactory = new HeuristicProjectFactory(path);
    final TargetProject targetProject = projectFactory.create();
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(targetProject);
    final GeneratedJDTAST<ProductSourcePath> jdtast = (GeneratedJDTAST<ProductSourcePath>) sourceCode.getProductAsts()
        .get(0);
    return (TypeDeclaration) jdtast.getRoot()
        .types()
        .get(0);
  }
}