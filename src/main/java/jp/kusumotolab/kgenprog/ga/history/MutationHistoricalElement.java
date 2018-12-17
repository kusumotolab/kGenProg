package jp.kusumotolab.kgenprog.ga.history;

import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class MutationHistoricalElement implements HistoricalElement {

  private final Variant parent;
  private final Base appendedBase;

  public MutationHistoricalElement(final Variant parent, final Base appendedBase) {
    this.parent = parent;
    this.appendedBase = appendedBase;
  }

  @Override
  public List<Variant> getParents() {
    return Collections.singletonList(parent);
  }

  @Override
  public String getOperationName() {
    return appendedBase.getOperation()
        .getName();
  }

  public Base getAppendedBase() {
    return appendedBase;
  }
}
