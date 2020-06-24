package jp.kusumotolab.kgenprog.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 個体の履歴をJSONに出力するクラス
 */
public class JSONExporter extends Exporter {

  private static final Logger log = LoggerFactory.getLogger(JSONExporter.class);

  public JSONExporter(final Configuration config) {
    super(config);
  }

  /**
   * 個体の履歴を記録したJSONをファイルに出力する．
   */
  @Override
  public void export(final VariantStore variantStore) {
    if (config.needNotOutput()) {
      return;
    }

    final Path outputFile = config.getOutDir()
        .resolve("history.json");
    final Gson gson = createGson(config);

    try {
      if (Files.notExists(config.getOutDir())) {
        Files.createDirectories(config.getOutDir());
      }

      final BufferedWriter out = Files.newBufferedWriter(outputFile);
      gson.toJson(variantStore, out);
      out.close();
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 各クラスのシリアライザを登録する．
   */
  private Gson createGson(final Configuration config) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    return gsonBuilder.registerTypeAdapter(VariantStore.class, new VariantStoreSerializer(config))
        .registerTypeHierarchyAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeHierarchyAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .registerTypeHierarchyAdapter(Patch.class, new PatchSerializer())
        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
        .create();
  }
}
