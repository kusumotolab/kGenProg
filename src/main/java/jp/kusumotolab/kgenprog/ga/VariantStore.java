package jp.kusumotolab.kgenprog.ga;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResultSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.project.test.TestResultsSerializer;

public class VariantStore {

  private static Logger log = LoggerFactory.getLogger(VariantStore.class);

  private final TargetProject targetProject;
  private final Strategies strategies;
  private final Variant initialVariant;
  private List<Variant> currentVariants;
  private List<Variant> allVariants;
  private List<Variant> generatedVariants;
  private final List<Variant> foundSolutions;
  private final OrdinalNumber generation;

  public VariantStore(final TargetProject targetProject, final Strategies strategies) {
    this.targetProject = targetProject;
    this.strategies = strategies;

    generation = new OrdinalNumber(0);
    initialVariant = createInitialVariant();
    currentVariants = Collections.singletonList(initialVariant);
    allVariants = new LinkedList<>();
    allVariants.add(initialVariant);
    generatedVariants = new ArrayList<>();
    foundSolutions = new ArrayList<>();
    generation.incrementAndGet();
  }

  /**
   * テスト用
   */
  @Deprecated
  public VariantStore(final Variant initialVariant) {
    this.targetProject = null;
    this.strategies = null;
    this.initialVariant = initialVariant;

    currentVariants = Collections.singletonList(initialVariant);
    allVariants = new LinkedList<>();
    allVariants.add(initialVariant);
    generatedVariants = new ArrayList<>();
    foundSolutions = new ArrayList<>();
    generation = new OrdinalNumber(1);
  }

  public Variant createVariant(final Gene gene, final HistoricalElement element) {
    final GeneratedSourceCode sourceCode = strategies.execSourceCodeGeneration(this, gene);
    return createVariant(gene, sourceCode, element);
  }

  public Variant getInitialVariant() {
    return initialVariant;
  }

  public OrdinalNumber getGenerationNumber() {
    return generation;
  }

  public OrdinalNumber getFoundSolutionsNumber() {
    return new OrdinalNumber(foundSolutions.size());
  }

  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }

  public List<Variant> getGeneratedVariants() {
    return generatedVariants;
  }

  public List<Variant> getFoundSolutions() {
    return foundSolutions;
  }

  public List<Variant> getFoundSolutions(final int maxNumber) {
    final int length = Math.min(maxNumber, foundSolutions.size());
    return foundSolutions.subList(0, length);
  }

  /**
   * 引数の要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see addNextGenerationVariant(Variant)
   */
  public void addGeneratedVariants(final Variant... variants) {
    addGeneratedVariants(Arrays.asList(variants));
  }

  /**
   * リストの要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see addNextGenerationVariant(Variant)
   */
  public void addGeneratedVariants(final Collection<? extends Variant> variants) {
    variants.forEach(this::addGeneratedVariant);
  }

  /**
   * 引数を次世代のVariantとして追加する {@code variant.isCompleted() == true} の場合，foundSolutionとして追加され次世代のVariantには追加されない
   *
   * @param variant
   */
  public void addGeneratedVariant(final Variant variant) {
    log.debug("enter addNextGenerationVariant(Variant)");

    allVariants.add(variant);
    if (variant.isCompleted()) {
      foundSolutions.add(variant);
      log.info("{} solution has been found", getFoundSolutionsNumber());
    } else {
      generatedVariants.add(variant);
    }
  }

  /**
   * VariantSelectionを実行し世代交代を行う
   *
   * currentVariantsおよびgeneratedVariantsから次世代のVariantsを選択し，それらを次のcurrentVariantsとする
   * また，generatedVariantsをclearする
   */
  public void changeGeneration() {
    log.debug("enter changeGeneration()");

    final List<Variant> nextVariants =
        strategies.execVariantSelection(currentVariants, generatedVariants);
    nextVariants.forEach(Variant::incrementSelectionCount);
    generation.incrementAndGet();
    log.info("exec selection. {} variants: ({}, {}) => {}", generation, currentVariants.size(),
        generatedVariants.size(), nextVariants.size());

    currentVariants = nextVariants;
    generatedVariants = new ArrayList<>();
  }

  private Variant createInitialVariant() {
    final GeneratedSourceCode sourceCode = strategies.execASTConstruction(targetProject);
    return createVariant(new Gene(Collections.emptyList()), sourceCode,
        new OriginalHistoricalElement());
  }

  private Variant createVariant(final Gene gene, final GeneratedSourceCode sourceCode,
      final HistoricalElement element) {
    final TestResults testResults = strategies.execTestExecutor(sourceCode);
    final Fitness fitness = strategies.execSourceCodeValidation(this, testResults);
    final List<Suspiciousness> suspiciousnesses =
        strategies.execFaultLocalization(sourceCode, testResults);
    return new Variant(generation.get(), gene, sourceCode, testResults, fitness, suspiciousnesses,
        element);
  }

  public void writeToFile(final Path outDir) {

    final Path outputPath = createOutputPath(outDir);
    final Gson gson = createGson();

    try (final BufferedWriter out = Files.newBufferedWriter(outputPath)) {
      if (Files.notExists(outDir)) {
        Files.createDirectories(outDir);
      }
      gson.toJson(this, out);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private Path createOutputPath(final Path outDir) {
    final LocalDateTime currentTime = LocalDateTime.now();
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    final String formattedCurrentTime = dateTimeFormatter.format(currentTime);
    final String projectName = targetProject.rootPath.getFileName()
        .toString();

    final String fileName = projectName + "_" + formattedCurrentTime + ".json";
    return outDir.resolve(fileName);
  }

  private Gson createGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    return gsonBuilder.registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeAdapter(VariantStore.class, new VariantStoreSerializer())
        .setPrettyPrinting()
        .create();
  }

  private class VariantStoreSerializer implements JsonSerializer<VariantStore> {

    @Override
    public JsonElement serialize(final VariantStore variantStore, final Type type,
        final JsonSerializationContext context) {

      final JsonObject serializedVariantStore = new JsonObject();
      final TargetProject targetProject = variantStore.targetProject;
      final String projectName = (targetProject != null) ? targetProject.rootPath.getFileName()
          .toString() : "";
      final JsonElement serializedVariants = context.serialize(variantStore.allVariants);

      serializedVariantStore.addProperty("projectName", projectName);
      serializedVariantStore.add("variants", serializedVariants);

      return serializedVariantStore;
    }
  }
}
