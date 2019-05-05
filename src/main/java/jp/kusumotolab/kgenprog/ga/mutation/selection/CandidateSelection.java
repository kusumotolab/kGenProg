package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * 再利用候補の選択ををするインターフェース
 */
public interface CandidateSelection {

  /**
   * 再利用するソースコードをセットする
   *
   * @param candidates 再利用するソースコードのリスト
   */
  void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates);

  /**
   * 再利用する AST ノードを取り出す
   *
   * @param scope 再利用する候補ののスコープ
   * @return 再利用する AST ノード
   */
  ASTNode exec(final Scope scope);
}
