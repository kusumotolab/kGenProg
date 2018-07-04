package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

  private final Path outDir;

  public DiffOutput(Path outDir) {
    this.outDir = outDir;
  }

  @Override
  public void outputResult(TargetProject targetProject, List<Variant> modifiedVariants) {

    List<GeneratedSourceCode> modifiedCode = new ArrayList<>();

    // 出力先ディレクトリ作成
    if(!outDir.toFile().exists()) {
      outDir.toFile().mkdir();
    }

    // ast へ変更内容を適用
    for (Variant variant : modifiedVariants) {
      GeneratedSourceCode targetCode = targetProject.getInitialVariant().getGeneratedSourceCode();
      recordAST(targetCode);
      for (Base base : variant.getGene().getBases()) {
        targetCode = base.getOperation().applyDirectly(targetCode, base.getTargetLocation());
      }
      modifiedCode.add(targetCode);
    }

    for (GeneratedSourceCode code : modifiedCode) {
      for (GeneratedAST ast : code.getFiles()) {
        try {
          // 修正ファイル作成
          String origStr = new String(
              Files.readAllBytes(getOriginPath(targetProject.getSourceFiles(), ast.getSourceFile())));
                  ;
          Document document = new Document(origStr);
          TextEdit edit = ((GeneratedJDTAST) ast).getRoot().rewrite(document, null);
          if (edit.getChildren().length != 0) {
            edit.apply(document);
            Files.write(
                outDir
                    .resolve(((GeneratedJDTAST) ast).getSourceFile().path.getFileName().toString()),
                Arrays.asList(document.get()), StandardOpenOption.CREATE);

            // patch file 作成
            List<String> origin = Files
                .readAllLines(getOriginPath(targetProject.getSourceFiles(), ast.getSourceFile()));
            List<String> modified = Files.readAllLines(outDir
                .resolve(((GeneratedJDTAST) ast).getSourceFile().path.getFileName().toString()));

            Patch<String> diff = DiffUtils.diff(origin, modified);

            List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                getOriginPath(targetProject.getSourceFiles(), ast.getSourceFile()).getFileName()
                    .toString(),
                ast.getSourceFile().path.getFileName().toString(), origin, diff, 3);

            unifiedDiff.forEach(System.out::println);

            int index = ((GeneratedJDTAST) ast).getSourceFile().path.getFileName().toString()
                .lastIndexOf(".");
            String fileName = ((GeneratedJDTAST) ast).getSourceFile().path.getFileName().toString()
                .substring(0, index);

            Files.write(outDir.resolve(fileName + ".patch"), unifiedDiff, StandardOpenOption.CREATE);
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
  public void recordAST(GeneratedSourceCode code) {
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
  public Path getOriginPath(List<SourceFile> originFiles, SourceFile source) {
    for (SourceFile origin : originFiles) {
      if (origin.path.getFileName().equals(source.path.getFileName())) {
        return origin.path;
      }
    }

    return null;
  }

}
