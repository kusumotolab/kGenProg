package jp.kusumotolab.kgenprog.project;

import static org.junit.Assert.assertEquals;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TargetProjectFactoryTest {

  @Test
  public void testCreate01() {
    final Path rootPath = Paths.get("./example/example01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    assertEquals(project.rootPath, rootPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreate05() {
    final Path rootPath = Paths.get("./example/example01xxxxxxxxx");
    TargetProjectFactory.create(rootPath);
  }
}
