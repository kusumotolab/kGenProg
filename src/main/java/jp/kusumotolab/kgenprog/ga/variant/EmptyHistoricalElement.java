package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Collections;
import java.util.List;

public class EmptyHistoricalElement implements HistoricalElement {

  public static final EmptyHistoricalElement instance = new EmptyHistoricalElement();

  private EmptyHistoricalElement() {
  }

  @Override
  public List<Variant> getParents() {
    return Collections.emptyList();
  }

  @Override
  public String getOperationName() {
    return "";
  }
}
