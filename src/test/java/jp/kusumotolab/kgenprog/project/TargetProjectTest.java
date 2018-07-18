package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TargetProjectTest {

  @Test
  public void testGenerate01() throws IOException {
    final Path basePath = Paths.get("example/example01");
    final TargetProject project = TargetProjectFactory.create(basePath);

    assertThat(project.getSourcePaths(), is(containsInAnyOrder( //
        new TargetSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")))));

    assertThat(project.getTestPaths(), is(containsInAnyOrder( //
        new TestSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
  }

  @Test
  public void testGenerate02() throws IOException {
    final Path basePath = Paths.get("example/example02");
    final TargetProject project = TargetProjectFactory.create(basePath);

    assertThat(project.getSourcePaths(), is(containsInAnyOrder( //
        new TargetSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculator.java")), //
        new TargetSourcePath(basePath.resolve("src/jp/kusumotolab/Util.java")))));

    assertThat(project.getTestPaths(), is(containsInAnyOrder( //
        new TestSourcePath(basePath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")), //
        new TestSourcePath(basePath.resolve("src/jp/kusumotolab/UtilTest.java")))));

  }
}
