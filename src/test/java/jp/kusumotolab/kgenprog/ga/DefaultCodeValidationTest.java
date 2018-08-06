package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class DefaultCodeValidationTest {

  @Test
  public void testExec() {
    final Path rootPath = Paths.get("example/example01");
    final Path workPath = rootPath.resolve("bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final TestProcessBuilder testProcessBuilder = new TestProcessBuilder(targetProject, workPath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Fitness fitness =
        defaultCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

    final double expected = (double) 3 / 4; // 4 tests executed and 3 tests passed.
    assertThat(fitness.getValue()).isEqualTo(expected);
  }

  @Test
  public void testExecForBuildFailure() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final Path workPath = rootPath.resolve("bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final TestProcessBuilder testProcessBuilder = new TestProcessBuilder(targetProject, workPath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Fitness fitness =
        defaultCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

    assertThat(fitness.getValue()).isNaN();
  }
}
