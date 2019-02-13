package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.jface.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.InsertBlockOperation;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);
  private final boolean normalizeSourceCode;

  public PatchGenerator(final boolean normalizeSourceCode) {
    this.normalizeSourceCode = normalizeSourceCode;
  }

  public Patch exec(final Variant modifiedVariant) {

    final Patch patch = new Patch();
    final GeneratedSourceCode modifiedSourceCode;
    if (normalizeSourceCode) {
      modifiedSourceCode = normalizeGeneratedSourceCode(modifiedVariant.getGene(),
          modifiedVariant.getGeneratedSourceCode());
    } else {
      modifiedSourceCode = modifiedVariant.getGeneratedSourceCode();
    }

    final List<GeneratedAST<ProductSourcePath>> modifiedAsts = modifiedSourceCode.getProductAsts();

    for (final GeneratedAST<ProductSourcePath> ast : modifiedAsts) {
      try {
        final FileDiff fileDiff = makeFileDiff(ast);
        final String diff = fileDiff.getDiff();
        if (diff.isEmpty()) {
          continue;
        }
        patch.add(fileDiff);
      } catch (final IOException | DiffException e) {
        log.error(e.getMessage());
        return new Patch();
      }
    }
    return patch;
  }

  // テストのためにpackage privateとする
  GeneratedSourceCode normalizeGeneratedSourceCode(final Gene gene,
      final GeneratedSourceCode origin) {
    // 変更されたファイルのみ、正規化を行う
    final List<ASTLocation> locations = gene.getBases()
        .stream()
        .map(Base::getTargetLocation)
        .collect(
            Collectors.groupingBy(ASTLocation::getSourcePath, Collectors.reducing((a, b) -> a)))
        .values()
        .stream()
        .map(Optional::get)
        .collect(Collectors.toList());

    GeneratedSourceCode current = origin;
    final InsertBlockOperation operation = new InsertBlockOperation();

    for (final ASTLocation location : locations) {
      current = operation.apply(current, location);
    }

    return current;
  }

  /***
   * FileDiff オブジェクトの生成を行う
   *
   * @param ast
   * @return
   * @throws IOException
   * @throws DiffException
   */
  private FileDiff makeFileDiff(final GeneratedAST<?> ast) throws IOException, DiffException {
    final Path originPath = ast.getSourcePath()
        .getResolvedPath();

    final String modifiedSourceCodeText = ast.getSourceCode();
    final Document document = new Document(modifiedSourceCodeText);

    final String fileName = ast.getPrimaryClassName().value;
    final String delimiter = document.getDefaultLineDelimiter();
    final List<String> modifiedSourceCodeLines =
        Arrays.asList(modifiedSourceCodeText.split(delimiter));
    final List<String> originalSourceCodeLines = Files.readAllLines(originPath);
    final List<String> noBlankLineOriginalSourceCodeLines =
        removeEndDelimiter(originalSourceCodeLines);
    final List<String> diffLines =
        makeDiff(fileName, noBlankLineOriginalSourceCodeLines, modifiedSourceCodeLines);

    return new FileDiff(diffLines, fileName, originalSourceCodeLines, modifiedSourceCodeLines);
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
