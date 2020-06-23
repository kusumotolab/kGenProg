package jp.kusumotolab.kgenprog.ga.validation;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation.Input;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class DefaultCodeValidationTest {

  @Test
  public void testExec() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Input input = new Input(null, null, initialVariant.getTestResults());
    final Fitness fitness = defaultCodeValidation.exec(input);

    final double expected = (double) 3 / 4; // 4 tests executed and 3 tests passed.
    assertThat(fitness.getSingularValue()).isEqualTo(expected);
  }

  @Test
  public void testExecForBuildFailure() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final DefaultCodeValidation defaultCodeValidation = new DefaultCodeValidation();
    final Input input = new Input(null, null, initialVariant.getTestResults());
    final Fitness fitness = defaultCodeValidation.exec(input);

    assertThat(fitness.getSingularValue()).isNaN();
  }
}
