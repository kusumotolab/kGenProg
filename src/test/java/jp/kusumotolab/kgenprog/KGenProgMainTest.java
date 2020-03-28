package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantRandomSelection;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantRandomSelection;
import jp.kusumotolab.kgenprog.ga.crossover.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.SimpleMutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementAndConditionSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.selection.GenerationalVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.output.Exporter;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;

public class KGenProgMainTest {

  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  /*
   * KGenProgMainオブジェクトを生成するヘルパーメソッド
   */
  private KGenProgMain createMain(final Path rootPath, final Path productPath,
      final Path testPath, final CandidateSelection selection) {

    final List<Path> productPaths = Arrays.asList(productPath);
    final List<Path> testPaths = Arrays.asList(testPath);
    final Path outDir = tempFolder.getRoot()
        .toPath();

    final Configuration config =
        new Configuration.Builder(rootPath, productPaths, testPaths).setTimeLimitSeconds(600)
            .setTestTimeLimitSeconds(1)
            .setMaxGeneration(100)
            .setRequiredSolutionsCount(1)
            .setOutDir(outDir)
            .setNeedNotOutput(true)
            .build();

    final FaultLocalization faultLocalization = new Ochiai();
    final Random random = new Random(config.getRandomSeed());
    final Mutation mutation = new SimpleMutation(config.getMutationGeneratingCount(), random,
        selection, config.getScope());
    final Crossover crossover =
        new SinglePointCrossover(random, new FirstVariantRandomSelection(random),
            new SecondVariantRandomSelection(random), config.getCrossoverGeneratingCount());
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection =
        new GenerationalVariantSelection(config.getHeadcount(), random);
    final LocalTestExecutor testExecutor = new LocalTestExecutor(config);
    final Exporter exporter = new Exporter(config) {
      @Override
      public void export(final VariantStore variantStore) {
      }
    };

    return new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, testExecutor, exporter);
  }

  @Test
  public void testCloseToZero01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCloseToZero02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCloseToZero03() {
    final Path rootPath = Paths.get("example/CloseToZero03");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCloseToZero04() {
    final Path rootPath = Paths.get("example/CloseToZero04");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCountDown01() {
    final Path rootPath = Paths.get("example/CountDown01");
    final Path productPath = rootPath.resolve("src/example/CountDown.java");
    final Path testPath = rootPath.resolve("src/example/CountDownTest.java");

    final CandidateSelection selection = new RouletteStatementAndConditionSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testGCD01() {
    final Path rootPath = Paths.get("example/GCD01");
    final String productName = "src/example/GreatestCommonDivider.java";
    final String testName = "src/example/GreatestCommonDividerTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testQuickSort01() {
    final Path rootPath = Paths.get("example/QuickSort01");
    final String productName = "src/example/QuickSort.java";
    final String testName = "src/example/QuickSortTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    final CandidateSelection selection = new RouletteStatementSelection(new Random(0));
    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, selection);
    final List<Variant> variants = kGenProgMain.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }
}
