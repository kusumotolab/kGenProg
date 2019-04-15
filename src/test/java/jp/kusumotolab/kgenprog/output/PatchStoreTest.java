package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatchStoreTest {

  private Path outDir;

  @Before
  public void setUp() throws IOException {
    /*
     * ./tmp/outが存在するときは./tmp/outを削除する
     * PatchStore.writeToFile()が出力先ディレクトリが存在しないときに
     * 出力先ディレクトリを作成できているか確認するため
     **/
    outDir = Paths.get("./tmp/_out-dir-for-test");
    deleteFile(outDir);
  }

  @After
  public void tearDown() throws IOException {
    // 後始末
    deleteFile(outDir);
  }

  /**
   * 正しいパスにファイルを出力できているか確認する
   *
   * 中身の正しさはチェックしない
   */
  @Test
  public void testWriteToFile() {
    final PatchStore patchStore = new PatchStore();

    // パッチを作成
    final Patch patch = createPatch(Arrays.asList("file1", "file2"));
    patchStore.add(patch);

    // パッチを出力
    patchStore.writeToFile(outDir);

    // variant1ディレクトリがあるか確認する
    final Path variantDir = outDir.resolve("variant1");
    assertThat(variantDir).exists();

    // file1.java file2.javaがあるか確認する
    for (int i = 1; i <= 2; i++) {
      final Path javaFile = variantDir.resolve("file" + i + ".java");
      assertThat(javaFile).exists();
    }

    // file1.diff file2.diffがある確認する
    for (int i = 1; i <= 2; i++) {
      final Path diffFile = variantDir.resolve("file" + i + ".diff");
      assertThat(diffFile).exists();
    }

    // variant1.patchがあるか確認する
    final Path variantPatch = outDir.resolve("variant1.patch");
    assertThat(variantPatch).exists();
  }


  private Patch createPatch(final List<String> fileNames) {
    final Patch patch = new Patch();
    fileNames.stream()
        .map(e -> new FileDiff(Collections.emptyList(), e, Collections.emptyList(),
            Collections.emptyList()))
        .collect(Collectors.toList())
        .forEach(patch::add);

    return patch;
  }

  /**
   * ディレクトリの中身ごとディレクトリを削除する
   */
  private void deleteFile(final Path path) throws IOException {
    if (Files.notExists(path)) {
      return;
    }

    if (Files.isDirectory(path)) {
      final List<Path> subFiles = Files.list(path)
          .collect(Collectors.toList());

      for (final Path subFile : subFiles) {
        deleteFile(subFile);
      }
    }

    Files.delete(path);
  }
}
