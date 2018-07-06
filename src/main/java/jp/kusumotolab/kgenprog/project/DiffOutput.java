package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class DiffOutput implements ResultOutput {

  private final Path workingDir;

  public DiffOutput(Path workingDir) {
    this.workingDir = workingDir;
  }

  @Override
  public void outputResult(TargetProject targetProject, List<Variant> modifiedVariants) {

    List<GeneratedSourceCode> modifiedCode = new ArrayList<>();

    // 出力先ディレクトリ作成
    if (!Files.exists(workingDir)) {
      try {
        Files.createDirectory(workingDir);
      } catch (IOException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }
    }

    // 初期 ast に対して，修正された ast へ実行された全変更内容をクローンを生成せずに適用
    for (Variant variant : modifiedVariants) {
      GeneratedSourceCode targetCode = targetProject.getInitialVariant().getGeneratedSourceCode();
      activateRecordModifications(targetCode);
      for (Base base : variant.getGene().getBases()) {
        targetCode = base.getOperation().applyDirectly(targetCode, base.getTargetLocation());
      }
      modifiedCode.add(targetCode);
    }

    for (GeneratedSourceCode code : modifiedCode) {
      Path variantBasePath = Paths.get(workingDir + "/Variant" + (modifiedCode.indexOf(code) + 1));
      try {
        Files.createDirectory(variantBasePath);
      } catch (IOException e1) {
        // TODO 自動生成された catch ブロック
        e1.printStackTrace();
      }
      for (GeneratedAST ast : code.getFiles()) {
        try {
          GeneratedJDTAST jdtAST = (GeneratedJDTAST) ast;

          // 修正ファイル作成
          String origStr = new String(Files
              .readAllBytes(getOriginPath(targetProject.getSourceFiles(), jdtAST.getSourceFile())));
          Document document = new Document(origStr);
          TextEdit edit = jdtAST.getRoot().rewrite(document, null);
          if (edit.getChildren().length != 0) {
            edit.apply(document);
            Files.write(variantBasePath.resolve(jdtAST.getPrimaryClassName() + ".java"),
                Arrays.asList(document.get()));

            // パッチファイル作成
            List<String> origin = Files.readAllLines(
                getOriginPath(targetProject.getSourceFiles(), jdtAST.getSourceFile()));
            List<String> modified =
                Files.readAllLines(variantBasePath.resolve(jdtAST.getPrimaryClassName() + ".java"));

            Patch<String> diff = DiffUtils.diff(origin, modified);

            List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                getOriginPath(targetProject.getSourceFiles(), jdtAST.getSourceFile()).getFileName()
                    .toString(),
                jdtAST.getSourceFile().path.getFileName().toString(), origin, diff, 3);

            unifiedDiff.forEach(System.out::println);

            Files.write(variantBasePath.resolve(jdtAST.getPrimaryClassName() + ".patch"), unifiedDiff);
          }
        } catch (MalformedTreeException e) {
          e.printStackTrace();
        } catch (BadLocationException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (DiffException e) {
          // TODO 自動生成された catch ブロック
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * variant 内のすべての AST が，その AST に対して行われた変更履歴を記録するように設定．
   *
   * @param variant
   */
  private void activateRecordModifications(GeneratedSourceCode code) {
    for (GeneratedAST ast : code.getFiles()) {
      ((GeneratedJDTAST) ast).getRoot().recordModifications();
    }
  }

  /**
   * 変更前ファイルのパス取得
   *
   * @param originFiles
   * @param source
   * @return
   */
  private Path getOriginPath(List<SourceFile> originFiles, SourceFile source) {
    for (SourceFile origin : originFiles) {
      try {
        if (Files.isSameFile(origin.path, source.path)) {
          return origin.path;
        }
      } catch (IOException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }
    }
    return null;
  }
}
