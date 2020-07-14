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

public class JSONExporterTest {

  @Test
  public void testExport() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final Configuration config = setupMinimalConfig(outdir);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);

    final Exporter jsonExporter = new JSONExporter(outdir);
    jsonExporter.export(variantStore);

    // assert json file exists
    final Path exportedJsonFile = outdir.resolve(JSONExporter.JSON_FILENAME);
    assertThat(exportedJsonFile).exists();

    // assert json bodies
    final String contents = Files.readString(exportedJsonFile);
    assertThat(contents)
        .contains("\"projectName\":\"BuildSuccess01\"")
        .contains("\"variants\":[]")
        .contains("Foo.java")       // filename should be written in somewhere
        .contains("FooTest.java");  // filename should be written in somewhere
  }

  @Test
  public void testExportWithPreviousResult() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();

    // setup previous result
    Files.createDirectory(outdir);
    Files.createFile(outdir.resolve(JSONExporter.JSON_FILENAME));
    Files.writeString(outdir.resolve(JSONExporter.JSON_FILENAME), "this is dummy file");

    final Configuration config = setupMinimalConfig(outdir);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);

    final Exporter jsonExporter = new JSONExporter(outdir);
    jsonExporter.export(variantStore);

    // assert json file exists
    final Path exportedJsonFile = outdir.resolve(JSONExporter.JSON_FILENAME);
    assertThat(exportedJsonFile).exists();

    // assert json bodies
    final String contents = Files.readString(exportedJsonFile);
    assertThat(contents)
        .contains("\"projectName\":\"BuildSuccess01\"")
        .contains("\"variants\":[]")
        .contains("Foo.java")       // filename should be written in somewhere
        .contains("FooTest.java");  // filename should be written in somewhere
    assertThat(contents).doesNotContain("this is dummy file");
  }

  private Configuration setupMinimalConfig(final Path outdir) {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return new Configuration.Builder(targetProject)
        .setOutDir(outdir)
        .build();
  }

}
