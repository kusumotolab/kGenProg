package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.util.Map;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.junit.Before;
import org.junit.Test;

public class ASTNodeAssertTest {

  // テスト対象のソースコード本文
  private final String source = new StringBuilder().append("")
      .append("class A {")
      .append("  public void a() {")
      .append("    int n = 0;")
      .append("    if (n == 1) {")
      .append("      System.out.println(n);")
      .append("    }")
      .append("  }")
      .append("}")
      .toString();

  // テストで使われるactual要素
  private ASTNode compilationUnit;
  private ASTNode ifStatement;
  private ASTNode expression;
  private ASTNode statement1;
  private ASTNode statement2;

  @Before
  public void before() {
    // ASTのトラバーサルしてテストしたい要素を抜き出す

    // @formatter:off
    final CompilationUnit compilationUnit = createCompilationUnit(source);
    final TypeDeclaration typeDecralation = (TypeDeclaration) compilationUnit.types().get(0);
    final MethodDeclaration methodDecralation = (MethodDeclaration) typeDecralation.bodyDeclarations().get(0);
    final Block block = (Block) methodDecralation.getBody();
    final Statement statement1 = (Statement) block.statements().get(0);       // "int n = 0;"
    final IfStatement ifStatement = (IfStatement) block.statements().get(1);  // "if (n == 1) {...}"
    final Expression expression = (Expression) ifStatement.getExpression();   // "n == 1"
    final Block block2 = (Block) ifStatement.getThenStatement();
    final Statement statement2 = (Statement) block2.statements().get(0);      // "System.out.println(n);"
    // @formatter:on

    // 抜き出した要素をセット
    this.compilationUnit = compilationUnit;
    this.ifStatement = ifStatement;
    this.statement1 = statement1;
    this.statement2 = statement2;
    this.expression = expression;
  }

  @Test
  public void testAssertForCompilationUnit() {
    assertThat(compilationUnit).isSameSourceCodeAs(compilationUnit);
    assertThat(compilationUnit).isSameSourceCodeAs(source);
    assertThat(compilationUnit).isSameSourceCodeAs(source + "  ");
    assertThat(compilationUnit).isSameSourceCodeAs(source + "\n");
    assertThat(compilationUnit).isSameSourceCodeAs("\n" + source + "\n");
    assertThat(compilationUnit).isSameSourceCodeAs("\n  " + source + "  \n");
  }

  @Test
  public void testAssertForStatement() {
    assertThat(statement1).isSameSourceCodeAs((Statement) statement1);
    assertThat((Statement) statement1).isSameSourceCodeAs(statement1);
    assertThat(statement1).isSameSourceCodeAs(statement1);
    assertThat(statement1).isSameSourceCodeAs(statement1.toString());
    assertThat(statement1).isSameSourceCodeAs("int n=0;");
    assertThat(statement1).isSameSourceCodeAs("int n = 0;");
    assertThat(statement1).isSameSourceCodeAs("int n = 0;  ");
    assertThat(statement1).isSameSourceCodeAs("int n = 0;\n");
    assertThat(statement1).isSameSourceCodeAs("int n = 0;\n\n");
    assertThat(statement1).isSameSourceCodeAs("int n = 0;\n\n\r\n");
    assertThat(statement1).isSameSourceCodeAs("int   n   =   0  ;");
    assertThat(statement1).isSameSourceCodeAs("  int n = 0;");
    assertThat(statement1).isSameSourceCodeAs("  int n = 0;  ");
    assertThat(statement1).isSameSourceCodeAs("\n  int n = 0;");;
    assertThat(statement1).isSameSourceCodeAs("\nint\nn\n=\n0\n;\n");
  }

  @Test
  public void testAssertForMethodInvocation() {
    assertThat(statement2).isSameSourceCodeAs(statement2);
    assertThat(statement2).isSameSourceCodeAs(statement2.toString());
    assertThat(statement2).isSameSourceCodeAs("System.out.println(n);");
    assertThat(statement2).isSameSourceCodeAs("System.out.println(  n);");
    assertThat(statement2).isSameSourceCodeAs("System.out.println(n  );");
    assertThat(statement2).isSameSourceCodeAs("System.out.println(  n  );");
    assertThat(statement2).isSameSourceCodeAs("System.out.println(  n  )  ;");
    assertThat(statement2).isSameSourceCodeAs("  System.out.println(  n  )  ;");
    assertThat(statement2).isSameSourceCodeAs("System\n.out\n.println\n(\nn\n)\n;");
  }

  @Test
  public void testAssertForExpression() {
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
