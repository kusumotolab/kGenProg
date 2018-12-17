package jp.kusumotolab.kgenprog.ga.history;

import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class OriginalHistoricalElement implements HistoricalElement {

  @Override
  public List<Variant> getParents() {
    return Collections.emptyList();
  }

  @Override
  public String getOperationName() {
    return "";
  }

}
