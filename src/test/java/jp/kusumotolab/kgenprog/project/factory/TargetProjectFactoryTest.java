package jp.kusumotolab.kgenprog.project.factory;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

public class TargetProjectFactoryTest {

  @Test
  public void testCreate01() {
    final Path rootPath = Paths.get("./example/example01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    assertEquals(project.rootPath, rootPath);
    assertThat(project.getSourceFiles(), is(containsInAnyOrder(
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getTestFiles(), is(containsInAnyOrder(
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getClassPaths(), is(containsInAnyOrder(//
        new ClassPath(Paths.get("lib/junit4/junit-4.12.jar")),
        new ClassPath(Paths.get("lib/junit4/hamcrest-core-1.3.jar")))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreate05() {
    final Path rootPath = Paths.get("./example/example01xxxxxxxxx");
    TargetProjectFactory.create(rootPath);
  }
}
