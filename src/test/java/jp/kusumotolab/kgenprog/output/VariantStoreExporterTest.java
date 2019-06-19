package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class VariantStoreExporterTest {

  private Path outDir;

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    outDir = tempFolder.getRoot()
        .toPath();
  }

  @Test
  public void testWriteToFile() {
    // 適当なTargetProjectを作る
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(project).setOutDir(outDir)
        .build();

    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();
    variantStoreExporter.writeToFile(config, variantStore);

    // 出力ファイルの存在をチェック
    final Path exportedJsonFile = outDir.resolve("history.json");
    assertThat(exportedJsonFile).exists();

    // TODO
    // ファイルの中身をチェック
  }

}
