package jp.kusumotolab.kgenprog.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 個体の履歴をJSONファイルに出力するクラス
 */
class JSONExporter implements Exporter {

  private static final Logger log = LoggerFactory.getLogger(JSONExporter.class);

  private final Path outdir;
  public static final String JSON_FILENAME = "history.json";

  JSONExporter(final Path outdir) {
    this.outdir = outdir;
  }

  /**
   * 個体の履歴を記録したJSONをファイルに出力する．
   */
  @Override
  public void export(final VariantStore variantStore) {
    createDir(outdir);

    final Path outputFile = outdir.resolve(JSON_FILENAME);
    try (final BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
      final Gson gson = setupGson();
      gson.toJson(variantStore, writer);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 各クラスのシリアライザを登録する．
   */
  private Gson setupGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    return gsonBuilder.registerTypeAdapter(VariantStore.class, new VariantStoreSerializer())
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
