package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.util.Map;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
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
      .append("  public int b(int n) {")
      .append("    if (n < 0) { return -n; }")
      .append("    return n;")
      .append("  }")
      .append("}")
      .toString();

  private static final CompilationUnit COMPILATION_UNIT = createAstNode(SOURCE);

  @Test
  public void test() {
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(SOURCE);
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs(SOURCE + "\n");
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs("\n" + SOURCE + "\n\n");
    assertThat(COMPILATION_UNIT).isSameSourceCodeAs("\n   " + SOURCE + "   \n\n");
  }


  private static CompilationUnit createAstNode(final String source) {
    final ASTParser parser = ASTParser.newParser(AST.JLS10);
    parser.setSource(source.toCharArray());

    @SuppressWarnings("unchecked")
    final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    parser.setCompilerOptions(options);

    return (CompilationUnit) parser.createAST(null);
  }
}
