package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class PatchGeneratorTest {


  @Test
  public void testPatchGenerator1() throws IOException {
    final Path basePath = Paths.get("example/example01");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    return n;\n" + "  }\n" + "}\n\n";

    final TargetProject project = TargetProjectFactory.create(basePath);
    final Variant originVariant = project.getInitialVariant();
    final GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getAsts()
        .get(0);

    // 削除位置の Location 作成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final DeleteOperation operation = new DeleteOperation();
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

    final GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode = new String(
        Files.readAllBytes(outdirPath.resolve("variant1/jp.kusumotolab.BuggyCalculator.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator2() throws IOException {
    final Path basePath = Paths.get("example/example03");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = "package jp.kusumotolab;\n" + "\n" + "public class Util {\n"
        + "\tpublic static int plus(int n) {\n" + "\t}\n" + "\n"
        + "\tpublic static int minus(int n) {\n" + "\t\treturn n - 1;\n" + "\t}\n" + "\n"
        + "\t// テストからのみ実行されるダミー関数\n" + "\tpublic static void dummy() {\n" + "\t\tnew String();\n"
        + "\t}\n" + "}\n\n";

    final TargetProject project = TargetProjectFactory.create(basePath);
    final Variant originVariant = project.getInitialVariant();
    final GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getAsts()
        .get(0);

    // 削除位置の Location 作成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final DeleteOperation operation = new DeleteOperation();
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(basePath.resolve("src/jp/kusumotolab/Util.java")), statement);

    final GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode =
        new String(Files.readAllBytes(outdirPath.resolve("variant1/jp.kusumotolab.Util.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator3() throws IOException {
    final Path basePath = Paths.get("example/example01");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    if (n > 0) {\n" + "      n--;\n"
        + "    }\n" + " else {\n" + "      n++;\n" + "    }\n" + "    a();\n" + "\treturn n;\n"
        + "  }\n" + "}\n\n";

    final TargetProject project = TargetProjectFactory.create(basePath);
    final Variant originVariant = project.getInitialVariant();
    final GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getAsts()
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
        new ProductSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

    // 挿入対象生成
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);

    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode = new String(
        Files.readAllBytes(outdirPath.resolve("variant1/jp.kusumotolab.BuggyCalculator.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPatchGenerator4() throws IOException {
    final Path basePath = Paths.get("example/example01/");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    {\n" + "\t\ta();\n" + "\t}\n"
        + "    return n;\n" + "  }\n" + "}\n\n";

    final TargetProject project = TargetProjectFactory.create(basePath);
    final Variant originVariant = project.getInitialVariant();
    final GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getAsts()
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
        new ProductSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

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

    final GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode = new String(
        Files.readAllBytes(outdirPath.resolve("variant1/jp.kusumotolab.BuggyCalculator.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

}
