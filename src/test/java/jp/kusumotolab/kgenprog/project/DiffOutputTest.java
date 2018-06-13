package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.DiffOutput;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;

public class DiffOutputTest {

  @Test
  public void DiffOutputTest1() {
    Path basePath = Paths.get("example/example01/");
    DiffOutput diffOutput = new DiffOutput();

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(1);
    DeleteOperation operation = new DeleteOperation();
    JDTLocation location = new JDTLocation(new TargetSourceFile(
        Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java")), statement);

    GeneratedSourceCode code = operation.apply(originVariant.getGeneratedSourceCode(), location);
    List<Variant> modVariant = new ArrayList<Variant>(Arrays.asList(
        new Variant(new SimpleGene(Arrays.asList(new Base(location, operation))), null, code)));

    diffOutput.outputResult(project, modVariant);
  }

  @Test
  public void DiffOutputTest2() {
    Path basePath = Paths.get("example/example03/");
    DiffOutput diffOutput = new DiffOutput();

    TargetProject project = TargetProject.generate(basePath);
    Variant originVariant = project.getInitialVariant();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) originVariant.getGeneratedSourceCode().getFiles().get(0);

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
  }

}
