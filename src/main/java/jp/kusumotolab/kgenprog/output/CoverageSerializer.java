package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.util.stream.IntStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.Coverage;

/**
 * Coverageのシリアライザ．
 * デバック用
 */
public class CoverageSerializer implements JsonSerializer<Coverage> {

  @Override
  public JsonElement serialize(final Coverage src, final Type type,
      final JsonSerializationContext context) {
    final int[] statuses = IntStream.range(0, src.getStatusesSize())
        .mapToObj(src::getStatus)
        .mapToInt(Enum::ordinal)
        .toArray();
    src.getExecutedTargetFQN();

    final JsonObject serializedCoverage = new JsonObject();
    serializedCoverage.add("coverages", context.serialize(statuses));
    serializedCoverage.add("executedTargetFQN", context.serialize(src.getExecutedTargetFQN()));
    return serializedCoverage;
  }
}
