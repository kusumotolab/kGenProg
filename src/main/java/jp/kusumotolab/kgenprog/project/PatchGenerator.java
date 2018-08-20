package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);

  public List<Patch> exec(final TargetProject targetProject, final Variant modifiedVariant) {
    log.debug("enter exec(TargetProject, Variant)");

    final List<Patch> patches = new ArrayList<>();

    final GeneratedSourceCode modifiedSourceCode = modifiedVariant.getGeneratedSourceCode();
    final List<GeneratedAST> modifiedAsts = modifiedSourceCode.getAsts();

    for (final GeneratedAST ast : modifiedAsts) {
      try {
        patches.add(makePatch(ast));
      } catch (final IOException | DiffException e) {
        log.error(e.getMessage());
        return Collections.emptyList();
      }
    }
    log.debug("exit exec(TargetProject, Variant)");
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
  private Patch makePatch(final GeneratedAST ast) throws IOException, DiffException {
    final Path originPath = ast.getProductSourcePath().path;

    final String modifiedSourceCodeText = ast.getSourceCode();
    final Document document = new Document(modifiedSourceCodeText);

    final String fileName = ast.getPrimaryClassName();
    final String delimiter = document.getDefaultLineDelimiter();
    final List<String> modifiedSourceCodeLines =
        Arrays.asList(modifiedSourceCodeText.split(delimiter));
    final List<String> originalSouceCodeLines = Files.readAllLines(originPath);
    final List<String> diffLines =
        makeDiff(fileName, originalSouceCodeLines, modifiedSourceCodeLines);

    return new Patch(diffLines, fileName, originalSouceCodeLines, modifiedSourceCodeLines);
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
}
