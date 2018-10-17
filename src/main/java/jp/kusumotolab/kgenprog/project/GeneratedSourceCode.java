package jp.kusumotolab.kgenprog.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

  private final List<GeneratedAST<ProductSourcePath>> productAsts;
  private final List<GeneratedAST<TestSourcePath>> testAsts;
  private final Map<SourcePath, GeneratedAST<ProductSourcePath>> pathToAst;
  private final String messageDigest;

  /**
   * @param productAsts ProductソースコードのAST
   * @param testAsts TestソースコードのList
   */
  public GeneratedSourceCode(final List<GeneratedAST<ProductSourcePath>> productAsts,
      final List<GeneratedAST<TestSourcePath>> testAsts) {
    this.productAsts = productAsts;
    this.testAsts = testAsts;
    pathToAst = productAsts.stream()
        .collect(Collectors.toMap(GeneratedAST::getSourcePath, v -> v));
    this.messageDigest = createMessageDigest();
  }

  public List<GeneratedAST<ProductSourcePath>> getProductAsts() {
    log.debug("enter getProductAsts()");
    return productAsts;
  }

  public List<GeneratedAST<TestSourcePath>> getTestAsts() {
    return testAsts;
  }

  /**
   * 引数のソースコードに対応するASTを取得する
   */
  public GeneratedAST<ProductSourcePath> getProductAst(final ProductSourcePath path) {
    log.debug("enter getProductAst()");
    return pathToAst.get(path);
  }

  /**
   * 指定された行にあるASTのノードを推定する。候補が複数ある場合、ノードが表すソースコードが広い順にListに格納したものを返す。
   * 
   * @see GeneratedAST#inferLocations(int)
   */
  public List<ASTLocation> inferLocations(final ProductSourcePath path, final int lineNumber) {
    log.debug("enter inferLocations(SourcePath, int)");
    final GeneratedAST<ProductSourcePath> ast = getProductAst(path);
    if (ast == null) {
      return Collections.emptyList();
    }
    return ast.inferLocations(lineNumber);
  }

  /**
   * ProductASTにあるすべてのASTLocationを取得する
   */
  public List<ASTLocation> getAllLocations() {
    return productAsts.stream()
        .flatMap(v -> v.getAllLocations()
            .stream())
        .collect(Collectors.toList());
  }

  /**
   * ASTLocationが対応する行番号を推定する
   */
  public LineNumberRange inferLineNumbers(final ASTLocation location) {
    log.debug("enter inferLineNumbers(Location)");
    return location.inferLineNumbers();
  }

  public String getMessageDigest() {
    return messageDigest;
  }

  public boolean isGenerationSuccess() {
    return true;
  }

  public String getGenerationMessage() {
    return "";
  }

  private String createMessageDigest() {
    try {
      final MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

      productAsts.stream()
          .sorted(Comparator.comparing(v -> v.getSourcePath()
              .toString()))
          .map(GeneratedAST::getMessageDigest)
          .map(String::getBytes)
          .forEach(digest::update);

      return Hex.encodeHexString(digest.digest());

    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
