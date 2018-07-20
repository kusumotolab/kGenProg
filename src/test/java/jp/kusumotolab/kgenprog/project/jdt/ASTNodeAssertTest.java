package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.util.Map;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.junit.Test;

public class ASTNodeAssertTest {

  private static final String SOURCE = new StringBuilder().append("")
      .append("class A {")
      .append("  public void a() {")
      .append("    int n = 0;")
      .append("    if (n == 1) {")
      .append("      System.out.println(n);")
      .append("    }")
      .append("  }")
      .append("}")
      .toString();

  private static final CompilationUnit COMPILATION_UNIT = createCompilationUnit(SOURCE);

  @Test
  public void testAssertForCompilationUnit() {
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(COMPILATION_UNIT);
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(SOURCE);
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(SOURCE + "  ");
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(SOURCE + "\n");
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs("\n" + SOURCE + "\n");
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs("\n  " + SOURCE + "  \n");
  }

  @Test
  public void testAssertForStatement() {
    // @formatter:off
    final TypeDeclaration typeDecralation = (TypeDeclaration) COMPILATION_UNIT.types().get(0);
    final MethodDeclaration methodDecralation = (MethodDeclaration) typeDecralation.bodyDeclarations().get(0);
    final Block block = (Block) methodDecralation.getBody();
    final Statement statement = (Statement) block.statements().get(0); // "int n = 0;"
    // @formatter:on

    assertThat(statement).isSameSourceCodeAs(statement);
    assertThat(statement).isSameSourceCodeAs(statement.toString());
    assertThat(statement).isSameSourceCodeAs("int n=0;");
    assertThat(statement).isSameSourceCodeAs("int n = 0;");
    assertThat(statement).isSameSourceCodeAs("int n = 0;  ");
    assertThat(statement).isSameSourceCodeAs("int n = 0;\n");
    assertThat(statement).isSameSourceCodeAs("int n = 0;\n\n");
    assertThat(statement).isSameSourceCodeAs("int n = 0;\n\n\r\n");
    assertThat(statement).isSameSourceCodeAs("int   n   =   0  ;");
    assertThat(statement).isSameSourceCodeAs("  int n = 0;");
    assertThat(statement).isSameSourceCodeAs("  int n = 0;  ");
    assertThat(statement).isSameSourceCodeAs("\n  int n = 0;");;
    assertThat(statement).isSameSourceCodeAs("\nint\nn\n=\n0\n;\n");
  }

  @Test
  public void testAssertForMethodInvocation() {
    // @formatter:off
    final TypeDeclaration typeDecralation = (TypeDeclaration) COMPILATION_UNIT.types().get(0);
    final MethodDeclaration methodDecralation = (MethodDeclaration) typeDecralation.bodyDeclarations().get(0);
    final Block block = (Block) methodDecralation.getBody();
    final IfStatement ifStatement = (IfStatement) block.statements().get(1);
    final Block block2 = (Block) ifStatement.getThenStatement();
    final Statement statement = (Statement) block2.statements().get(0); // "System.out.println(n);"
    // @formatter:on

    assertThat(statement).isSameSourceCodeAs(statement);
    assertThat(statement).isSameSourceCodeAs(statement.toString());
    assertThat(statement).isSameSourceCodeAs("System.out.println(n);");
    assertThat(statement).isSameSourceCodeAs("System.out.println(  n);");
    assertThat(statement).isSameSourceCodeAs("System.out.println(n  );");
    assertThat(statement).isSameSourceCodeAs("System.out.println(  n  );");
    assertThat(statement).isSameSourceCodeAs("System.out.println(  n  )  ;");
  }

  @Test
  public void testAssertForExpression() {
    // @formatter:off
    final TypeDeclaration typeDecralation = (TypeDeclaration) COMPILATION_UNIT.types().get(0);
    final MethodDeclaration methodDecralation = (MethodDeclaration) typeDecralation.bodyDeclarations().get(0);
    final Block block = (Block) methodDecralation.getBody();
    final IfStatement ifStatement = (IfStatement) block.statements().get(1);
    final Expression expression = (Expression) ifStatement.getExpression(); // "n == 1"
    // @formatter:on

    assertThat(expression).isSameSourceCodeAs(expression);
    assertThat(expression).isSameSourceCodeAs(expression.toString());
    assertThat(expression).isSameSourceCodeAs("n==1");
    assertThat(expression).isSameSourceCodeAs("n == 1");
    assertThat(expression).isSameSourceCodeAs("n  ==1");
    assertThat(expression).isSameSourceCodeAs("n==  1");
    assertThat(expression).isSameSourceCodeAs("  n==1");
    assertThat(expression).isSameSourceCodeAs("  n==1  ");
  }

  @Test
  public void testAssertForIfStatement() {
    // @formatter:off
    final TypeDeclaration typeDecralation = (TypeDeclaration) COMPILATION_UNIT.types().get(0);
    final MethodDeclaration methodDecralation = (MethodDeclaration) typeDecralation.bodyDeclarations().get(0);
    final Block block = (Block) methodDecralation.getBody();
    final IfStatement ifStatement = (IfStatement) block.statements().get(1); // "if (n == 1) {...}"
    // @formatter:on

    assertThat(ifStatement).isSameSourceCodeAs(ifStatement);
    assertThat(ifStatement).isSameSourceCodeAs(ifStatement.toString());
    assertThat(ifStatement).isSameSourceCodeAs("if(n==1){System.out.println(n);}");
    assertThat(ifStatement).isSameSourceCodeAs("if (n == 1) { System.out.println(n); }");
    assertThat(ifStatement).isSameSourceCodeAs("if (n == 1) {\n  System.out.println(n);\n}");
  }

  private static CompilationUnit createCompilationUnit(final String source) {
    final ASTParser parser = ASTParser.newParser(AST.JLS10);
    parser.setSource(source.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    @SuppressWarnings("unchecked")
    final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    parser.setCompilerOptions(options);

    return (CompilationUnit) parser.createAST(null);
  }
}
