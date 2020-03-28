package jp.kusumotolab.kgenprog.fl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

/**
 * Jaccardに関するテストクラス.
 */
public class JaccardTest {

  /**
   * BuildSuccess01によるテストを実行する.
   */
  @Test
  public void testForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Jaccard();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    suspiciousnesses.sort(comparing(Suspiciousness::getValue, reverseOrder()));

    final double susp1 = 1.0 / (1.0 + 0.0 + 1.0); // 0.50 (the most suspicious stmt)
    final double susp2 = 1.0 / (1.0 + 0.0 + 3.0); // 0.25
    final double susp3 = 1.0 / (1.0 + 0.0 + 3.0); // 0.25
    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactly(susp1, susp2, susp3);
  }

  /**
   * BuildSuccess02によるテストを実行する.
   */
  @Test
  public void testForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Jaccard();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    suspiciousnesses.sort(comparing(Suspiciousness::getValue, reverseOrder()));

    final double susp1 = 1.0 / (1.0 + 0.0 + 1.0); // 0.50 (the most suspicious stmt)
    final double susp2 = 1.0 / (1.0 + 0.0 + 3.0); // 0.25
    final double susp3 = 1.0 / (1.0 + 0.0 + 3.0); // 0.25
    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactly(susp1, susp2, susp3);
  }

  /**
   * ビルドに失敗する題材(BuildFailure01)によるテストを実行する.
   * <br>
   * 疑惑値のリストが空になることを期待する.
   */
  @Test
  public void testForFailedProject() throws IOException {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new Jaccard();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    assertThat(suspiciousnesses).isEmpty();
  }
}
