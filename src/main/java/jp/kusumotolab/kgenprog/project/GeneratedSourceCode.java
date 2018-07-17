package jp.kusumotolab.kgenprog.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APR によって生成されたソースコード 複数ソースファイルの AST の集合を持つ
 */
public class GeneratedSourceCode {

  private static Logger log = LoggerFactory.getLogger(GeneratedSourceCode.class);
  private static final String DIGEST_ALGORITHM = "MD5";

  // TODO listは順序が保証されず重複を許容してしまう．Mapで名前から引ける方が外から使いやすい．
  private final List<GeneratedAST> files;
  private final Map<SourceFile, GeneratedAST> fileToAST;
  private final String messageDigest;

  public GeneratedSourceCode(List<GeneratedAST> files) {
    this.files = files;
    fileToAST = files.stream()
        .collect(Collectors.toMap(GeneratedAST::getSourceFile, v -> v));
    this.messageDigest = createMessageDigest();

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
    return files.stream()
        .flatMap(v -> v.getAllLocations()
            .stream())
        .collect(Collectors.toList());
  }

  public Range inferLineNumbers(Location location) {
    log.debug("enter inferLineNumbers(Location)");
    return location.inferLineNumbers();
  }

  public String getMessageDigest() {
    return messageDigest;
  }

  private String createMessageDigest() {
    try {
      final MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

      files.stream()
          .sorted(Comparator.comparing(v -> v.getSourceFile()
              .toString()))
          .map(GeneratedAST::getMessageDigest)
          .map(String::getBytes)
          .forEach(digest::update);

      return DatatypeConverter.printHexBinary(digest.digest());

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
