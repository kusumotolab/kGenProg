package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
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

  private Configuration buildConfiguration(final Path outDir, final boolean isForce) {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final List<Path> productPaths = Collections.singletonList(rootPath.resolve(PRODUCT_NAME));
    final List<Path> testPaths = Collections.singletonList(rootPath.resolve(TEST_NAME));

    return new Configuration.Builder(rootPath, productPaths, testPaths).setOutDir(outDir)
        .setIsForce(isForce)
        .setTestTimeLimitSeconds(1)
        .setMaxGeneration(1)
        .setRequiredSolutionsCount(1)
        .build();
  }

  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが有効
   * </ul>
   * <ul>
   * outDirが空でないディレクトリ
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithForceOptionAndNonEmptyOutdir() throws IOException {
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

    final Configuration config = buildConfiguration(outDir, true);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // 以前に作成したファイル群が存在しないことを確認
    assertThat(childDir).doesNotExist();
    assertThat(childFile).doesNotExist();
    assertThat(grandChildFile).doesNotExist();
    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }

  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが有効
   * </ul>
   * <ul>
   * outDirが空のディレクトリ
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithForceOptionAndEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);

    final Configuration config = buildConfiguration(outDir, true);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }


  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが無効
   * </ul>
   * <ul>
   * outDirが空でないディレクトリ
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithNonEmptyOutdir() throws IOException {
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

    final Configuration config = buildConfiguration(outDir, false);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // 以前に作成したファイル群の存在を確認
    assertThat(childDir).exists();
    assertThat(childFile).exists();
    assertThat(grandChildFile).exists();
    // .patchと.jsonが存在しないことを確認
    assertThat(fooPatch).doesNotExist();
    assertThat(historyJson).doesNotExist();
    // variant1が存在しないことを確認
    assertThat(variantDir).doesNotExist();
    // .diffと.javaが存在しないことを確認
    assertThat(fooJava).doesNotExist();
    assertThat(fooDiff).doesNotExist();
  }


  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが無効
   * </ul>
   * <ul>
   * outDirが空のディレクトリ
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithEmptyOutdir() throws IOException {
    final Path outDir = tempDir.resolve("out");
    // outDirを作る
    Files.createDirectory(outDir);

    final Configuration config = buildConfiguration(outDir, false);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }

  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが有効
   * </ul>
   * <ul>
   * outDirが存在しない
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithForceOptionAndNonExistentOutdir() {
    final Path outDir = tempDir.resolve("out");
    final Configuration config = buildConfiguration(outDir, true);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }


  /**
   * 以下の条件下でパッチとJSONを出力するか確認する
   * <li>
   * <ul>
   * forceオプションが無効
   * </ul>
   * <ul>
   * outDirが存在しない
   * </ul>
   * </li>
   */
  @Test
  public void testExporterWithNonExistentOutDir() {
    final Path outDir = tempDir.resolve("out");
    final Configuration config = buildConfiguration(outDir, false);
    final VariantStore variantStore = TestUtil.createVariantStoreWithDefaultStrategies(config);
    // variantを1個作る = パッチは1つ生成される
    variantStore.addGeneratedVariant(createModifiedVariant(variantStore.getInitialVariant()));

    final Exporter exporter = new Exporter(config);
    exporter.export(variantStore, patchGenerator);

    final Path variantDir = outDir.resolve("variant1");
    final Path fooPatch = outDir.resolve("variant1.patch");
    final Path historyJson = outDir.resolve("history.json");
    final Path fooJava = variantDir.resolve("example.Foo.java");
    final Path fooDiff = variantDir.resolve("example.Foo.diff");

    // outDirの存在を確認
    assertThat(outDir).exists();
    // .patchと.jsonの存在を確認
    assertThat(fooPatch).exists();
    assertThat(historyJson).exists();
    // variant1の存在を確認
    assertThat(variantDir).exists();
    // .diffと.javaの存在を確認
    assertThat(fooJava).exists();
    assertThat(fooDiff).exists();
  }
}
