package jp.kusumotolab.kgenprog.output;

import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.Configuration;

/**
 * Exporterインスタンスを生成するクラス
 */
public class ExporterFactory {

  /**
   * 設定情報に応じたExporterを生成する
   *
   * @param config 設定情報
   */
  public static Exporter create(final Configuration config) {
    final PatchGenerator patchGenerator = new PatchGenerator();
    final List<Exporter> exporters = new ArrayList<>();
    exporters.add(new PatchExporter(config, patchGenerator));

    if (config.getHistoryRecord()) {
      exporters.add(new JSONExporter(config, patchGenerator));
    }

    return new Exporters(config, exporters);
  }
}
