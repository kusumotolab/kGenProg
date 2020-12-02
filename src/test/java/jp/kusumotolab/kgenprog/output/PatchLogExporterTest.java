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

public class PatchLogExporterTest {

  @Test
  public void testPatchGenerationWithShiftJISEncodedSourceCode() throws IOException {
    final Logger logger = (Logger) LoggerFactory.getLogger(PatchLogExporter.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    final GeneratedSourceCode base = createGeneratedSourceCodeFromShiftJISEncodedFiles();

    final Variant variant = createModifiedVariant(base, "Foo.java", "return m;", "return n;", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchLogExporter();
    patchExporter.export(variantStore);

    // logの確認
    final List<ILoggingEvent> logs = listAppender.list;
    final String message = logs.get(0)
        .getMessage();
    assertThat(message)
        .doesNotContain(
            new String("2つの整数のうち大きい整数を返す".getBytes(StandardCharsets.UTF_8),
                Charset.defaultCharset()))
        .doesNotContain(
            new String("整数".getBytes(StandardCharsets.UTF_8), Charset.defaultCharset()))
        .doesNotContain(
            new String("n, mのうち大きい整数".getBytes(StandardCharsets.UTF_8), Charset.defaultCharset()))
        .contains("public int max")
        .containsPattern("- +return m;")
        .containsPattern("\\+ +return n;");
  }

  @Test
  public void testPatchGenerationWithUTF8EncodedSourceCode() throws IOException {
    final Logger logger = (Logger) LoggerFactory.getLogger(PatchLogExporter.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    final GeneratedSourceCode base = createGeneratedSourceCodeFromUTF8EncodedFiles();

    final Variant variant = createModifiedVariant(base, "Foo.java", "return m;", "return n;", 0);
    final VariantStore variantStore = createMockedVariantStore(variant);

    final Exporter patchExporter = new PatchLogExporter();
    patchExporter.export(variantStore);

    // logの確認
    final List<ILoggingEvent> logs = listAppender.list;
    final String message = logs.get(0)
        .getMessage();
    assertThat(message)
        .doesNotContain(
            new String("2つの整数のうち大きい整数を返す".getBytes(StandardCharsets.UTF_8),
                Charset.defaultCharset()))
        .doesNotContain(
            new String("整数".getBytes(StandardCharsets.UTF_8), Charset.defaultCharset()))
        .doesNotContain(
            new String("n, mのうち大きい整数".getBytes(StandardCharsets.UTF_8), Charset.defaultCharset()))
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
