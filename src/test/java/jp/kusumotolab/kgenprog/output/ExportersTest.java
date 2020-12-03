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
    Files.createFile(outdir.resolve("x/xxx"));

    final Configuration config = setupMinimalConfig(outdir);
    new Exporters(config);

    // outdir should be deleted by the instantiation
    assertThat(outdir).doesNotExist();
  }

  @Test
  public void testExportAll() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);

    // no output dir exists by default
    assertThat(outdir).doesNotExist();
  }

  @Test
  public void testExportAllWithHistoryRecord() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, false, true);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);

    // assert only file exists (contents should be tested in each exporter test)
    assertThat(outdir).exists();
    assertThat(outdir.resolve(JSONExporter.JSON_FILENAME)).exists();
  }

  @Test
  public void testExportAllWithPatchOutput() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, true, false);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);
    assertThat(outdir).exists();
    assertThat(outdir.resolve(JSONExporter.JSON_FILENAME)).doesNotExist();
  }

  @Test
  public void testExportAllWithBothOutput() {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir, true, true);
    final Exporters exporters = new Exporters(config);
    final VariantStore store = TestUtil.createVariantStoreWithDefaultStrategies(config);

    exporters.exportAll(store);
    assertThat(outdir).exists();
    assertThat(outdir.resolve(JSONExporter.JSON_FILENAME)).exists();
  }


  private Configuration setupMinimalConfig(final Path outdir) {
    return setupMinimalConfig(outdir, false, false);
  }

  private Configuration setupMinimalConfig(final Path outdir,
      final boolean isPatchOutput,
      final boolean isHistoryRecord) {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return new Configuration.Builder(targetProject)
        .setPatchOutput(isPatchOutput)
        .setHistoryRecord(isHistoryRecord)
        .setOutDir(outdir)
        .build();
  }

}
