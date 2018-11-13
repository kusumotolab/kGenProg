package jp.kusumotolab.kgenprog.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Hex;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {

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

  public List<GeneratedAST<?>> getAllAsts() {
    final List<GeneratedAST<?>> list = new ArrayList<>();
    list.addAll(productAsts);
    list.addAll(testAsts);
    return list;
  }

  public List<GeneratedAST<ProductSourcePath>> getProductAsts() {
    return productAsts;
  }

  public List<GeneratedAST<TestSourcePath>> getTestAsts() {
    return testAsts;
  }

  /**
   * 引数のソースコードに対応するASTを取得する
   */
  public GeneratedAST<ProductSourcePath> getProductAst(final ProductSourcePath path) {
    return pathToAst.get(path);
  }

  /**
   * ASTLocationが対応する行番号を推定する
   */
  public LineNumberRange inferLineNumbers(final ASTLocation location) {
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
