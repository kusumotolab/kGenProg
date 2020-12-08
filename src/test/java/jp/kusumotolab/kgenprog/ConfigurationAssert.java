package jp.kusumotolab.kgenprog;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ConfigurationAssert extends AbstractAssert<ConfigurationAssert, Configuration> {

  public ConfigurationAssert(final Configuration actual) {
    super(actual, ConfigurationAssert.class);
  }

  public static ConfigurationAssert assertThat(final Configuration actual) {
    return new ConfigurationAssert(actual);
  }

  public ConfigurationAssert isEqualToRecursivelyIgnoringGivenFields(
      final Configuration subject,
      final String... ignoredFieldNames) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("builder") // always exclude "builder" field
        .ignoringFields(ignoredFieldNames)
        .isEqualTo(subject);
    return this;
  }
}