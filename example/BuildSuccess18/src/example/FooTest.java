package example;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() throws IOException {

    // working-dirからの相対パスでPathを取り出す．
    // KGPテスト実行時に別プロセス切り出しが難しいので，擬似的に題材ルートからの相対パスを用いる．
    final Path path = Paths.get(System.getProperty("user.dir"), "tmp/out.txt");

    final List<String> contents = Arrays.asList(Double.toString(Math.random()));

    new Foo().write(path, contents);

    final List<String> actualContents = Files.readAllLines(path);
    assertEquals(actualContents, contents);
  }
}
