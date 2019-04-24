package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class PatchStoreTest {

  private Path outDir;

  @Before
  public void setUp() throws IOException {
    /**
     * ./tmp/outが存在するときは./tmp/outを削除する
     * PatchStore.writeToFile()が出力先ディレクトリが存在しないときに
     * 出力先ディレクトリを作成できているか確認するため
     */
    outDir = Paths.get("./tmp/_out-dir-for-test");
    TestUtil.deleteDirectory(outDir);
  }

  @After
  public void tearDown() throws IOException {
    // 後始末
    TestUtil.deleteDirectory(outDir);
  }

  /**
   * 正しいパスにファイルを出力できているか確認する
   *
   * 中身の正しさはチェックしない
   */
  @Test
  public void testWriteToFile() {
    final PatchStore patchStore = new PatchStore();

    // パッチを作成・出力
    final Patch patch = createPatch(Arrays.asList("file1", "file2"));
    patchStore.add(patch);
    patchStore.writeToFile(outDir);

    final Path variantDir = outDir.resolve("variant1");
    final Path javaFile1 = variantDir.resolve("file1.java");
    final Path javaFile2 = variantDir.resolve("file2.java");
    final Path diffFile1 = variantDir.resolve("file1.diff");
    final Path diffFile2 = variantDir.resolve("file2.diff");
    final Path patchFile = outDir.resolve("variant1.patch");

    // variant1ディレクトリがあるか確認する
    assertThat(variantDir).exists();
    // variant1.patchがあるか確認する
    assertThat(patchFile).exists();
    // file1.java file2.javaがあるか確認する
    assertThat(javaFile1).exists();
    assertThat(javaFile2).exists();
    // file1.diff file2.diffがある確認する
    assertThat(diffFile1).exists();
    assertThat(diffFile2).exists();
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
}
