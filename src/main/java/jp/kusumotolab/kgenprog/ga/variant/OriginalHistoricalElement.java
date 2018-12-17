package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Collections;
import java.util.List;

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
