package jp.kusumotolab.kgenprog.project;

import java.nio.charset.Charset;

// TODO: クラス名を再検討
public interface GeneratedAST<T extends SourcePath> {

  String getSourceCode();

  FullyQualifiedName getPrimaryClassName();

  T getSourcePath();

  ASTLocations createLocations();

  String getMessageDigest();

  Charset getCharset();

  int getNumberOfLines();
}
