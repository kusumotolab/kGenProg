package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class VariantStoreExporterTest {

  @Test
  public void testWriteToFile() throws IOException {
    /*
     * ./tmp/outが存在するときは./tmp/outを削除する
     * VariantExporter.writeToFile()が出力先ディレクトリが存在しないときに
     * 出力先ディレクトリを作成できている確認するため
     **/
    final Path outDir = Paths.get("./tmp/_out-dir-for-test");
    deleteFile(outDir);

    // 適当なTargetProjectを作る
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(project)
        .setOutDir(outDir)
        .build();

    final VariantStore variantStore = new VariantStore(TestUtil.createVariant(config));
    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();
    variantStoreExporter.writeToFile(config, variantStore);

    // 出力ファイルの存在をチェック
    final Path exportedJsonFile = outDir.resolve("history.json");
    assertThat(exportedJsonFile).exists();

    // 後始末
    deleteFile(outDir);
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
