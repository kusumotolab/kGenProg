package jp.kusumotolab.kgenprog.project;

import java.util.List;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {
  // TODO listは順序が保証されず重複を許容してしまう．Mapで名前から引ける方が外から使いやすい．
  private List<GeneratedAST> files;

  public GeneratedSourceCode(List<GeneratedAST> files) {
    this.files = files;
  }

  public List<GeneratedAST> getFiles() {
    return files;
  }
}
