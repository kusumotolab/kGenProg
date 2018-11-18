package jp.kusumotolab.kgenprog.project;

public interface ASTLocation {

  public static LineNumberRange NONE = new LineNumberRange(0, -1);

  public SourcePath getSourcePath();

  /**
   * このLocationが指すノードがソースコード中でどの位置にあるか、行番号の範囲を返す。 範囲が求められない場合、(0, -1)のRangeを返す
   *
   * @return 行番号の範囲
   */
  public LineNumberRange inferLineNumbers();
  
  public GeneratedAST<?> getGeneratedAST();
}
