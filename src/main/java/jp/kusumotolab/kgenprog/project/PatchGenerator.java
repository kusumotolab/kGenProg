package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class PatchGenerator implements ResultGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);

  @Override
  public List<Result> exec(final TargetProject targetProject, final Variant modifiedVariants) {
    log.debug("enter exec(TargetProject, Variant)");

    final List<Result> patches = new ArrayList<>();

    final GeneratedSourceCode modifiedSourceCode =
        applyAllModificationDirectly(targetProject, modifiedVariants);

    for (final GeneratedAST ast : modifiedSourceCode.getAsts()) {
      try {
        final GeneratedJDTAST jdtAST = (GeneratedJDTAST) ast;
        final Path originPath = jdtAST.getProductSourcePath().path;

        // 修正ファイル作成
        final Document document = new Document(new String(Files.readAllBytes(originPath)));
        final TextEdit edit = jdtAST.getRoot()
            .rewrite(document, null);
        // その AST が変更されているかどうか判定
        if (edit.getChildren().length != 0) {
          edit.apply(document);

          //Patch オブジェクトの生成
          final String fileName = jdtAST.getPrimaryClassName();
          final List<String> modifiedSourceCodeLines = Arrays.asList(document.get()
              .split(document.getDefaultLineDelimiter()));
          final List<String> diff = makeDiff(fileName, Files.readAllLines(originPath), modifiedSourceCodeLines);
          patches.add(new Patch(fileName, modifiedSourceCodeLines, diff));
        }
      } catch (final MalformedTreeException | BadLocationException | IOException e) {
        throw new RuntimeException(e);
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
    for (final GeneratedAST ast : code.getAsts()) {
      ((GeneratedJDTAST) ast).getRoot()
          .recordModifications();
    }
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

    for (final Base base : modifiedVariant.getGene()
        .getBases()) {
      targetCode = base.getOperation()
          .applyDirectly(targetCode, base.getTargetLocation());
    }

    return targetCode;
  }

  /***
   * UnifiedDiff 形式の diff を返す．
   *
   * @param fileName
   * @param origin
   * @param modified
   * @return
   */
  private List<String> makeDiff(final String fileName, final List<String> origin,
      final List<String> modified) {
    try {
      final com.github.difflib.patch.Patch<String> diff = DiffUtils.diff(origin, modified);
      return UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, origin, diff, 3);
    } catch (final DiffException e) {
      throw new RuntimeException(e);
    }
  }
}
