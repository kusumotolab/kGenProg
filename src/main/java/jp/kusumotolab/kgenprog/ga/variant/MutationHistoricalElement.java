package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Collections;
import java.util.List;

/**
 * 変異の生成を記録するクラス
 */
public class MutationHistoricalElement implements HistoricalElement {

  private final Variant parent;
  private final Base appendedBase;

  /**
   * @param parent 親の個体
   * @param appendedBase 追加された塩基
   */
  public MutationHistoricalElement(final Variant parent, final Base appendedBase) {
    this.parent = parent;
    this.appendedBase = appendedBase;
  }

  /**
   * @return 親の個体
   */
  @Override
  public List<Variant> getParents() {
    return Collections.singletonList(parent);
  }

  /**
   * @return 適用された変異操作
   */
  @Override
  public String getOperationName() {
    return appendedBase.getOperation()
        .getName();
  }

  /**
   * @return 適用した塩基
   */
  public Base getAppendedBase() {
    return appendedBase;
  }
}
