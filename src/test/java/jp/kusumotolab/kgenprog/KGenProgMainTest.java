package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class KGenProgMainTest {

  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  /*
   * 引数で与えられた情報を利用して，CUILauncher経由でkGenProgMainを実行するメソッド
   */
  private List<Variant> runKGenProgMain(final Path rootPath, final Path productPath,
      final Path testPath) {

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

    final CUILauncher launcher = new CUILauncher();
    return launcher.launch(config);
  }

  @Test
  public void testCloseToZero01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCloseToZero02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  @Ignore
  public void testCloseToZero03() {
    final Path rootPath = Paths.get("example/CloseToZero03");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCloseToZero04() {
    final Path rootPath = Paths.get("example/CloseToZero04");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Test
  public void testCountDown01() {
    final Path rootPath = Paths.get("example/CountDown01");
    final Path productPath = rootPath.resolve("src/example/CountDown.java");
    final Path testPath = rootPath.resolve("src/example/CountDownTest.java");

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

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

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

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

    final List<Variant> variants = runKGenProgMain(rootPath, productPath, testPath);

    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }
}
