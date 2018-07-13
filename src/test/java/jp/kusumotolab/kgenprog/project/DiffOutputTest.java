package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class DiffOutputTest {


  @Test
  public void testDiffOutput1() throws IOException {
    Path basePath = Paths.get("example/example01");
    final Path outdirPath = basePath.resolve("modified");
    DiffOutput diffOutput = new DiffOutput(outdirPath);

    String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    return n;\n" + "  }\n" + "}\n\n";

    TargetProject project = TargetProjectFactory.create(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getFiles()
        .get(0);

    // 削除位置の Location 作成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    DeleteOperation operation = new DeleteOperation();
    JDTLocation location = new JDTLocation(
        new TargetSourceFile(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    String modSource = new String(
        Files.readAllBytes(outdirPath.resolve("variant01/jp.kusumotolab.BuggyCalculator.java")));

    assertThat(normalizeCrLf(modSource), is(normalizeCrLf(expected)));

    FileUtils.deleteDirectory(outdirPath.toFile());
  }

  @Test
  public void testDiffOutput2() throws IOException {
    Path basePath = Paths.get("example/example03");
    final Path outdirPath = basePath.resolve("modified");
    DiffOutput diffOutput = new DiffOutput(outdirPath);

    String expected = "package jp.kusumotolab;\n" + "\n" + "public class Util {\n"
        + "\tpublic static int plus(int n) {\n" + "\t}\n" + "\n"
        + "\tpublic static int minus(int n) {\n" + "\t\treturn n - 1;\n" + "\t}\n" + "\n"
        + "\t// テストからのみ実行されるダミー関数\n" + "\tpublic static void dummy() {\n" + "\t\tnew String();\n"
        + "\t}\n" + "}\n\n";

    TargetProject project = TargetProjectFactory.create(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getFiles()
        .get(0);

    // 削除位置の Location 作成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    DeleteOperation operation = new DeleteOperation();
    JDTLocation location = new JDTLocation(
        new TargetSourceFile(basePath.resolve("src/jp/kusumotolab/Util.java")), statement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    String modSource =
        new String(Files.readAllBytes(outdirPath.resolve("variant01/jp.kusumotolab.Util.java")));

    assertThat(normalizeCrLf(modSource), is(normalizeCrLf(expected)));

    FileUtils.deleteDirectory(outdirPath.toFile());
  }

  @Test
  public void testDiffOutput3() throws IOException {
    Path basePath = Paths.get("example/example01");
    final Path outdirPath = basePath.resolve("modified");
    DiffOutput diffOutput = new DiffOutput(outdirPath);

    String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    if (n > 0) {\n" + "      n--;\n"
        + "    }\n" + " else {\n" + "      n++;\n" + "    }\n" + "    a();\n" + "\treturn n;\n"
        + "  }\n" + "}\n\n";

    TargetProject project = TargetProjectFactory.create(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getFiles()
        .get(0);

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    JDTLocation location = new JDTLocation(
        new TargetSourceFile(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

    // 挿入対象生成
    AST jdtAST = ast.getRoot()
        .getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    InsertOperation operation = new InsertOperation(insertStatement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);

    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    String modSource = new String(
        Files.readAllBytes(outdirPath.resolve("variant01/jp.kusumotolab.BuggyCalculator.java")));

    assertThat(normalizeCrLf(modSource), is(normalizeCrLf(expected)));

    FileUtils.deleteDirectory(outdirPath.toFile());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testDiffOutput4() throws IOException {
    Path basePath = Paths.get("example/example01/");
    final Path outdirPath = basePath.resolve("modified");
    DiffOutput diffOutput = new DiffOutput(outdirPath);

    String expected = "package jp.kusumotolab;\n" + "public class BuggyCalculator {\n"
        + "  public int close_to_zero(  int n){\n" + "    {\n" + "\t\ta();\n" + "\t}\n"
        + "    return n;\n" + "  }\n" + "}\n\n";

    TargetProject project = TargetProjectFactory.create(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast = (GeneratedJDTAST) originVariant.getGeneratedSourceCode()
        .getFiles()
        .get(0);

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    JDTLocation location = new JDTLocation(
        new TargetSourceFile(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        statement);

    // 挿入対象生成
    AST jdtAST = ast.getRoot()
        .getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    statement = jdtAST.newExpressionStatement(invocation);
    Block replaceBlock = jdtAST.newBlock();
    replaceBlock.statements()
        .add(statement);

    ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    String modSource = new String(
        Files.readAllBytes(outdirPath.resolve("variant01/jp.kusumotolab.BuggyCalculator.java")));

    assertThat(normalizeCrLf(modSource), is(normalizeCrLf(expected)));

    FileUtils.deleteDirectory(outdirPath.toFile());
  }

  private String normalizeCrLf(final String s) {
    return s.replaceAll("\\r|\\n", "\n")
        .trim();
  }
}
