package jp.kusumotolab.kgenprog.fl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class OchiaiTest {

  @Test
  public void testForExample01() {
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    suspeciousnesses.sort(comparing(Suspiciouseness::getValue, reverseOrder()));

    assertThat(suspeciousnesses).extracting("value")
        .containsExactly(0.7071067811865475d, 0.5d, 0.5d, 0.5d);
  }

  @Test
  public void testForExample02() {
    final Path rootDir = Paths.get("example/example02");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    suspeciousnesses.sort(comparing(Suspiciouseness::getValue, reverseOrder()));

    assertThat(suspeciousnesses).extracting("value")
        .containsExactly(0.7071067811865475d, 0.5d, 0.5d, 0.5d);
  }

  @Test
  public void testForFailedProject() throws IOException {
    final Path rootDir = Paths.get("example/example00");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    assertThat(suspeciousnesses).isEmpty();
  }

}
