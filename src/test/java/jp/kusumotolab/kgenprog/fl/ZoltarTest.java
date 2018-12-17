package jp.kusumotolab.kgenprog.fl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ZoltarTest {

  private final static Path WORK_PATH = Paths.get("tmp/work");

  @Before
  public void before() throws IOException {
    System.gc();
    TestUtil.deleteWorkDirectory(WORK_PATH);
  }

  @Test
  public void testForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Zoltar();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    suspiciousnesses.sort(comparing(Suspiciousness::getValue, reverseOrder()));

    final double susp1 = 1.0 / (1.0 + 0.0 + 1.0 + 10000d * 0.0 * 1.0 / 1.0); // 0.5  (the most suspicious stmt)
    final double susp2 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    final double susp3 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    final double susp4 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactly(susp1, susp2, susp3, susp4);
  }

  @Test
  public void testForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Zoltar();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    suspiciousnesses.sort(comparing(Suspiciousness::getValue, reverseOrder()));

    final double susp1 = 1.0 / (1.0 + 0.0 + 1.0 + 10000d * 0.0 * 1.0 / 1.0); // 0.5  (the most suspicious stmt)
    final double susp2 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    final double susp3 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    final double susp4 = 1.0 / (1.0 + 0.0 + 3.0 + 10000d * 0.0 * 3.0 / 1.0); // 0.25
    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactly(susp1, susp2, susp3, susp4);
  }

  @Test
  public void testForFailedProject() throws IOException {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Zoltar();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    assertThat(suspiciousnesses).isEmpty();
  }
}
