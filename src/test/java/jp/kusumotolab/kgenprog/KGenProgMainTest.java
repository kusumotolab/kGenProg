package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import jp.kusumotolab.kgenprog.ga.GenerationalVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.RandomNumberGeneration;
import jp.kusumotolab.kgenprog.ga.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.StatementSelection;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.DiffOutput;
import jp.kusumotolab.kgenprog.project.ResultOutput;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

@Ignore
public class KGenProgMainTest {

  final static String bc = "jp.kusumotolab.BuggyCalculator";
  final static String bct = "jp.kusumotolab.BuggyCalculatorTest";

  @Test
  public void testExample04() {

    final Path rootPath = Paths.get("example/example04");
    final SourcePath bcPath = new TargetSourcePath(rootPath.resolve(bc));
    final SourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));
    final List<SourcePath> targetSourcePaths = Arrays.asList(bcPath);
    final List<SourcePath> testSourcePaths = Arrays.asList(bctPath);

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourcePaths,
        testSourcePaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final StatementSelection statementSelection = new RouletteStatementSelection(
        randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final Path outPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, outPath);
    kGenProgMain.run();
  }

  @Test
  public void testExample05() {

    final Path rootPath = Paths.get("example/example05");
    final SourcePath bcPath = new TargetSourcePath(rootPath.resolve(bc));
    final SourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));
    final List<SourcePath> targetSourcePaths = Arrays.asList(bcPath);
    final List<SourcePath> testSourcePaths = Arrays.asList(bctPath);

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourcePaths,
        testSourcePaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final StatementSelection statementSelection = new RouletteStatementSelection(
        randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection();
    final Path outPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(outPath);
    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, outPath);
    kGenProgMain.run();
  }

  @Test
  public void testExample06() {

    final Path rootPath = Paths.get("example/example06");
    final SourcePath bcPath = new TargetSourcePath(rootPath.resolve(bc));
    final SourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));
    final List<SourcePath> targetSourcePaths = Arrays.asList(bcPath);
    final List<SourcePath> testSourcePaths = Arrays.asList(bctPath);

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourcePaths,
        testSourcePaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final StatementSelection statementSelection = new RouletteStatementSelection(
        randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final Path outPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, outPath);
    kGenProgMain.run();
  }

  @Test
  public void testExample07() {

    final Path rootPath = Paths.get("example/example07");
    final List<SourcePath> targetSourceFiles = new ArrayList<>();
    targetSourceFiles.add(
        new TargetSourcePath(rootPath.resolve("src/jp/kusumotolab/GreatestCommonDivider.java")));
    final List<SourcePath> testSourceFiles = new ArrayList<>();
    testSourceFiles.add(
        new TestSourcePath(rootPath.resolve("src/jp/kusumotolab/GreatestCommonDividerTest.java")));

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final StatementSelection statementSelection = new RouletteStatementSelection(
        randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final Path workingPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(workingPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workingPath, 60, 10, 1);
    kGenProgMain.run();
  }

  //@Test
  public void testExample08() {

    final Path rootPath = Paths.get("example/example08");
    final List<SourcePath> targetSourceFiles = new ArrayList<>();
    targetSourceFiles.add(
        new TargetSourcePath(rootPath.resolve("src/jp/kusumotolab/QuickSort.java")));
    final List<SourcePath> testSourceFiles = new ArrayList<>();
    testSourceFiles.add(
        new TestSourcePath(rootPath.resolve("src/jp/kusumotolab/QuickSortTest.java")));

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final RouletteStatementSelection statementSelection = new RouletteStatementSelection(
        randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final Path workingPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(workingPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workingPath, 60, 10, 1);
    kGenProgMain.run();
  }
}
