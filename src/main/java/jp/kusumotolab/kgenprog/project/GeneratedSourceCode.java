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
  private List<GeneratedAST> asts;
  private Map<SourcePath, GeneratedAST> pathToAst;

  public GeneratedSourceCode(List<GeneratedAST> asts) {
    this.asts = asts;
    pathToAst = asts.stream()
        .collect(Collectors.toMap(GeneratedAST::getSourcePath, v -> v));
  }

  public List<GeneratedAST> getAsts() {
    log.debug("enter getAsts()");
    return asts;
  }

  public GeneratedAST getAst(SourcePath path) {
    log.debug("enter getAst()");
    return pathToAst.get(path);
  }

  public List<Location> inferLocations(SourcePath path, int lineNumber) {
    log.debug("enter inferLocations(SourcePath, int)");
    GeneratedAST ast = getAst(path);
    if (ast == null) {
      return Collections.emptyList();
    }
    return ast.inferLocations(lineNumber);
  }

  public List<Location> getAllLocations() {
    return asts.stream()
        .flatMap(v -> v.getAllLocations()
            .stream())
        .collect(Collectors.toList());
  }

  public Range inferLineNumbers(Location location) {
    log.debug("enter inferLineNumbers(Location)");
    return location.inferLineNumbers();
  }
}
