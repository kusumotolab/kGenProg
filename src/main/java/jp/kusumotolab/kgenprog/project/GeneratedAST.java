package jp.kusumotolab.kgenprog.project;

import java.util.List;

// TODO: クラス名を再検討
public interface GeneratedAST<T extends SourcePath> {

  public String getSourceCode();

  public String getPrimaryClassName();

  public T getSourcePath();

  /**
   * 指定された行にあるASTのノードを推定する。候補が複数ある場合、ノードが表すソースコードが広い順にListに格納したものを返す。
   * 例えば以下のプログラムで{@code a = -a}の行を指定した場合、IfStatement、Block、ExpressionStatementの順に格納される。
   * 
   * <pre>
   * {@code
   * if (a < 0) {
   *   a = -a;
   * }
   * }
   * </pre>
   * 
   * @param lineNumber 行番号
   * 
   * @return 指定された行にあるASTノードを表すLocationのList
   */
  public List<ASTLocation> inferLocations(int lineNumber);

  public List<ASTLocation> getAllLocations();

  public String getMessageDigest();
}
