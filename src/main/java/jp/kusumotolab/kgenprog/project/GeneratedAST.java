package jp.kusumotolab.kgenprog.project;

import java.util.List;

// TODO: クラス名を再検討
public interface GeneratedAST {

  public String getSourceCode();

  public String getPrimaryClassName();

  public SourceFile getSourceFile();

  public List<Location> inferLocations(int lineNumber);
}
