package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Operation;

public class Base {
  final private Location targetLocation;
  final private Operation operation;

  public Base(Location targetLocation, Operation operation) {
    this.targetLocation = targetLocation;
    this.operation = operation;
  }

  public Location getTargetLocation() {
    return targetLocation;
  }

  public Operation getOperation() {
    return operation;
  }


}
