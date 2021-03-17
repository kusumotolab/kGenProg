package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.KGenProgMain.ExitStatus;

public class KGenProgMainTest {

  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  /*
   * 引数で与えられた情報を利用して，CUILauncher経由でkGenProgMainを実行するメソッド
   */
  private ExitStatus runKGenProgMain(final Path rootPath, final Path productPath,
      final Path testPath) {

    final List<Path> productPaths = Collections.singletonList(productPath);
    final List<Path> testPaths = Collections.singletonList(testPath);
    final Configuration config = new Configuration.Builder(rootPath, productPaths, testPaths)
        .setTimeLimitSeconds(600)
        .setTestTimeLimitSeconds(1)
        .setMaxGeneration(100)
        .setRequiredSolutionsCount(1)
        .setPatchOutput(false) // to prevent file output
        .setHistoryRecord(false) // to prevent file output
        .build();

    final CUILauncher launcher = new CUILauncher();
    return launcher.launch(config);
  }

  @Test
  public void testCloseToZero01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testCloseToZero02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testCloseToZero03() {
    final Path rootPath = Paths.get("example/CloseToZero03");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testCloseToZero04() {
    final Path rootPath = Paths.get("example/CloseToZero04");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testCountDown01() {
    final Path rootPath = Paths.get("example/CountDown01");
    final Path productPath = rootPath.resolve("src/example/CountDown.java");
    final Path testPath = rootPath.resolve("src/example/CountDownTest.java");

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testGCD01() {
    final Path rootPath = Paths.get("example/GCD01");
    final String productName = "src/example/GreatestCommonDivider.java";
    final String testName = "src/example/GreatestCommonDividerTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }

  @Test
  public void testQuickSort01() {
    final Path rootPath = Paths.get("example/QuickSort01");
    final String productName = "src/example/QuickSort.java";
    final String testName = "src/example/QuickSortTest.java";
    final Path productPath = rootPath.resolve(productName);
    final Path testPath = rootPath.resolve(testName);

    assertThatCode(() -> runKGenProgMain(rootPath, productPath, testPath)).
        doesNotThrowAnyException();
  }


  @Test
  public void testBuildFailure() {
    final Path rootPath = Paths.get("example/Abnormals/BuildFailure");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final ExitStatus status = runKGenProgMain(rootPath, productPath, testPath);
    assertThat(status).isEqualTo(ExitStatus.FAILURE_INITIAL_BUILD);

  }

  @Test
  public void testNoBugs() {
    final Path rootPath = Paths.get("example/Abnormals/NoBugs");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final ExitStatus status = runKGenProgMain(rootPath, productPath, testPath);
    assertThat(status).isEqualTo(ExitStatus.FAILURE_NO_BUGS);
  }

  @Test
  public void testMissingRootDir() {
    final Path rootPath = Paths.get("no-such-project-dir");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final ExitStatus status = runKGenProgMain(rootPath, productPath, testPath);
    assertThat(status).isEqualTo(ExitStatus.FAILURE_INVALID_PROJECT);
  }

}
