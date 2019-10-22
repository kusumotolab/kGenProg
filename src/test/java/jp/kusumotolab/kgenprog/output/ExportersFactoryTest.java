package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.Configuration;

public class ExportersFactoryTest {

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * needHistoricalElementオプションが有効なときに，PatchExporterとJSONExporterの生成ができているか確認する
   */
  @Test
  public void testCreateWithNeedHistoricalElement() {
    final Configuration config = buildConfiguration(true);
    final List<Exporter> exporters = ExportersFactory.create(config);
    final Optional<Exporter> patchExporter = exporters.stream()
        .filter(e -> e instanceof PatchExporter)
        .findFirst();
    final Optional<Exporter> jsonExporter = exporters.stream()
        .filter(e -> e instanceof JSONExporter)
        .findFirst();

    assertThat(exporters).hasSize(2);
    assertThat(patchExporter).isNotEmpty();
    assertThat(jsonExporter).isNotEmpty();
  }

  /**
   * needHistoricalElementオプションが無効なときに，PatchExporterの生成ができているか確認する
   */
  @Test
  public void testCreateWithoutNeedHistoricalElement() {
    final Configuration config = buildConfiguration(false);
    final List<Exporter> exporters = ExportersFactory.create(config);
    final Optional<Exporter> patchExporter = exporters.stream()
        .filter(e -> e instanceof PatchExporter)
        .findFirst();

    assertThat(exporters).hasSize(1);
    assertThat(patchExporter).isNotEmpty();
  }

  private Configuration buildConfiguration(final boolean needHistoricalElement) {
    final Path rootPath = tempFolder.getRoot()
        .toPath();

    return new Configuration.Builder(rootPath, Collections.emptyList(), Collections.emptyList())
        .setNeedHistoricalElement(needHistoricalElement)
        .build();
  }
}
