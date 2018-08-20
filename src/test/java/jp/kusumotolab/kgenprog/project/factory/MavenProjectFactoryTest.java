package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Paths;
import org.junit.Test;

public class MavenProjectFactoryTest {

  @Test
  public void create() {
    final MavenProjectFactory mavenProjectFactory = new MavenProjectFactory(
        Paths.get(""));
    final TargetProject targetProject = mavenProjectFactory.create();
  }
}