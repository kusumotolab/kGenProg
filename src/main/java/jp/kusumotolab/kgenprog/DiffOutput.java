package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class DiffOutput implements ResultOutput {

  @Override
  public void outputResult(TargetProject targetProject, List<Variant> modifiedVariants) {

    List<GeneratedSourceCode> modifiedCode = new ArrayList<GeneratedSourceCode>();

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
          String str = readAll(getOriginPath(targetProject.getSourceFiles(), ast.getSourceFile()));
          Document document = new Document(str);
          TextEdit edit = ((GeneratedJDTAST) ast).getRoot().rewrite(document, null);
          if (edit.getChildren().length != 0) {
            edit.apply(document);
            System.out.println(document.get());
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (MalformedTreeException e) {
          e.printStackTrace();
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * variant 内のすべての AST に recordModification を実行．
   *
   * @param variant
   */
  public void recordAST(GeneratedSourceCode code) {
    for (GeneratedAST ast : code.getFiles()) {
      ((GeneratedJDTAST) ast).getRoot().recordModifications();
    }
  }

  /**
   * ファイル一括読み出し
   *
   * @param path
   * @return
   * @throws IOException
   */
  public String readAll(String path) throws IOException {
    return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
        .collect(Collectors.joining(System.getProperty("line.separator")));
  }


  /**
   * 変更前ファイルのパス取得
   *
   * @param originFiles
   * @param source
   * @return
   */
  public String getOriginPath(List<SourceFile> originFiles, SourceFile source) {
    for (SourceFile origin : originFiles) {
      if (origin.path.getFileName().equals(source.path.getFileName())) {
        return origin.path.toString();
      }
    }

    return null;
  }

}
