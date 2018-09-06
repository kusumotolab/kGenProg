package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.GenerationalVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.PatchGenerator;

public class KGenProgMainTest {

  private final static String ProductName = "src/example/CloseToZero.java";
  private final static String TestName = "src/example/CloseToZeroTest.java";

  private final static Path WorkPath = Paths.get("tmp/work");
  private final static Path OutPath = Paths.get("tmp/out");

  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(WorkPath.toFile());
    FileUtils.deleteDirectory(OutPath.toFile());
  }

  /*
   * KGenProgMainオブジェクトを生成するヘルパーメソッド
   */
  private KGenProgMain createMain(final Path rootPath, final Path productPath,
      final Path testPath) {

    final List<Path> productPaths = Arrays.asList(productPath);
    final List<Path> testPaths = Arrays.asList(testPath);

    final Configuration config = new Configuration.Builder(rootPath, productPaths, testPaths)
        .setWorkingDir(WorkPath)
        .setTimeLimitSeconds(600)
        .setMaxGeneration(100)
        .setRequiredSolutionsCount(1)
        .build();
    final FaultLocalization faultLocalization = new Ochiai();
    final Random random = new Random();
    final CandidateSelection statementSelection = new RouletteStatementSelection(random);
    final Mutation mutation = new RandomMutation(10, random, statementSelection);
    final Crossover crossover = new SinglePointCrossover(random);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final PatchGenerator patchGenerator = new PatchGenerator();

    return new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, patchGenerator);
  }

  @Test
  public void testCloseToZero01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(ProductName);
    final Path testPath = rootPath.resolve(TestName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testCloseToZero02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path productPath = rootPath.resolve(ProductName);
    final Path testPath = rootPath.resolve(TestName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testCloseToZero03() {
    final Path rootPath = Paths.get("example/CloseToZero03");
    final Path productPath = rootPath.resolve(ProductName);
    final Path testPath = rootPath.resolve(TestName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testCloseToZero04() {
    final Path rootPath = Paths.get("example/CloseToZero04");
    final Path productPath = rootPath.resolve(ProductName);
    final Path testPath = rootPath.resolve(TestName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testGCD01() {
    final Path rootPath = Paths.get("example/GCD01");
    final String productName = "src/example/GreatestCommonDivider.java";
    final String testName = "src/example/GreatestCommonDividerTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testQuickSort01() {
    final Path rootPath = Paths.get("example/QuickSort01");
    final String productName = "src/example/QuickSort.java";
    final String testName = "src/example/QuickSortTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }
}
