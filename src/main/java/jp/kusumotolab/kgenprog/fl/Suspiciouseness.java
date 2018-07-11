package jp.kusumotolab.kgenprog.fl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.Location;

public class Suspiciouseness {

  private static Logger log = LoggerFactory.getLogger(Suspiciouseness.class);

  // 疑惑値とその場所のリスト
  final private Location location;
  final private double value;

  public Suspiciouseness(Location location, double value) {
    this.location = location;
    this.value = value;
  }

  public Location getLocation() {
    log.debug("enter getLocation()");
    return location;
  }

  public double getValue() {
    log.debug("enter getValue()");
    return value;
  }
}
