package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.ProgramElementVisitor;
import jp.kusumotolab.kgenprog.project.jdt.StatementListVisitor;

/**
 * 型を考慮してStatementを選ぶ．
 */
public class HeuristicStatementSelection extends HeuristicSelection {

  /**
   * @param random 乱数生成器
   */
  public HeuristicStatementSelection(final Random random) {
    super(random);
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> generatedASTs) {
    final ProgramElementVisitor visitor = new StatementListVisitor();
    super.setCandidates(generatedASTs, visitor);
  }
}
