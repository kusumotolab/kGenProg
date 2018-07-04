package jp.kusumotolab.kgenprog.project;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {

  private static Logger log = LoggerFactory.getLogger(GeneratedSourceCode.class);

  // TODO listは順序が保証されず重複を許容してしまう．Mapで名前から引ける方が外から使いやすい．
  private List<GeneratedAST> files;
  private Map<SourceFile, GeneratedAST> fileToAST;

  public GeneratedSourceCode(List<GeneratedAST> files) {
    this.files = files;
    fileToAST = files.stream().collect(Collectors.toMap(GeneratedAST::getSourceFile, v -> v));
  }

  public List<GeneratedAST> getFiles() {
    log.debug("enter getFiles()");
    return files;
  }

  public GeneratedAST getAST(SourceFile file) {
    log.debug("enter getAST()");
    return fileToAST.get(file);
  }

  public List<Location> inferLocations(SourceFile file, int lineNumber) {
    log.debug("enter inferLocations(SourceFile, int)");
    GeneratedAST ast = getAST(file);
    if (ast == null) {
      return Collections.emptyList();
    }
    return ast.inferLocations(lineNumber);
  }
  
  public List<Location> getAllLocations() {
    return files.stream().flatMap(v -> v.getAllLocations().stream()).collect(Collectors.toList());
  }
  
  public Range inferLineNumbers(Location location) {
    log.debug("enter inferLineNumbers(Location)");
    return location.inferLineNumbers();
  }
}
