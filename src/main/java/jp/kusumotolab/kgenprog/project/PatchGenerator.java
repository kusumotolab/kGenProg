package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import jp.kusumotolab.kgenprog.ga.Variant;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);

  public Patches exec(final Variant modifiedVariant) {
    log.debug("enter exec(Variant)");

    final Patches patches = new Patches();
    final GeneratedSourceCode modifiedSourceCode = modifiedVariant.getGeneratedSourceCode();
    final List<GeneratedAST<ProductSourcePath>> modifiedAsts = modifiedSourceCode.getProductAsts();

    for (final GeneratedAST<ProductSourcePath> ast : modifiedAsts) {
      try {
        final Patch patch = makePatch(ast);
        final String diff = patch.getDiff();
        if (diff.isEmpty()) {
          continue;
        }
        patches.add(patch);
      } catch (final IOException | DiffException e) {
        log.error(e.getMessage());
        return new Patches();
      }
    }
    log.debug("exit exec(Variant)");
    return patches;
  }

  /***
   * patch オブジェクトの生成を行う
   *
   * @param ast
   * @return
   * @throws IOException
   * @throws DiffException
   */
  private Patch makePatch(final GeneratedAST<?> ast) throws IOException, DiffException {
    final Path originPath = ast.getSourcePath().path;

    final String modifiedSourceCodeText = ast.getSourceCode();
    final Document document = new Document(modifiedSourceCodeText);

    final String fileName = ast.getPrimaryClassName();
    final String delimiter = document.getDefaultLineDelimiter();
    final List<String> modifiedSourceCodeLines =
        Arrays.asList(modifiedSourceCodeText.split(delimiter));
    final List<String> originalSourceCodeLines = Files.readAllLines(originPath);
    final List<String> noBlankLineOriginalSourceCodeLines = removeEndDelimiter(originalSourceCodeLines);
    final List<String> diffLines =
        makeDiff(fileName, noBlankLineOriginalSourceCodeLines, modifiedSourceCodeLines);

    return new Patch(diffLines, fileName, originalSourceCodeLines, modifiedSourceCodeLines);
  }

  /***
   * UnifiedDiff 形式の diff を返す．
   *
   * @param fileName
   * @param originalSourceCodeLines
   * @param modifiedSourceCodeLines
   * @return
   */
  private List<String> makeDiff(final String fileName, final List<String> originalSourceCodeLines,
      final List<String> modifiedSourceCodeLines) throws DiffException {
    final com.github.difflib.patch.Patch<String> diff =
        DiffUtils.diff(originalSourceCodeLines, modifiedSourceCodeLines);
    return UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, originalSourceCodeLines, diff,
        3);
  }

  private List<String> removeEndDelimiter(final List<String> sourceCodeLines) {
    for (int index = sourceCodeLines.size() - 1; index >= 0; index--) {
      final String sourceCodeLine = sourceCodeLines.get(index);
      if (!sourceCodeLine.equals("")) {
        return sourceCodeLines.subList(0, index + 1);
      }
    }

    return Collections.emptyList();
  }
}
