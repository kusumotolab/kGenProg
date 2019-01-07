package example;

import static org.junit.Assert.assertEquals;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() {
    final Foo foo = new Foo();

    // working-dirからの相対パスでPathを取り出す．
    // KGPテスト実行時に別プロセス切り出しが難しいので，擬似的に題材ルートからの相対パスを用いる．
    final Path path = Paths.get(System.getProperty("user.dir"), "resources/in.txt");

    assertEquals(foo.read(path), "Hello World");
  }
}
