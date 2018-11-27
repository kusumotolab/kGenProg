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
import jp.kusumotolab.kgenprog.ga.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class VariantStoreExporter {

  private final static Logger log = LoggerFactory.getLogger(VariantStoreExporter.class);

  public void writeToFile(final Configuration config, final VariantStore variantStore) {

    final Path outputPath = config.getOutDir()
        .resolve("history.json");
    final Gson gson = createGson(config);

    try (final BufferedWriter out = Files.newBufferedWriter(outputPath)) {
      if (Files.notExists(config.getOutDir())) {
        Files.createDirectories(config.getOutDir());
      }
      gson.toJson(variantStore, out);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private Gson createGson(final Configuration config) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    return gsonBuilder.registerTypeAdapter(VariantStore.class, new VariantStoreSerializer(config))
        .registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeAdapter(FileDiff.class, new FileDiffSerializer())
        .create();
  }
}
