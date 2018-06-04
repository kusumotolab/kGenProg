package jp.kusumotolab.kgenprog.project;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {
  // TODO listは順序が保証されず重複を許容してしまう．Mapで名前から引ける方が外から使いやすい．
  private List<GeneratedAST> files;
  private Map<SourceFile, GeneratedAST> fileToAST;

  public GeneratedSourceCode(List<GeneratedAST> files) {
    this.files = files;
    fileToAST = files.stream().collect(Collectors.toMap(GeneratedAST::getSourceFile, v -> v));
  }

  public List<GeneratedAST> getFiles() {
    return files;
  }

  public GeneratedAST getAST(SourceFile file) {
    return fileToAST.get(file);
  }

  public Location inferLocation(SourceFile file, int lineNumber) {
    GeneratedAST ast = getAST(file);
    if (ast == null) {
      return null;
    }
    return ast.inferLocation(lineNumber);
  }
}
