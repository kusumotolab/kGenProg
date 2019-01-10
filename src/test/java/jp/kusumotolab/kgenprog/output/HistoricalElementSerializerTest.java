package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class HistoricalElementSerializerTest {

  private Gson gson;

  @Before
  public void setup() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .create();
  }

  @Test
  public void testOriginalHistoricalElement() {
    final OriginalHistoricalElement originalHistoricalElement = new OriginalHistoricalElement();
    final JsonObject serializedOriginalHistoricalElement = gson.toJsonTree(
        originalHistoricalElement)
        .getAsJsonObject();

    // キーの存在チェック

    final Set<String> serializedOperationKey = serializedOriginalHistoricalElement.keySet();
    assertThat(serializedOperationKey).containsOnly(
        JsonKeyAlias.HistoricalElement.PARENT_IDS,
        JsonKeyAlias.HistoricalElement.NAME);

    // 親IDのチェック
    final JsonArray serializedParentIds = serializedOriginalHistoricalElement.get(
        JsonKeyAlias.HistoricalElement.PARENT_IDS)
        .getAsJsonArray();
    assertThat(serializedParentIds).hasSize(0);

    // 操作名のチェック
    final String operationName = serializedOriginalHistoricalElement.get(
        JsonKeyAlias.HistoricalElement.NAME)
        .getAsString();
    assertThat(operationName).isBlank();
  }
}
