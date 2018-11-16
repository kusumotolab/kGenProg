package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class TargetFullyQualifiedNameTest {

  @Test
  public void testGetPackage() {
    final TargetFullyQualifiedName fullyQualifiedName = new TargetFullyQualifiedName(
        "jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName");
    final String expected = "jp.kusumotolab.kgenprog.project";
    final String result = fullyQualifiedName.getPackageName();
    assertThat(result).isEqualTo(expected);
  }
}
