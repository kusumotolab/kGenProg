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
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = new StringBuilder().append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    return n;\n" + "  }\n")
        .append("}\n\n")
        .toString();

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
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement);

    final GeneratedSourceCode code =
        operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode =
        new String(Files.readAllBytes(outdirPath.resolve("variant1/example.Foo.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator2() throws IOException {
    final Path basePath = Paths.get("example/BuildSuccess03");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

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
        .append("}\n\n")
        .toString();

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
        new ProductSourcePath(basePath.resolve("src/example/Bar.java")), statement);

    final GeneratedSourceCode code =
        operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode =
        new String(Files.readAllBytes(outdirPath.resolve("variant1/example.Bar.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @Test
  public void testPatchGenerator3() throws IOException {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = new StringBuilder().append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    if (n > 0) {\n")
        .append("      n--;\n")
        .append("    }\n")
        .append(" else {\n")
        .append("      n++;\n")
        .append("    }\n")
        .append("    a();\n")
        .append("\treturn n;\n")
        .append("  }\n")
        .append("}\n\n")
        .toString();

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
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement);

    // 挿入対象生成
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    final InsertOperation operation = new InsertOperation(insertStatement);

    final GeneratedSourceCode code =
        operation.apply(originVariant.getGeneratedSourceCode(), location);

    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode =
        new String(Files.readAllBytes(outdirPath.resolve("variant1/example.Foo.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPatchGenerator4() throws IOException {
    final Path basePath = Paths.get("example/BuildSuccess01/");
    final Path outdirPath = basePath.resolve("modified");
    final PatchGenerator patchGenerator = new PatchGenerator(outdirPath);

    final String expected = new StringBuilder().append("package example;\n")
        .append("public class Foo {\n")
        .append("  public int foo(  int n){\n")
        .append("    {\n")
        .append("\t\ta();\n")
        .append("\t}\n")
        .append("    return n;\n")
        .append("  }\n")
        .append("}\n\n")
        .toString();

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
        new ProductSourcePath(basePath.resolve("src/example/Foo.java")), statement);

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

    final GeneratedSourceCode code =
        operation.apply(originVariant.getGeneratedSourceCode(), location);
    final List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    patchGenerator.exec(project, modVariant);

    final String modifiedSourceCode =
        new String(Files.readAllBytes(outdirPath.resolve("variant1/example.Foo.java")));

    FileUtils.deleteDirectory(outdirPath.toFile());

    assertThat(modifiedSourceCode).isEqualToNormalizingNewlines(expected);
  }

}
