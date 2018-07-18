package jp.kusumotolab.kgenprog.fl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

    assertThat(suspeciousnesses.get(0)
        .getValue(), is(0.7071067811865475d));
    assertThat(suspeciousnesses.get(1)
        .getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(2)
        .getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(3)
        .getValue(), is(0.5d));
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

    assertThat(suspeciousnesses.get(0)
        .getValue(), is(0.7071067811865475d));
    assertThat(suspeciousnesses.get(1)
        .getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(2)
        .getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(3)
        .getValue(), is(0.5d));
  }

  @Test
  public void testForFailedProject() throws IOException {
    final Path rootDir = Paths.get("example/example00");
    final Path outDir = rootDir.resolve("bin");

    // TODO 一時的なSyserr対策．
    // そもそもコンパイルエラー時にsyserr吐かないほうが良い．
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {

      @Override
      public void write(int b) {} // 何もしないwriter
    }));

    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    assertThat(suspeciousnesses, is(empty()));

    // 後処理
    System.setErr(ps);
  }
}
