package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class CUILauncherTest {

  private CUILauncher launcher;

  @Before
  public void setUp() {
    launcher = new CUILauncher();
  }

  @After
  public void tearDown() {
    launcher = null;
  }

  @Test
  public void testGetLogLevel() {
    assertThat(launcher.getLogLevel()).isEqualTo(Level.INFO);
  }

  @Test
  public void testSetLogLevelDebug() {
    launcher.setLogLevelDebug(true);
    assertThat(launcher.getLogLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  public void testSetLogLevelError() {
    launcher.setLogLevelError(true);
    assertThat(launcher.getLogLevel()).isEqualTo(Level.ERROR);
  }

  @Test
  public void testSetRootDirAndGetRootDir() {
    launcher.setRootDir("./");
    assertThat(launcher.getRootDir()).isEqualTo(Paths.get("./"));
  }

  @Test
  public void testAddProductSourcePathsAndGetProductSourcePaths01() {
    launcher.addProductSourcePath("./src/main/java");
    assertThat(launcher.getProductSourcePaths())
        .containsExactly(new ProductSourcePath(Paths.get("./src/main/java")));
  }

  /**
   * 複数パスを追加するテスト． CUILauncher ではディレクトリが存在するかどうかはチェックしていないため， 存在しないパスを指定しても例外は発生しない．
   */
  @Test
  public void testAddProductSourcePathsAndGetProductSourcePaths02() {
    launcher.addProductSourcePath("./src/main/java");
    launcher.addProductSourcePath("./src/main/kotlin"); // 実在しないパス
    launcher.addProductSourcePath("./src/main/resources");
    assertThat(launcher.getProductSourcePaths())
        .containsExactlyInAnyOrder(new ProductSourcePath(Paths.get("./src/main/java")),
            new ProductSourcePath(Paths.get("./src/main/kotlin")), // 実在しないパスを含んでいてもよい
            new ProductSourcePath(Paths.get("./src/main/resources")));
  }

  @Test
  public void testAddTestSourcePathsAndGetTestSourcePaths01() {
    launcher.addTestSourcePath("./src/test/java");
    assertThat(launcher.getTestSourcePaths())
        .containsExactly(new TestSourcePath(Paths.get("./src/test/java")));
  }

  /**
   * 複数パスを追加するテスト． CUILauncher ではディレクトリが存在するかどうかはチェックしていないため， 存在しないパスを指定しても例外は発生しない．
   */
  @Test
  public void testAddTestSourcePathsAndGetTestSourcePaths02() {
    launcher.addTestSourcePath("./src/test/java");
    launcher.addTestSourcePath("./src/test/kotlin");  // 実在しないパス
    launcher.addTestSourcePath("./src/test/resources");
    assertThat(launcher.getTestSourcePaths())
        .containsExactlyInAnyOrder(new TestSourcePath(Paths.get("./src/test/java")),
            new TestSourcePath(Paths.get("./src/test/kotlin")), // 実在しないパスを含んでいてもよい
            new TestSourcePath(Paths.get("./src/test/resources")));
  }

  @Test
  public void testAddClassPathsAndGetClassPaths01() {
    launcher.addClassPath("./lib");
    assertThat(launcher.getClassPaths())
        .containsExactly(new ClassPath(Paths.get("./lib")));
  }

  /**
   * 複数パスを追加するテスト． CUILauncher ではディレクトリが存在するかどうかはチェックしていないため， 存在しないパスを指定しても例外は発生しない．
   */
  @Test
  public void testAddClassPathsAndGetClassPaths02() {
    launcher.addClassPath("./lib01"); // 実在しないパス
    launcher.addClassPath("./lib02"); // 実在しないパス
    launcher.addClassPath("./lib03"); // 実在しないパス
    assertThat(launcher.getClassPaths())
        .containsExactlyInAnyOrder(new ClassPath(Paths.get("./lib01")), // 実在しないパスを含んでいてもよい
            new ClassPath(Paths.get("./lib02")), // 実在しないパスを含んでいてもよい
            new ClassPath(Paths.get("./lib03"))); // 実在しないパスを含んでいてもよい
  }
}
