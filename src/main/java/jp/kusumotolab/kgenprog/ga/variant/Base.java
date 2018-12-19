package jp.kusumotolab.kgenprog.ga.variant;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.Operation;

public class Base {

  final private ASTLocation targetLocation;
  final private Operation operation;

  public Base(ASTLocation targetLocation, Operation operation) {
    this.targetLocation = targetLocation;
    this.operation = operation;
  }

  public ASTLocation getTargetLocation() {
    return targetLocation;
  }

  public Operation getOperation() {
    return operation;
  }


}
