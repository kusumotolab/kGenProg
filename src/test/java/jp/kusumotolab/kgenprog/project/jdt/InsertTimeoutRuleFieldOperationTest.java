package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

/**
 *
 * @deprecated #507 の修正により不要になったため，本クラスは廃止 & Ignore
 */
@Ignore
public class InsertTimeoutRuleFieldOperationTest {

  @Test
  public void testInsert() {
    final String source = new StringBuilder().append("")
        .append("class A {")
        .append("  public void a() {")
        .append("  }")
        .append("}")
        .toString();

    final TestSourcePath sourcePath = new TestSourcePath(Paths.get("."), Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<TestSourcePath> ast = constructor.constructAST(sourcePath, source);
    final GeneratedSourceCode sourceCode =
        new GeneratedSourceCode(Collections.emptyList(), Collections.singletonList(ast));
    final InsertTimeoutRuleFieldOperation operation = new InsertTimeoutRuleFieldOperation(10);
    final GeneratedSourceCode applyiedSourceCode = operation.apply(sourceCode, null);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  @org.junit.Rule")
        .append(
            "  public final org.junit.rules.Timeout globalTimeout = org.junit.rules.Timeout.seconds(10);")
        .append("  public void a() {")
        .append("  }")
        .append("}")
        .toString();

    final GeneratedJDTAST<TestSourcePath> newAST =
        (GeneratedJDTAST<TestSourcePath>) applyiedSourceCode.getTestAsts()
            .get(0);

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);
  }

  @Test
  public void testInsertDuplicateName() {
    final String source = new StringBuilder().append("")
        .append("class A {")
        .append("  private int globalTimeout;")
        .append("  public void a() {")
        .append("  }")
        .append("}")
        .toString();

    final TestSourcePath sourcePath = new TestSourcePath(Paths.get("."), Paths.get("A.java"));
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<TestSourcePath> ast = constructor.constructAST(sourcePath, source);
    final GeneratedSourceCode sourceCode =
        new GeneratedSourceCode(Collections.emptyList(), Collections.singletonList(ast));
    final InsertTimeoutRuleFieldOperation operation = new InsertTimeoutRuleFieldOperation(10);
    final GeneratedSourceCode applyiedSourceCode = operation.apply(sourceCode, null);

    final String expected = new StringBuilder().append("")
        .append("class A {")
        .append("  @org.junit.Rule")
        .append(
            "  public final org.junit.rules.Timeout _globalTimeout = org.junit.rules.Timeout.seconds(10);")
        .append("  private int globalTimeout;")
        .append("  public void a() {")
        .append("  }")
        .append("}")
        .toString();

    final GeneratedJDTAST<TestSourcePath> newAST =
        (GeneratedJDTAST<TestSourcePath>) applyiedSourceCode.getTestAsts()
            .get(0);

    assertThat(newAST.getRoot()).isSameSourceCodeAs(expected);
  }

}
