package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ExporterTest {

  private Path tempDir;
  private final PatchGenerator patchGenerator = new PatchGenerator();
  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    tempDir = tempFolder.getRoot()
        .toPath();
  }

  /**
   * 1箇所でも差分があるようなVariantを1つ作る
   */
  private Variant createModifiedVariant(final Variant parent) {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location =
        new JDTASTLocation(new ProductSourcePath(basePath, Src.FOO), statement, ast);

    final DeleteOperation operation = new DeleteOperation();
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final TestResults testResults = new EmptyTestResults("for testing.");
    final Base base = new Base(location, operation);
    final Gene gene = new Gene(Collections.singletonList(base));
    final Fitness fitness = new SimpleFitness(1.0d);
    final HistoricalElement historicalElement = new MutationHistoricalElement(parent, base);

    return new Variant(0, 0, gene, code, testResults, fitness, null, historicalElement);
  }

  private Configuration buildConfiguration(final Path outDir) {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final List<Path> productPaths = Collections.singletonList(rootPath.resolve(PRODUCT_NAME));
    final List<Path> testPaths = Collections.singletonList(rootPath.resolve(TEST_NAME));

    return new Configuration.Builder(rootPath, productPaths, testPaths)
        .setOutDir(outDir)
        .setTestTimeLimitSeconds(1)
        .setMaxGeneration(1)
        .setRequiredSolutionsCount(1)
        .build();
  }

  /**
   * 出力先ディレクトリが空でないときに，出力先ディレクトリの初期化ができているか確認する
   */
  @Test
  public void testClearPreviousResultsOnNonEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);
    // 適当なファイルを作る
    final Path childDir = outDir.resolve("childDir");
    final Path childFile = outDir.resolve("child");
    final Path grandChildFile = childDir.resolve("grandChild");
    Files.createDirectory(childDir);
    Files.createFile(childFile);
    Files.createFile(grandChildFile);

    final Configuration config = buildConfiguration(outDir);
    final Exporter exporter = createExporter(config);

    // 初期化を行う
    exporter.clearPreviousResults();

    // サブファイルを集める
    final List<Path> subFiles = Files.walk(config.getOutDir(), FileVisitOption.FOLLOW_LINKS)
        .filter(e -> !e.equals(config.getOutDir()))
        .collect(Collectors.toList());

    // outDirの存在を確認
    assertThat(outDir).exists();
    // outDirが空であることを確認
    assertThat(subFiles).isEmpty();
  }

  /**
   * 出力先ディレクトリが空のときに，初期化ができているか確認する
   */
  @Test
  public void testClearPreviousResultsOnEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);

    final Configuration config = buildConfiguration(outDir);
    final Exporter exporter = createExporter(config);

    // 初期化を行う
    exporter.clearPreviousResults();

    // サブファイルを集める
    final List<Path> subFiles = Files.walk(config.getOutDir(), FileVisitOption.FOLLOW_LINKS)
        .filter(e -> !e.equals(config.getOutDir()))
        .collect(Collectors.toList());

    // outDirの存在を確認
    assertThat(outDir).exists();
    // outDirが空であることを確認
    assertThat(subFiles).isEmpty();
  }

  /**
   * 出力先ディレクトリが存在しないときに，初期化ができているか確認する
   */
  @Test
  public void testClearPreviousResultsOnNonExistentOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    final Configuration config = buildConfiguration(outDir);
    final Exporter exporter = createExporter(config);

    // 初期化を行う
    exporter.clearPreviousResults();

    // outDirが存在しないことを確認
    assertThat(outDir).doesNotExist();
  }

  /**
   * テスト用の何もしないExporterを作る
   */
  private Exporter createExporter(final Configuration config) {
    return new Exporter(config) {
      @Override
      public void export(final VariantStore variantStore) {
      }
    };
  }
}
