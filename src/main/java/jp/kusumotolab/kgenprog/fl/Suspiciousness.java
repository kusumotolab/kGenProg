package jp.kusumotolab.kgenprog.fl;

import jp.kusumotolab.kgenprog.project.ASTLocation;

public class Suspiciousness {

  // 疑惑値とその場所のリスト
  final private ASTLocation location;
  final private double value;

  public Suspiciousness(ASTLocation location, double value) {
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
