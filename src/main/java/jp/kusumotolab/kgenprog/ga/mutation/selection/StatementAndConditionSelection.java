package jp.kusumotolab.kgenprog.ga.mutation.selection;

import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.ga.mutation.Query;

public abstract class StatementAndConditionSelection implements CandidateSelection {

  /**
   * 再利用する文もしくは条件式を取り出す
   *
   * @param query 再利用する候補のクエリ
   * @return 再利用する文もしくは条件式
   */
  public abstract ASTNode exec(final Query query);
}
