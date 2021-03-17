package jp.kusumotolab.kgenprog.output;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import org.junit.Test;
import com.google.gson.Gson;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class HistoricalElementSerializerTest {

  private final Gson gson = TestUtil.createGson();

  @Test
  public void testOriginalHistoricalElement() {
    final OriginalHistoricalElement originalHistoricalElement = new OriginalHistoricalElement();
    final String serializedOriginalHistoricalElement = gson.toJson(originalHistoricalElement);

    assertThatJson(serializedOriginalHistoricalElement).isObject()
        .containsOnlyKeys(JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS,
            JsonKeyAlias.CrossoverHistoricalElement.NAME);
    assertThatJson(serializedOriginalHistoricalElement).node(
        JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS)
        .isArray()
        .isEmpty();
    assertThatJson(serializedOriginalHistoricalElement).node(
        JsonKeyAlias.CrossoverHistoricalElement.NAME)
        .isString()
        .isEmpty();
  }
}
