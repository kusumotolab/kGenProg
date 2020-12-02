package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class PatchFileExporterTest {

  private static final Charset SHIFT_JIS = Charset.forName("shift-jis");

  @Test
  public void testExport() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCode();

    // modify single source code
    final Variant variant = createModifiedVariant(base, "Foo.java", "n--;", "n -= 1; //", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    // assert contents of patch folder
    final Path outVariantDir = outdir.resolve("patch-v0");
    assertThat(outVariantDir).exists();
    assertThat(Files.readString(outVariantDir.resolve("example.Foo.java"))).contains(
        "public int foo")
        .doesNotContain("n--;")
        .contains("n -= 1; //");
    assertThat(Files.readString(outVariantDir.resolve("example.Foo.diff"))).contains(
        "public int foo")
        .contains("--- example.Foo")
        .contains("+++ example.Foo")
        .containsPattern("@@ -\\d+,\\d+ \\+\\d+,\\d+ @@")
        .containsPattern("- +n--;")
        .containsPattern("\\+ +n -= 1; //");
  }

  @Test
  public void testExportWithMultipleModificationsForSingleFile() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCode();

    // multiple replacement for single file
    final Variant variant = createModifiedVariant(base, "Foo.java", ";", "; //", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(outdir.resolve("patch-v0")).exists();
    assertThat(Files.readString(outdir.resolve("patch-v0/example.Foo.java")))
        .contains("public int foo")
        .contains("n--; //")
        .contains("n++; //")
        .contains("return n; //");
    assertThat(Files.readString(outdir.resolve("patch-v0/example.Foo.diff")))
        .contains("public int foo")
        .containsPattern("- +n--;")
        .containsPattern("\\+ +n--; //")
        .containsPattern("- +n\\+\\+;")
        .containsPattern("\\+ +n\\+\\+; //")
        .containsPattern("- +return n;")
        .containsPattern("\\+ +return n; //");
  }

  @Test
  public void testExportWithMultipleModificationsForMultipleFiles() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCode();

    // modify two source code files
    final Variant v1 = createModifiedVariant(base, "Foo.java", "n--;", "n -= 1;", 0);
    final Variant v2 = createModifiedVariant(base, "Bar.java", "return n + 1;", "return n;", 99);
    final VariantStore variantStore = createMockedVariantStore(v1, v2);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(outdir.resolve("patch-v0")).exists();
    assertThat(Files.readString(outdir.resolve("patch-v0/example.Foo.java")))
        .doesNotContain("n--;")
        .contains("n -= 1;");
    assertThat(Files.readString(outdir.resolve("patch-v0/example.Foo.diff")))
        .containsPattern("- +n--;")
        .containsPattern("\\+ +n -= 1;");

    assertThat(outdir.resolve("patch-v99")).exists();
    assertThat(Files.readString(outdir.resolve("patch-v99/example.Bar.java")))
        .doesNotContain("return n + 1;")
        .contains("return n;");
    assertThat(Files.readString(outdir.resolve("patch-v99/example.Bar.diff")))
        .containsPattern("- +return n \\+ 1;")
        .containsPattern("\\+ +return n;");
  }

  @Test
  public void testExportWithManyVariants() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCode();

    // create multiple variants
    final Variant v0 = createModifiedVariant(base, "Foo.java", "n--;", "n;", 0);
    final Variant v1 = createModifiedVariant(base, "Foo.java", "n--;", "n;;", 1);
    final Variant v2 = createModifiedVariant(base, "Foo.java", "n--;", "n;;;", 2);
    final Variant v3 = createModifiedVariant(base, "Foo.java", "n--;", "n;;;;", 3);
    final Variant v4 = createModifiedVariant(base, "Foo.java", "n--;", "n;;;;;", 4);
    final VariantStore variantStore = createMockedVariantStore(v0, v1, v2, v3, v4);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(outdir.resolve("patch-v0")).exists();
    assertThat(outdir.resolve("patch-v1")).exists();
    assertThat(outdir.resolve("patch-v2")).exists();
    assertThat(outdir.resolve("patch-v3")).exists();
    assertThat(outdir.resolve("patch-v4")).exists();
    assertThat(outdir.resolve("patch-v4/example.Foo.java")).exists();
    assertThat(outdir.resolve("patch-v4/example.Foo.diff")).exists();
  }

  @Test
  public void testExportWithPreviousResults() throws IOException {
    final Path outdir = TestUtil.createVirtualDir();

    // setup prev results
    Files.createDirectory(outdir);
    Files.createDirectory(outdir.resolve("patch-v3"));

    final GeneratedSourceCode base = createGeneratedSourceCode();
    final Variant variant = createModifiedVariant(base, "Foo.java", "n--;", "n -= 1; //", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(true).isTrue(); // to prevent smoke test in sonarlint
  }

  @Test
  public void testPatchGenerationWithShiftJISEncodedSourceCode() throws IOException {
    final Logger logger = (Logger) LoggerFactory.getLogger(PatchFileExporter.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCodeFromShiftJISEncodedFiles();

    final Variant variant = createModifiedVariant(base, "Foo.java", "return m;", "return n;", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(outdir.resolve("patch-v0")).exists();

    final String diff = Files.readString(outdir.resolve("patch-v0/example.Foo.diff"),
        StandardCharsets.UTF_8);
    // エンコーディングの違いによるdiffがないか確かめる
    assertThat(diff)
        .doesNotContain(
            new String("2つの整数のうち大きい整数を返す".getBytes(SHIFT_JIS), StandardCharsets.UTF_8))
        .doesNotContain(
            new String("整数".getBytes(SHIFT_JIS), StandardCharsets.UTF_8))
        .doesNotContain(
            new String("n, mのうち大きい整数".getBytes(SHIFT_JIS), StandardCharsets.UTF_8))
        .contains("public int max")
        .containsPattern("- +return m;")
        .containsPattern("\\+ +return n;");
  }

  @Test
  public void testPatchGenerationWithUTF8EncodedSourceCode() throws IOException {
    final Logger logger = (Logger) LoggerFactory.getLogger(PatchFileExporter.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    final Path outdir = TestUtil.createVirtualDir();
    final GeneratedSourceCode base = createGeneratedSourceCodeFromUTF8EncodedFiles();

    final Variant variant = createModifiedVariant(base, "Foo.java", "return m;", "return n;", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchFileExporter(outdir);
    patchExporter.export(variantStore);

    assertThat(outdir.resolve("patch-v0")).exists();

    final String diff = Files.readString(outdir.resolve("patch-v0/example.Foo.diff"),
        StandardCharsets.UTF_8);
    // エンコーディングの違いによるdiffがないか確かめる
    assertThat(diff)
        .doesNotContain(
            new String("2つの整数のうち大きい整数を返す".getBytes(Charset.defaultCharset()),
                StandardCharsets.UTF_8))
        .doesNotContain(
            new String("整数".getBytes(Charset.defaultCharset()), StandardCharsets.UTF_8))
        .doesNotContain(
            new String("n, mのうち大きい整数".getBytes(Charset.defaultCharset()), StandardCharsets.UTF_8))
        .contains("public int max")
        .containsPattern("- +return m;")
        .containsPattern("\\+ +return n;");
  }

  private GeneratedSourceCode createGeneratedSourceCode() {
    // setup original source from BS03
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return TestUtil.createGeneratedSourceCode(targetProject);
  }

  private GeneratedSourceCode createGeneratedSourceCodeFromUTF8EncodedFiles() {
    final Path rootPath = Paths.get("example/BuildSuccess24");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return TestUtil.createGeneratedSourceCode(targetProject);
  }

  private GeneratedSourceCode createGeneratedSourceCodeFromShiftJISEncodedFiles() {
    final Path rootPath = Paths.get("example/BuildSuccess25");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    return TestUtil.createGeneratedSourceCode(targetProject);
  }

  @SuppressWarnings({"rawtypes", "unchecked"}) // suppress warnings for jdt constructions
  private Variant createModifiedVariant(final GeneratedSourceCode baseSourceCode,
      final String replacedFile,
      final String replaceFrom,
      final String replaceTo,
      final long id) {

    // extract target ast from base source dcode
    final GeneratedAST targetAst = baseSourceCode.getProductAsts()
        .stream()
        .filter(ast -> ast.getSourcePath()
            .toString()
            .endsWith(replacedFile))
        .findFirst()
        .orElseThrow(RuntimeException::new);

    // modify the target ast with string replacement
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final String originalSource = targetAst.getSourceCode();
    final String modifiedSource = originalSource.replace(replaceFrom, replaceTo);
    final GeneratedAST ast = constructor.constructAST(targetAst.getSourcePath(), modifiedSource,
        targetAst.getCharset());

    // spy to return the modified ast
    final GeneratedSourceCode spy = spy(baseSourceCode);
    when(spy.getProductAsts()).thenReturn(List.of(ast));

    // mock to variant
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(spy);
    when(variant.getId()).thenReturn(id);
    return variant;
  }

  private VariantStore createMockedVariantStore(final Variant... variants) {
    final VariantStore vs = mock(VariantStore.class);
    when(vs.getFoundSolutions()).thenReturn(List.of(variants));
    return vs;
  }
}
