package jp.kusumotolab.kgenprog.ga.variant;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.Operation;

/**
 * 遺伝子でいう塩基に当たる情報を保持するクラス
 * 塩基がもつ情報は，「どの AST ノード」に対して「どのような操作」を施すか
 */
public class Base {

  private final ASTLocation targetLocation;
  private final Operation operation;

  public Base(ASTLocation targetLocation, Operation operation) {
    this.targetLocation = targetLocation;
    this.operation = operation;
  }

  /**
   * @return 修正する AST ノードの場所
   */
  public ASTLocation getTargetLocation() {
    return targetLocation;
  }

  /**
   * @return 適応する操作
   */
  public Operation getOperation() {
    return operation;
  }


}
