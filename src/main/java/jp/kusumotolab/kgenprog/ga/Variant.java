package jp.kusumotolab.kgenprog.ga;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Variant {

  private static Logger log = LoggerFactory.getLogger(Variant.class);

  private final int generationNumber;
  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;
  
  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    result = result * prime + generationNumber;
    result = result * prime + generatedSourceCode.hashCode();
    result = result * prime + testResults.hashCode();
    result = result * prime + fitness.hashCode();
    result = result * prime + suspiciousnesses.hashCode();

    return result;
  }

  public Variant(final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final TestResults testResults,
      final Fitness fitness, final List<Suspiciousness> suspiciousnesses,
      final HistoricalElement historicalElement) {
    this.generationNumber = generationNumber;
    this.gene = gene;
    this.generatedSourceCode = generatedSourceCode;
    this.testResults = testResults;
    this.fitness = fitness;
    this.suspiciousnesses = suspiciousnesses;
    this.historicalElement = historicalElement;
  }

  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  public OrdinalNumber getGenerationNumber() {
    log.debug("enter getGenerationNumberF()");
    return new OrdinalNumber(generationNumber);
  }

  public Gene getGene() {
    log.debug("enter getGene()");
    return gene;
  }

  public GeneratedSourceCode getGeneratedSourceCode() {
    log.debug("enter getGeneratedSourceCode()");
    return generatedSourceCode;
  }

  public TestResults getTestResults() {
    log.debug("enter getTestResults()");
    return testResults;
  }

  public Fitness getFitness() {
    log.debug("enter getFitness()");
    return fitness;
  }

  public List<Suspiciousness> getSuspiciousnesses() {
    log.debug("enter getSuspiciousnesses");
    return suspiciousnesses;
  }

  public HistoricalElement getHistoricalElement() {
    log.debug("enter getHistoricalElement");
    return historicalElement;
  }

  public JsonElement exportJson(final JsonArray serializedVariants, final JsonArray serializedEdges,
      final int variantCount, final int edgeCount) {

    final String variantId = "v" + variantCount;

    final JsonObject serializedVariant = new JsonObject();
//    serializedVariant.addProperty();
    serializedVariant.addProperty("generation_number", generationNumber);
    serializedVariant.addProperty("fitness", fitness.getValue());
//    serializedVariant.addProperty();

    return null;
  }

  /**
   *
   * */
  private JsonObject createNode(final int variantCount) {
    final String variantId = "v" + variantCount;

    final JsonObject serializedVariant = new JsonObject();
    serializedVariant.addProperty("id", variantId);
    serializedVariant.addProperty("generation_number", generationNumber);
    serializedVariant.addProperty("fitness", fitness.getValue());
    return null;
  }

}
