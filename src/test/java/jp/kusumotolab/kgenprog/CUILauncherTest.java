package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class CUILauncherTest {

  private CUILauncher launcher;

  @Before
  public void setUp() throws Exception {
    launcher = new CUILauncher();
  }

  @After
  public void tearDown() throws Exception {
    launcher = null;
  }

  @Test
  public void testSetRootDirAndGetRootDir() {
    launcher.setRootDir("./");
    assertThat(launcher.getRootDir()).isEqualTo(Paths.get("./")
        .toAbsolutePath());
  }

  @Test
  public void testAddProductSourcePathsAndGetProductSourcePaths01() {
    launcher.addProductSourcePath("./src/main/java");
    assertThat(launcher.getProductSourcePaths()).contains(
        new ProductSourcePath(Paths.get("./src/main/java")
            .toAbsolutePath()));
  }

  @Test
  public void testAddProductSourcePathsAndGetProductSourcePaths02() {
    launcher.addProductSourcePath("./src/main/java");
    launcher.addProductSourcePath("./src/main/kotlin");
    launcher.addProductSourcePath("./src/main/resources");
    assertThat(launcher.getProductSourcePaths())
        .contains(new ProductSourcePath(Paths.get("./src/main/java")
            .toAbsolutePath()))
        .contains(new ProductSourcePath(Paths.get("./src/main/kotlin")
            .toAbsolutePath()))
        .contains(new ProductSourcePath(Paths.get("./src/main/resources")
            .toAbsolutePath()));
  }

  @Test
  public void testAddTestSourcePathsAndGetTestSourcePaths01() {
    launcher.addTestSourcePath("./src/test/java");
    assertThat(launcher.getTestSourcePaths()).contains(
        new TestSourcePath(Paths.get("./src/test/java")
            .toAbsolutePath()));
  }

  @Test
  public void testAddTestSourcePathsAndGetTestSourcePaths02() {
    launcher.addTestSourcePath("./src/test/java");
    launcher.addTestSourcePath("./src/test/kotlin");
    launcher.addTestSourcePath("./src/test/resources");
    assertThat(launcher.getTestSourcePaths())
        .contains(new TestSourcePath(Paths.get("./src/test/java")
            .toAbsolutePath()))
        .contains(new TestSourcePath(Paths.get("./src/test/kotlin")
            .toAbsolutePath()))
        .contains(new TestSourcePath(Paths.get("./src/test/resources")
            .toAbsolutePath()));
  }

  @Test
  public void testAddClassPathsAndGetClassPaths01() {
    launcher.addClassPath("./lib");
    assertThat(launcher.getClassPaths())
        .contains(new ClassPath(Paths.get("./lib")
            .toAbsolutePath()));
  }

  @Test
    launcher.addClassPath("./lib01");
    launcher.addClassPath("./lib02");
    launcher.addClassPath("./lib03");
  public void testAddClassPathsAndGetClassPaths02() {
    assertThat(launcher.getClassPaths())
        .contains(new ClassPath(Paths.get("./lib01")
            .toAbsolutePath()))
        .contains(new ClassPath(Paths.get("./lib02")
            .toAbsolutePath()))
        .contains(new ClassPath(Paths.get("./lib03")
            .toAbsolutePath()));
  }
}
