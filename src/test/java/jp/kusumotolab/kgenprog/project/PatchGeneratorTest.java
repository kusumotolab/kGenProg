package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.output.FileDiff;
import jp.kusumotolab.kgenprog.output.PatchGenerator;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class PatchGeneratorTest {

  @Test
  public void testPatchGenerator1() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final String expected = new StringBuilder().append("")
        .append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    return n;\n" + "  }\n")
        .append("}")
        .toString();

    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 削除位置の Location 作成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement, ast);

    final DeleteOperation operation = new DeleteOperation();
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final Variant modifiedVariant = new Variant(0, 0,
        new Gene(Arrays.asList(new Base(location, operation))), code, null, null, null, null);

    final FileDiff fileDiff = patchGenerator.exec(modifiedVariant)
        .get(0);
    final String modifiedSourceCode = String.join("\n", fileDiff.getModifiedSourceCodeLines());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator2() {
    final Path basePath = Paths.get("example/BuildSuccess02");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final String expected = new StringBuilder().append("")
        .append("package example;\n")
        .append("public class Bar {\n")
        .append("  public static int bar1(  int n){\n")
        // .append(" return n + 1;\n")
        .append("  }\n")
        .append("  public static int bar2(  int n){\n")
        .append("    return n - 1;\n")
        .append("  }\n")
        .append("  public static void bar3(){\n")
        .append("    new String();\n")
        .append("  }\n")
        .append("}")
        .toString();

    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 削除位置の Location 作成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/example/Bar.java")), statement, ast);

    final DeleteOperation operation = new DeleteOperation();
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final Variant modifiedVariant = new Variant(0, 0,
        new Gene(Arrays.asList(new Base(location, operation))), code, null, null, null, null);

    final FileDiff fileDiff = patchGenerator.exec(modifiedVariant)
        .get(0);
    final String modifiedSourceCode = String.join("\n", fileDiff.getModifiedSourceCodeLines());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator3() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final String expected = new StringBuilder().append("")
        .append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    if (n > 0) {\n")
        .append("      n--;\n")
        .append("    }\n")
        .append(" else {\n")
        .append("      n++;\n")
        .append("    }\n")
        .append("\ta();\n")
        .append("    return n;\n")
        .append("  }\n")
        .append("}")
        .toString();

    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement, ast);

    // 挿入対象生成
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    final InsertOperation operation = new InsertOperation(insertStatement);
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final Variant modifiedVariant = new Variant(0,0,
        new Gene(Arrays.asList(new Base(location, operation))), code, null, null, null, null);

    final FileDiff fileDiff = patchGenerator.exec(modifiedVariant)
        .get(0);
    final String modifiedSourceCode = String.join("\n", fileDiff.getModifiedSourceCodeLines());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPatchGenerator4() {
    final Path basePath = Paths.get("example/BuildSuccess01/");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final String expected = new StringBuilder().append("")
        .append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    {\n")
        .append("\t\ta();\n")
        .append("\t}\n")
        .append("    return n;\n")
        .append("  }\n")
        .append("}")
        .toString();

    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 挿入位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement, ast);

    // 挿入対象生成
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    statement = jdtAST.newExpressionStatement(invocation);
    final Block replaceBlock = jdtAST.newBlock();
    replaceBlock.statements()
        .add(statement);

    final ReplaceOperation operation = new ReplaceOperation(replaceBlock);
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final Variant modifiedVariant = new Variant(0,0,
        new Gene(Arrays.asList(new Base(location, operation))), code, null, null, null, null);

    final FileDiff fileDiff = patchGenerator.exec(modifiedVariant)
        .get(0);
    final String modifiedSourceCode = String.join("\n", fileDiff.getModifiedSourceCodeLines());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator5() {
    final Path basePath = Paths.get("example/BuildSuccess09");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final String delimiter = System.lineSeparator();
    final String expected = new StringBuilder().append("")
        .append("--- example.Foo" + delimiter)
        .append("+++ example.Foo" + delimiter)
        .append("@@ -3,11 +3,6 @@" + delimiter)
        .append(" public class Foo {" + delimiter)
        .append(" " + delimiter)
        .append("   public int foo(int n) {" + delimiter)
        .append("-    if (n > 0) {" + delimiter)
        .append("-      n--;" + delimiter)
        .append("-    } else {" + delimiter)
        .append("-      n++;" + delimiter)
        .append("-    }" + delimiter)
        .append("     return n;" + delimiter)
        .append("   }" + delimiter)
        .append(" }")
        .toString();

    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 削除位置の Location 作成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement, ast);

    final DeleteOperation operation = new DeleteOperation();
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final Variant modifiedVariant = new Variant(0,0,
        new Gene(Arrays.asList(new Base(location, operation))), code, null, null, null, null);

    final FileDiff fileDiff = patchGenerator.exec(modifiedVariant)
        .get(0);

    assertThat(fileDiff.getDiff()).isEqualToNormalizingNewlines(expected);
  }
}
