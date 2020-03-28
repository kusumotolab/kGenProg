package jp.kusumotolab.kgenprog.project;

// TODO: クラス名を再検討
public interface GeneratedAST<T extends SourcePath> {

  String getSourceCode();

  FullyQualifiedName getPrimaryClassName();

  T getSourcePath();

  ASTLocations createLocations();

  String getMessageDigest();

  int getNumberOfLines();
}
