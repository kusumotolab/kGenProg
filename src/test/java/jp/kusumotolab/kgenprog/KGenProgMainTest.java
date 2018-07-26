package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
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
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.DiffOutput;
import jp.kusumotolab.kgenprog.project.ResultOutput;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class KGenProgMainTest {

  final static String bc = "src/jp/kusumotolab/BuggyCalculator.java";
  final static String bct = "src/jp/kusumotolab/BuggyCalculatorTest.java";

  final Path workPath = Paths.get("tmp/work");
  final Path outPath = Paths.get("tmp/out");

  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(workPath.toFile());
    FileUtils.deleteDirectory(outPath.toFile());
  }

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
    final StatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workPath);

    final List<Variant> variants = kGenProgMain.run();
    assertThat(variants).hasSize(1)
        .allMatch(variant -> variant.getFitness()
            .getValue() == 1.0);
  }

  @Ignore // Be ignored but should not be ignored
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
    final StatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection();
    final ResultOutput resultGenerator = new DiffOutput(outPath);
    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workPath);

    final List<Variant> variants = kGenProgMain.run();
    assertThat(variants).hasSize(1)
        .allMatch(variant -> variant.getFitness()
            .getValue() == 1.0);
  }

  @Ignore // Be ignored but should not be ignored
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
    final StatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workPath);

    final List<Variant> variants = kGenProgMain.run();
    assertThat(variants).hasSize(1)
        .allMatch(variant -> variant.getFitness()
            .getValue() == 1.0);
  }

  @Ignore
  @Test
  public void testExample07() {

    final Path rootPath = Paths.get("example/example07");
    final String gcd = "src/jp/kusumotolab/GreatestCommonDivider.java";
    final String gcdt = "src/jp/kusumotolab/GreatestCommonDividerTest.java";
    final SourcePath gcdPath = new TargetSourcePath(rootPath.resolve(gcd));
    final SourcePath gcdtPath = new TestSourcePath(rootPath.resolve(gcdt));
    final List<SourcePath> targetSourceFiles = Arrays.asList(gcdPath);
    final List<SourcePath> testSourceFiles = Arrays.asList(gcdtPath);

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final StatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workPath, 60, 10, 1);
    kGenProgMain.run();
  }

  @Ignore
  @Test
  public void testExample08() {

    final Path rootPath = Paths.get("example/example08");
    final String qs = "src/jp/kusumotolab/QuickSort.java";
    final String qst = "src/jp/kusumotolab/QuickSortTest.java";
    final SourcePath qsPath = new TargetSourcePath(rootPath.resolve(qs));
    final SourcePath qsttPath = new TestSourcePath(rootPath.resolve(qst));
    final List<SourcePath> targetSourceFiles = Arrays.asList(qsPath);
    final List<SourcePath> testSourceFiles = Arrays.asList(qsttPath);

    final TargetProject project = TargetProjectFactory.create(rootPath, targetSourceFiles,
        testSourceFiles, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final RouletteStatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final ResultOutput resultGenerator = new DiffOutput(outPath);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, resultGenerator, workPath, 60, 10, 1);
    kGenProgMain.run();
  }
}
