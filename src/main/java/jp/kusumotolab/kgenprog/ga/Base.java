package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Operation;

public class Base {

  private static Logger log = LoggerFactory.getLogger(Base.class);

  final private Location targetLocation;
  final private Operation operation;

  public Base(Location targetLocation, Operation operation) {
    this.targetLocation = targetLocation;
    this.operation = operation;
  }

  public Location getTargetLocation() {
    log.debug("enter getTargetLocation()");
    return targetLocation;
  }

  public Operation getOperation() {
    log.debug("enter getOperation()");
    return operation;
  }


}
