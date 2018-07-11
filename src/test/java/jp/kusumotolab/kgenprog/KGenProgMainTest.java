package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.SiglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.DiffOutput;
import jp.kusumotolab.kgenprog.project.ResultOutput;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.TestSourceFile;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

@Ignore
public class KGenProgMainTest {

  @Test
  public void testExample04() {

    final Path rootPath = Paths.get("example/example04");
    final List<SourceFile> targetSourceFiles = new ArrayList<>();
    targetSourceFiles
        .add(new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")));
    final List<SourceFile> testSourceFiles = new ArrayList<>();
    testSourceFiles
        .add(new TestSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")));

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);

    final FaultLocalization faultLocalization = new Ochiai();
    final Mutation mutation = new RandomMutation();
    final Crossover crossover = new SiglePointCrossover();
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection();

    final KGenProgMain kGenProgMain = new KGenProgMain(project, faultLocalization, mutation,
        crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection);
    kGenProgMain.run();

    final ResultOutput ro = new DiffOutput(kGenProgMain.workingDir);
    ro.outputResult(project, kGenProgMain.getComplatedVariants());
  }

  @Test
  public void testExample05() {

    final Path rootPath = Paths.get("example/example05");
    final List<SourceFile> targetSourceFiles = new ArrayList<>();
    targetSourceFiles
        .add(new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")));
    final List<SourceFile> testSourceFiles = new ArrayList<>();
    testSourceFiles
        .add(new TestSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")));

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);

    FaultLocalization faultLocalization = new Ochiai();
    Mutation mutation = new RandomMutation();
    Crossover crossover = new SiglePointCrossover();
    SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    VariantSelection variantSelection = new DefaultVariantSelection();

    KGenProgMain kGenProgMain = new KGenProgMain(project, faultLocalization, mutation, crossover,
        sourceCodeGeneration, sourceCodeValidation, variantSelection);
    kGenProgMain.run();

    final ResultOutput ro = new DiffOutput(kGenProgMain.workingDir);
    ro.outputResult(project, kGenProgMain.getComplatedVariants());
  }

  @Test
  public void testExample06() {

    final Path rootPath = Paths.get("example/example06");
    final List<SourceFile> targetSourceFiles = new ArrayList<>();
    targetSourceFiles
        .add(new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")));
    final List<SourceFile> testSourceFiles = new ArrayList<>();
    testSourceFiles
        .add(new TestSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")));

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);

    FaultLocalization faultLocalization = new Ochiai();
    Mutation mutation = new RandomMutation();
    Crossover crossover = new SiglePointCrossover();
    SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    VariantSelection variantSelection = new DefaultVariantSelection();

    KGenProgMain kGenProgMain = new KGenProgMain(project, faultLocalization, mutation, crossover,
        sourceCodeGeneration, sourceCodeValidation, variantSelection);
    kGenProgMain.run();

    final ResultOutput ro = new DiffOutput(kGenProgMain.workingDir);
    ro.outputResult(project, kGenProgMain.getComplatedVariants());
  }
}
