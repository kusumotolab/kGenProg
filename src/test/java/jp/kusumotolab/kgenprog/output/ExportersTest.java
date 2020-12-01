package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ExportersTest {

  @Test
  public void testInstanciationWithPreviousResults() throws IOException {
    // setup previous results which contains some files and a sub-directory
    final Path outdir = TestUtil.createVirtualDir();
    Files.createDirectory(outdir);
    Files.createFile(outdir.resolve("a.txt"));
    Files.createFile(outdir.resolve("z.txt"));
    Files.createDirectory(outdir.resolve("x"));
    Files.createFile(outdir.resolve("x")
        .resolve("xxx"));

    final Configuration config = setupMinimalConfig(outdir);
    final Exporters exporters = new Exporters(config);

    // outdir should be deleted by the instanciation
    assertThat(outdir).doesNotExist();
  }

  @Test
  public void testExportAll() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);

    // assert only file exists (contents should be tested in each exporter test)
    assertThat(outdir).exists();
    assertThat(outdir.resolve(JSONExporter.JSON_FILENAME)).exists();
  }

  @Test
  public void testExportAllWithNoHistoryRecord() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, false, false);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);

    // assert only file exists (contents should be tested in each exporter test)
    assertThat(outdir).exists();
    assertThat(outdir.resolve(JSONExporter.JSON_FILENAME)).doesNotExist();
  }

  @Test
  public void testExportAllWithNoOutput01() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, true, false);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);
    assertThat(outdir).doesNotExist(); // asssert no file output
  }

  @Test
  public void testExportAllWithNoOutput02() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, true, true);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);
    assertThat(outdir).exists(); // outdir should be created by historyrecord
  }

  private Configuration setupMinimalConfig(final Path outdir) {
    return setupMinimalConfig(outdir, false, true);
  }

  private Configuration setupMinimalConfig(final Path outdir,
      final boolean isNoOutput,
      final boolean isHistoryRecord) {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return new Configuration.Builder(targetProject)
        .setNeedNotOutput(isNoOutput)
        .setHistoryRecord(isHistoryRecord)
        .setOutDir(outdir)
        .build();
  }

}
