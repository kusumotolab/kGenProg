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
import jp.kusumotolab.kgenprog.ga.CandidateSelection;
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
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.PatchGenerator;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.ResultGenerator;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class KGenProgMainTest {

  private final static String Ctz = "src/example/CloseToZero.java";
  private final static String Ctzt = "src/example/CloseToZeroTest.java";

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

    final ProductSourcePath productSourcePath = new ProductSourcePath(productPath);
    final TestSourcePath testSourcePath = new TestSourcePath(testPath);
    final List<ProductSourcePath> productSourcePaths = Arrays.asList(productSourcePath);
    final List<TestSourcePath> testSourcePaths = Arrays.asList(testSourcePath);

    final TargetProject project = TargetProjectFactory.create(rootPath, productSourcePaths,
        testSourcePaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final CandidateSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation = new RandomMutation(10, randomNumberGeneration, statementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final ResultGenerator resultGenerator = new PatchGenerator(OutPath);

    return new KGenProgMain(project, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, resultGenerator, WorkPath);
  }

  @Test
  public void testCloseToZero01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(Ctz);
    final Path testPath = rootPath.resolve(Ctzt);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testCloseToZero02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path productPath = rootPath.resolve(Ctz);
    final Path testPath = rootPath.resolve(Ctzt);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore // Be ignored but should not be ignored
  @Test
  public void testCloseToZero03() {
    final Path rootPath = Paths.get("example/CloseToZero03");
    final Path productPath = rootPath.resolve(Ctz);
    final Path testPath = rootPath.resolve(Ctzt);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath);
    final List<Variant> variants = kGenProgMain.run();

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testGCD01() {
    final Path rootPath = Paths.get("example/GCD01");
    final String gcd = "src/example/GreatestCommonDivider.java";
    final String gcdt = "src/example/GreatestCommonDividerTest.java";
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
  public void testQuickSort01() {
    final Path rootPath = Paths.get("example/QuickSort01");
    final String qs = "src/example/QuickSort.java";
    final String qst = "src/example/QuickSortTest.java";
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
