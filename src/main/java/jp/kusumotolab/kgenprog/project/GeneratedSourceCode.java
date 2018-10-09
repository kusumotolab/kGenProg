package jp.kusumotolab.kgenprog.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {

  private static Logger log = LoggerFactory.getLogger(GeneratedSourceCode.class);
  private static final String DIGEST_ALGORITHM = "MD5";

  // TODO listは順序が保証されず重複を許容してしまう．Mapで名前から引ける方が外から使いやすい．
  private final List<GeneratedAST> asts;
  private final Map<SourcePath, GeneratedAST> pathToAst;
  private final String messageDigest;

  public GeneratedSourceCode(List<GeneratedAST> asts) {
    this.asts = asts;
    pathToAst = asts.stream()
        .collect(Collectors.toMap(GeneratedAST::getProductSourcePath, v -> v));
    this.messageDigest = createMessageDigest();
  }

  public List<GeneratedAST> getAsts() {
    log.debug("enter getAsts()");
    return asts;
  }

  public GeneratedAST getAst(SourcePath path) {
    log.debug("enter getAst()");
    return pathToAst.get(path);
  }

  public List<ASTLocation> inferLocations(SourcePath path, int lineNumber) {
    log.debug("enter inferLocations(SourcePath, int)");
    GeneratedAST ast = getAst(path);
    if (ast == null) {
      return Collections.emptyList();
    }
    return ast.inferLocations(lineNumber);
  }

  public List<ASTLocation> getAllLocations() {
    return asts.stream()
        .flatMap(v -> v.getAllLocations()
            .stream())
        .collect(Collectors.toList());
  }

  public LineNumberRange inferLineNumbers(ASTLocation location) {
    log.debug("enter inferLineNumbers(Location)");
    return location.inferLineNumbers();
  }

  public String getMessageDigest() {
    return messageDigest;
  }

  public boolean isGenerationSuccess() {
    return true;
  }

  private String createMessageDigest() {
    try {
      final MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

      asts.stream()
          .sorted(Comparator.comparing(v -> v.getProductSourcePath()
              .toString()))
          .map(GeneratedAST::getMessageDigest)
          .map(String::getBytes)
          .forEach(digest::update);

      return Hex.encodeHexString(digest.digest());

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final GeneratedSourceCode generatedSourceCode = (GeneratedSourceCode) o;
    return Objects.equals(messageDigest, generatedSourceCode.messageDigest) &&
        Objects.equals(asts, generatedSourceCode.asts);
  }

  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    result = result * prime + messageDigest.hashCode();
    result = result * prime + asts.hashCode();

    return result;
  }
}
