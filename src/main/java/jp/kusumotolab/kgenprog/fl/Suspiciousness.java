package jp.kusumotolab.kgenprog.fl;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.test.TestResult;

public class Suspiciousness {

  private static Logger log = LoggerFactory.getLogger(Suspiciousness.class);

  // 疑惑値とその場所のリスト
  final private ASTLocation location;
  final private double value;

  public Suspiciousness(ASTLocation location, double value) {
    this.location = location;
    this.value = value;
  }

  public ASTLocation getLocation() {
    log.debug("enter getLocation()");
    return location;
  }

  public double getValue() {
    log.debug("enter getValue()");
    return value;
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Suspiciousness suspiciousness = (Suspiciousness) o;
    return Objects.equals(location, suspiciousness.location) &&
        Objects.equals(value, suspiciousness.value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    result = result * prime + Double.hashCode(value);
    result = result * prime + location.hashCode();

    return result;
  }
}
