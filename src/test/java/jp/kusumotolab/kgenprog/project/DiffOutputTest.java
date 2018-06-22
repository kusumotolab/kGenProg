package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class DiffOutputTest {

  @Test
  public void DiffOutputTest1() {
    Path basePath = Paths.get("example/example01/");
    DiffOutput diffOutput = new DiffOutput();

    String sep = "\r\n";

    String expected = "package jp.kusumotolab;" + sep +
        "public class BuggyCalculator {" + sep +
        "  public int close_to_zero(  int n){" + sep +
        "    return n;" + sep +
        "  }" + sep +
        "}" + sep;

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

    //削除位置の Location 作成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(0);
    DeleteOperation operation = new DeleteOperation();
    JDTLocation location = new JDTLocation(new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java")), statement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    File modFile = new File("example/example01/modified/BuggyCalculator.java");

    try {
      String modSource = new String(Files.readAllBytes(Paths.get(modFile.toURI())));
      DeleteFiles(new File("example/example01/modified"));
      assertThat(modSource, is(expected));
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }

  }

  @Test
  public void DiffOutputTest2() {
    Path basePath = Paths.get("example/example03/");
    DiffOutput diffOutput = new DiffOutput();

    String sep = "\r\n";

    String expected = "package jp.kusumotolab;" + sep +
        "" + sep +
        "public class Util {" + sep +
        "\tpublic static int plus(int n) {" + sep +
        "\t}" + sep +
        "" + sep +
        "\tpublic static int minus(int n) {" + sep +
        "\t\treturn n - 1;" + sep +
        "\t}" + sep +
        "" + sep +
        "\t// テストからのみ実行されるダミー関数" + sep +
        "\tpublic static void dummy() {" + sep +
        "\t\tnew String();" + sep +
        "\t}" + sep +
        "}" + sep;

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

    //削除位置の Location 作成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(0);
    DeleteOperation operation = new DeleteOperation();
    JDTLocation location = new JDTLocation(
        new TargetSourceFile(Paths.get("example/example03/src/jp/kusumotolab/Util.java")),
        statement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    File modFile = new File("example/example03/modified/Util.java");

    try {
      String modSource = new String(Files.readAllBytes(Paths.get(modFile.toURI())));
      DeleteFiles(new File("example/example03/modified"));
      assertThat(modSource, is(expected));
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  @Test
  public void DiffOutputTest3() {
    Path basePath = Paths.get("example/example01/");
    DiffOutput diffOutput = new DiffOutput();

    String sep = "\r\n";

    String expected = "package jp.kusumotolab;" + sep +
        "public class BuggyCalculator {" + sep +
        "  public int close_to_zero(  int n){" + sep +
        "    if (n > 0) {" + sep +
        "      n--;" + sep +
        "    }" + sep +
        " else {" +  sep +
        "      n++;" + sep +
        "    }" + sep +
        "    a();" + sep +
        "\treturn n;" + sep +
        "  }" + sep +
        "}" + sep;

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(0);
    JDTLocation location = new JDTLocation(new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java")), statement);

    // 挿入対象生成
    AST jdtAST = ast.getRoot().getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    InsertOperation operation = new InsertOperation(insertStatement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);

    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    File modFile = new File("example/example01/modified/BuggyCalculator.java");

    try {
      String modSource = new String(Files.readAllBytes(Paths.get(modFile.toURI())));
      DeleteFiles(new File("example/example01/modified"));
      assertThat(modSource, is(expected));
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }



  }

  @Test
  public void DiffOutputTest4() {
    Path basePath = Paths.get("example/example01/");
    DiffOutput diffOutput = new DiffOutput();

    String sep = "\r\n";

    String expected = "package jp.kusumotolab;" + sep +
        "public class BuggyCalculator {" + sep +
        "  public int close_to_zero(  int n){" + sep +
        "    {" +  sep +
        "\t\ta();" + sep +
        "\t}" + sep +
        "    return n;" + sep +
        "  }" + sep +
        "}" + sep;

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(0);
    JDTLocation location = new JDTLocation(new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java")), statement);

    // 挿入対象生成
    AST jdtAST = ast.getRoot().getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    statement = jdtAST.newExpressionStatement(invocation);
    Block replaceBlock = jdtAST.newBlock();
    replaceBlock.statements().add(statement);

    ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);

    File modFile = new File("example/example01/modified/BuggyCalculator.java");

    try {
      String modSource = new String(Files.readAllBytes(Paths.get(modFile.toURI())));
      DeleteFiles(new File("example/example01/modified"));
      assertThat(modSource, is(expected));
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }

  }

  /***
   * ファイルまたはディレクトリを削除するメソッド
   * @param file
   */
  public void DeleteFiles(File file) {
    if(file.exists()) {
      if(file.isFile()) {
        if(!file.delete()) {
          System.out.println("Failed to delete File");
        }
      } else {
        File[] files = file.listFiles();

        for(int i = 0; i < files.length; i++) {
          DeleteFiles(files[i]);
        }

        file.delete();
      }
    } else {
      System.out.println("File does not exist");
    }
  }

}
