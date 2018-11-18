package jp.kusumotolab.kgenprog.project;

// TODO: クラス名を再検討
public interface GeneratedAST<T extends SourcePath> {

  public String getSourceCode();

  public FullyQualifiedName getPrimaryClassName();

  public T getSourcePath();

  public ASTLocations createLocations();

  public String getMessageDigest();

  public int getNumberOfLines();
}
