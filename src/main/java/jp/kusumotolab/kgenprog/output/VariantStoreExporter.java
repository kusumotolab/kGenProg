package jp.kusumotolab.kgenprog.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.kusumotolab.kgenprog.ga.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class VariantStoreExporter {

  private final static Logger log = LoggerFactory.getLogger(VariantStoreExporter.class);

  public void writeToFile(final Path outDir, final VariantStore variantStore) {

    final Path outputPath = createOutputPath(outDir, variantStore);
    final Gson gson = createGson();

    try (final BufferedWriter out = Files.newBufferedWriter(outputPath)) {
      if (Files.notExists(outDir)) {
        Files.createDirectories(outDir);
      }
      gson.toJson(variantStore, out);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private Path createOutputPath(final Path outDir, final VariantStore variantStore) {
    final LocalDateTime currentTime = LocalDateTime.now();
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    final String formattedCurrentTime = dateTimeFormatter.format(currentTime);
    final String projectName = variantStore.getTargetProject().rootPath.getFileName()
        .toString();

    final String fileName = projectName + "_" + formattedCurrentTime + ".json";
    return outDir.resolve(fileName);
  }

  private Gson createGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    return gsonBuilder.registerTypeAdapter(VariantStore.class, new VariantStoreSerializer())
        .registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeAdapter(FileDiff.class, new FileDiffSerializer())
        .create();
  }
}
