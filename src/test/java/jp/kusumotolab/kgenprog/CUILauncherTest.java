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
  public void testSetProductSourcePathsAndGetProductSourcePaths01() {
    launcher.setProductSourcePaths("./src/main/java");
    assertThat(launcher.getProductSourcePaths()).contains(
        new ProductSourcePath(Paths.get("./src/main/java")
            .toAbsolutePath()));
  }

  @Test
  public void testSetProductSourcePathsAndGetProductSourcePaths02() {
    launcher.setProductSourcePaths("./src/main/java");
    launcher.setProductSourcePaths("./src/main/kotlin");
    launcher.setProductSourcePaths("./src/main/resources");
    assertThat(launcher.getProductSourcePaths())
        .contains(new ProductSourcePath(Paths.get("./src/main/java")
            .toAbsolutePath()))
        .contains(new ProductSourcePath(Paths.get("./src/main/kotlin")
            .toAbsolutePath()))
        .contains(new ProductSourcePath(Paths.get("./src/main/resources")
            .toAbsolutePath()));
  }

  @Test
  public void testSetTestSourcePathsAndGetTestSourcePaths01() {
    launcher.setTestSourcePaths("./src/test/java");
    assertThat(launcher.getTestSourcePaths()).contains(
        new TestSourcePath(Paths.get("./src/test/java")
            .toAbsolutePath()));
  }

  @Test
  public void testSetTestSourcePathsAndGetTestSourcePaths02() {
    launcher.setTestSourcePaths("./src/test/java");
    launcher.setTestSourcePaths("./src/test/kotlin");
    launcher.setTestSourcePaths("./src/test/resources");
    assertThat(launcher.getTestSourcePaths())
        .contains(new TestSourcePath(Paths.get("./src/test/java")
            .toAbsolutePath()))
        .contains(new TestSourcePath(Paths.get("./src/test/kotlin")
            .toAbsolutePath()))
        .contains(new TestSourcePath(Paths.get("./src/test/resources")
            .toAbsolutePath()));
  }

  @Test
  public void testSetClassPathsAndGetClassPaths01() {
    launcher.setClassPaths("./lib");
    assertThat(launcher.getClassPaths())
        .contains(new ClassPath(Paths.get("./lib")
            .toAbsolutePath()));
  }

  @Test
  public void testSetClassPathsAndGetClassPaths02() {
    launcher.setClassPaths("./lib01");
    launcher.setClassPaths("./lib02");
    launcher.setClassPaths("./lib03");
    assertThat(launcher.getClassPaths())
        .contains(new ClassPath(Paths.get("./lib01")
            .toAbsolutePath()))
        .contains(new ClassPath(Paths.get("./lib02")
            .toAbsolutePath()))
        .contains(new ClassPath(Paths.get("./lib03")
            .toAbsolutePath()));
  }
}
