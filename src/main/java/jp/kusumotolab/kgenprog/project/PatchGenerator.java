package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);

  public List<Patch> exec(final TargetProject targetProject, final Variant modifiedVariant) {
    log.debug("enter exec(TargetProject, Variant)");

    final List<Patch> patches = new ArrayList<>();

    final GeneratedSourceCode modifiedSourceCode =
        applyAllModificationDirectly(targetProject, modifiedVariant);

    for (final GeneratedAST ast : modifiedSourceCode.getAsts()) {
      try {
        // TODO
        // 危険なダウンキャスト #239
        final GeneratedJDTAST jdtAST = (GeneratedJDTAST) ast;
        final Path originPath = jdtAST.getProductSourcePath().path;

        // 修正ファイル作成
        final String originalSourceCodeText = new String(Files.readAllBytes(originPath));
        final Document document = new Document(originalSourceCodeText);
        final CompilationUnit unit = jdtAST.getRoot();
        final TextEdit edit = unit.rewrite(document, null);

        // その AST が変更されているかどうか判定
        if (0 == edit.getChildren().length) {
          continue; // 変更されていなかったら何もしない
        }

        // 変更を適用
        edit.apply(document);

        // Patch オブジェクトの生成
        final String fileName = jdtAST.getPrimaryClassName();
        final String modifiedSourceCodeText = document.get();
        final String delimiter = document.getDefaultLineDelimiter();
        final List<String> modifiedSourceCodeLines =
            Arrays.asList(modifiedSourceCodeText.split(delimiter));
        final List<String> originalSouceCodeLines =
            Arrays.asList(originalSourceCodeText.split(delimiter));
        final List<String> diffLines =
            makeDiff(fileName, originalSouceCodeLines, modifiedSourceCodeLines);
        final Patch patch =
            new Patch(diffLines, fileName, originalSouceCodeLines, modifiedSourceCodeLines);
        patches.add(patch);
      } catch (final MalformedTreeException | BadLocationException | IOException
          | DiffException e) {
        log.error(e.getMessage());
        return Collections.emptyList();
      }
    }
    log.debug("exit exec(TargetProject, Variant)");
    return patches;
  }

  /**
   * variant 内のすべての AST が，その AST に対して行われた変更履歴を記録するように設定．
   *
   * @param variant
   */
  private void activateRecordModifications(final GeneratedSourceCode code) {
    code.getAsts()
        .stream()
        .map(ast -> ((GeneratedJDTAST) ast).getRoot())
        .forEach(cu -> cu.recordModifications());
  }

  /***
   * 初期 ast に対して，修正された ast へ実行された全変更内容をクローンを生成せずに適用
   *
   * @param targetProject
   * @param modifiedVariant
   * @return
   */
  private GeneratedSourceCode applyAllModificationDirectly(final TargetProject targetProject,
      final Variant modifiedVariant) {
    GeneratedSourceCode targetCode = targetProject.getInitialVariant()
        .getGeneratedSourceCode();
    activateRecordModifications(targetCode);
    final Gene gene = modifiedVariant.getGene();
    for (final Base base : gene.getBases()) {
      targetCode = base.getOperation()
          .applyDirectly(targetCode, base.getTargetLocation());
    }
    return targetCode;
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
