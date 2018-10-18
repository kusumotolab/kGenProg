package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class MetricValidationTest {

  @Test
  public void testExec() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final MetricValidation validation = new MetricValidation();
    final Fitness fitness =
        validation.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    final double expected = 0.5;
    assertThat(fitness.getValue()).isCloseTo(expected, within(0.001));
  }
}