package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.Operation;

public class Base {

  private static Logger log = LoggerFactory.getLogger(Base.class);

  final private ASTLocation targetLocation;
  final private Operation operation;

  public Base(ASTLocation targetLocation, Operation operation) {
    this.targetLocation = targetLocation;
    this.operation = operation;
  }

  public ASTLocation getTargetLocation() {
    log.debug("enter getTargetLocation()");
    return targetLocation;
  }

  public Operation getOperation() {
    log.debug("enter getOperation()");
    return operation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    result = result * prime + targetLocation.hashCode();
    result = result * prime + operation.getName()
        .hashCode();

    return result;
  }


}
