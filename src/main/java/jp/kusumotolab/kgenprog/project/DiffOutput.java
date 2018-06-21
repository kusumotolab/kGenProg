package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class DiffOutput implements ResultOutput {

  @Override
  public void outputResult(TargetProject targetProject, List<Variant> modifiedVariants) {

    List<GeneratedSourceCode> modifiedCode = new ArrayList<GeneratedSourceCode>();
    Variant originVariant = targetProject.getInitialVariant();

    File file = new File(targetProject.rootPath.toString() + File.separator + "modified");
    file.mkdir();

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

            File modFile = new File(file.getPath() + File.separator + ((GeneratedJDTAST)ast).getSourceFile().path.getFileName().toString());
            modFile.createNewFile();
            FileWriter fileWriter = new FileWriter(modFile);
            fileWriter.write(document.get());
            fileWriter.close();
          }
        } catch (MalformedTreeException e) {
          e.printStackTrace();
        } catch (BadLocationException e) {
          e.printStackTrace();
        } catch (IOException e) {
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
    return new String(Files.readAllBytes(Paths.get(path)));
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
