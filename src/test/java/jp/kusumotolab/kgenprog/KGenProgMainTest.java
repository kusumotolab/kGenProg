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
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.ResultOutput;
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

  /*
   * KGenProgMainオブジェクトを生成するヘルパーメソッド
   */
  private KGenProgMain createMain(final Path rootPath, final Path productPath,
      final Path testPath) {

    final ProductSourcePath productSourcePath = new ProductSourcePath(productPath);
    final TestSourcePath testSourcePath = new TestSourcePath(testPath);
    final List<ProductSourcePath> productSourcePaths = Arrays.asList(productSourcePath);
    final List<TestSourcePath> testSourcePaths = Arrays.asList(testSourcePath);

    final TargetProject project = TargetProjectFactory.create(rootPath, productSourcePaths,
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

    return new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, resultGenerator, workPath);
  }

  @Test
  public void testExample04() {
    final Path rootPath = Paths.get("example/example04");
    final Path productPath = rootPath.resolve(bc);
    final Path testPath = rootPath.resolve(bct);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testExample05() {
    final Path rootPath = Paths.get("example/example05");
    final Path productPath = rootPath.resolve(bc);
    final Path testPath = rootPath.resolve(bct);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testExample06() {
    final Path rootPath = Paths.get("example/example06");
    final Path productPath = rootPath.resolve(bc);
    final Path testPath = rootPath.resolve(bct);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testExample07() {
    final Path rootPath = Paths.get("example/example07");
    final String gcd = "src/jp/kusumotolab/GreatestCommonDivider.java";
    final String gcdt = "src/jp/kusumotolab/GreatestCommonDividerTest.java";
    final Path productPath = rootPath.resolve(gcd);
    final Path testPath = rootPath.resolve(gcdt);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testExample08() {
    final Path rootPath = Paths.get("example/example08");
    final String qs = "src/jp/kusumotolab/QuickSort.java";
    final String qst = "src/jp/kusumotolab/QuickSortTest.java";
    final Path productPath = rootPath.resolve(qs);
    final Path testPath = rootPath.resolve(qst);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }
}
