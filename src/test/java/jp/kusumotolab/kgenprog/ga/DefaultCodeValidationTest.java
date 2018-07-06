package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final TestProcessBuilder testProcessBuilder = new TestProcessBuilder(targetProject, outDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Fitness fitness =
        defaultCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);
    assertThat(fitness.getValue(), is(closeTo(0.75, 0.000001)));
  }

  @Test
  public void testExecForBuildFailure() {
    final Path rootDir = Paths.get("example/example00");
    final Path outDir = rootDir.resolve("_bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final TestProcessBuilder testProcessBuilder = new TestProcessBuilder(targetProject, outDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Fitness fitness =
        defaultCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

    assertThat(fitness.getValue(), is(Double.NaN));
  }
}
