package jp.kusumotolab.kgenprog.testutil;

import static org.assertj.core.api.Assertions.fail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

public class TestUtil {

  public static void deleteWorkDirectory(final Path path) {
    if (Files.exists(path)) {
      try {
        FileUtils.deleteDirectory(path.toFile());
      } catch (IOException e) {
        fail("Couldn't delete work dir [" + path.toString() + "]");
      }
    }
  }

}
