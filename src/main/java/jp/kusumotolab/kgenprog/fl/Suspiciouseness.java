package jp.kusumotolab.kgenprog.fl;

import jp.kusumotolab.kgenprog.project.Location;

public class Suspiciouseness {
  // 疑惑値とその場所のリスト
  final private Location location;
  final private double value;

  public Suspiciouseness(Location location, double value) {
    this.location = location;
    this.value = value;
  }

  public Location getLocation() {
    return location;
  }

  public double getValue() {
    return value;
  }
}
