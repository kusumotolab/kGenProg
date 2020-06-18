package jp.kusumotolab.kgenprog.fl;

import jp.kusumotolab.kgenprog.project.ASTLocation;

public class Suspiciousness {

  // 疑惑値とその場所のリスト
  private final ASTLocation location;
  private final double value;

  public Suspiciousness(final ASTLocation location, final double value) {
    this.location = location;
    this.value = value;
  }

  public ASTLocation getLocation() {
    return location;
  }

  public double getValue() {
    return value;
  }
}
