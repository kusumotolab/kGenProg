package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.Configuration;

public class ExporterFactoryTest {

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * needHistoricalElementオプションが有効なときに，PatchExporterとJSONExporterの生成ができているか確認する
   */
  @Test
  public void testCreateWithNeedHistoricalElement()
      throws NoSuchFieldException, IllegalAccessException {
    final Configuration config = buildConfiguration(true);
    final Exporter exporter = ExporterFactory.create(config);

    final List<Exporter> exporters = getExporters(exporter);
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
  public void testCreateWithoutNeedHistoricalElement()
      throws NoSuchFieldException, IllegalAccessException {
    final Configuration config = buildConfiguration(false);
    final Exporter exporter = ExporterFactory.create(config);

    final List<Exporter> exporters = getExporters(exporter);
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
        .setHistoryRecord(needHistoricalElement)
        .build();
  }

  @SuppressWarnings("unchecked")
  private List<Exporter> getExporters(final Exporter exporter)
      throws NoSuchFieldException, IllegalAccessException {
    // リフレクションで private フィールドを取得する
    final Class<? extends Exporter> clazz = exporter.getClass();
    final Field field = clazz.getDeclaredField("exporters");
    field.setAccessible(true);
    return (List<Exporter>) field.get(exporter);
  }
}
