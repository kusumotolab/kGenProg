package jp.kusumotolab.kgenprog.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.jdt.core.dom.ASTNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import io.gsonfire.GsonFireBuilder;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 個体の履歴をJSONファイルに出力するクラス
 */
class JSONExporter implements Exporter {

  private static final Logger log = LoggerFactory.getLogger(JSONExporter.class);

  private final Path outdir;
  public static final String JSON_FILENAME = "hogehoge.json";

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
    return new GsonFireBuilder().enableExposeMethodResult()
        .createGsonBuilder()
        .registerTypeHierarchyAdapter(ASTNode.class, new ASTNodeSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(Fitness.class, new FitnessSerializer())
        .registerTypeHierarchyAdapter(FullyQualifiedName.class, new FullyQualifiedNameSerializer())
        .registerTypeHierarchyAdapter(Gene.class, new GeneSerializer())
        .registerTypeHierarchyAdapter(GeneratedJDTAST.class, new GeneratedJDTASTSerializer())
        .registerTypeHierarchyAdapter(GeneratedSourceCode.class,
            new GeneratedSourceCodeSerializer())
        .registerTypeHierarchyAdapter(Patch.class, new PatchSerializer())
        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
        .registerTypeHierarchyAdapter(SourcePath.class, new SourcePathSerializer())
        .registerTypeHierarchyAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeHierarchyAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(VariantStore.class, new VariantStoreSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .create();
  }
}
