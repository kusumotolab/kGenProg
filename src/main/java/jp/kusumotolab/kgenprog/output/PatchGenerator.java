package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);

  public Patch exec(final Variant modifiedVariant) {
    final List<FileDiff> filediffs = modifiedVariant.getGeneratedSourceCode()
        .getProductAsts()
        .stream()
        .map(this::createFileDiff)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return new Patch(filediffs, modifiedVariant.getId());
  }

  private FileDiff createFileDiff(final GeneratedAST<?> ast) {
    final String fileName = ast.getPrimaryClassName().value;
    final List<String> original = readOriginalSource(ast);
    final List<String> modified = parseModifiedSource(ast);
    final List<String> diffLines = calcUnifiedDiff(fileName, original, modified);
    if (diffLines.isEmpty()) {
      return null;
    }
    return new FileDiff(fileName, diffLines, original, modified);
  }

  private List<String> parseModifiedSource(final GeneratedAST<?> ast) {
    final String source = ast.getSourceCode();
    final String delimiter = new Document(source).getDefaultLineDelimiter();
    return List.of(source.split(delimiter));
  }

  private List<String> readOriginalSource(final GeneratedAST<?> ast) {
    final Path originalPath = ast.getSourcePath()
        .getResolvedPath();

    try {
      final Charset fileEncoding = inferFileEncoding(originalPath);
      if (fileEncoding.equals(StandardCharsets.UTF_8)) {
        return Files.readAllLines(originalPath);
      } else {
        return Files.readAllLines(originalPath, fileEncoding)
            .stream()
            .map(e -> e.getBytes(StandardCharsets.UTF_8))
            .map(e -> new String(e, StandardCharsets.UTF_8))
            .collect(Collectors.toList());
      }
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  private Charset inferFileEncoding(final Path path) throws IOException {
    final String encoding = UniversalDetector.detectCharset(path);
    return encoding != null ? Charset.forName(encoding) : Charset.defaultCharset();
  }

  private List<String> calcUnifiedDiff(final String fileName, final List<String> original,
      final List<String> modified) {
    try {
      final com.github.difflib.patch.Patch<String> diff = DiffUtils.diff(original, modified);
      return UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, original, diff, 3);
    } catch (final DiffException e) {
      log.error(String.format("cannot calculate diff: %s", fileName));
      return Collections.emptyList();
    }
  }

  // TODO: format all source code by this method
  private String format(final String source) {
    final int kind = CodeFormatter.K_UNKNOWN; // necessary to compile partial source code (eg, n++;)
    final int indent = 0;
    final String sep = "\n";

    final CodeFormatter formatter =
        ToolFactory.createCodeFormatter(JavaCore.getDefaultOptions());
    final TextEdit textEdit = formatter.format(kind, source, 0, source.length(), indent, sep);

    final IDocument document = new Document(source);

    try {
      textEdit.apply(document);
    } catch (final MalformedTreeException | BadLocationException | NullPointerException e) {
      log.error(String.format("Source code <%s> cannot be formatted", source), e);
      return "";
    }
    final String formatted = document.get();
    return formatted.trim();
  }
}
