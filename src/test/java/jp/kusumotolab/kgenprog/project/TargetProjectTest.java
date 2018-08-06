package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TargetProjectTest {

  private final String bc = "src/jp/kusumotolab/BuggyCalculator.java";
  private final String bct = "src/jp/kusumotolab/BuggyCalculatorTest.java";
  private final String ut = "src/jp/kusumotolab/Util.java";
  private final String utt = "src/jp/kusumotolab/UtilTest.java";

  @Test
  public void testGenerate01() throws IOException {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath bcPath = new ProductSourcePath(rootPath.resolve(bc));
    final TestSourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(bcPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(bctPath);
  }

  @Test
  public void testGenerate02() throws IOException {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath bcPath = new ProductSourcePath(rootPath.resolve(bc));
    final TestSourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));
    final ProductSourcePath utPath = new ProductSourcePath(rootPath.resolve(ut));
    final TestSourcePath uttPath = new TestSourcePath(rootPath.resolve(utt));

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(bcPath, utPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(bctPath, uttPath);
  }
}
