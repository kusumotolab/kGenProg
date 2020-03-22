package jp.kusumotolab.kgenprog.project.factory;

public interface ProjectFactory {

  /**
   * TargetProjectを生成する．
   *
   * @return TargetProject
   */
  TargetProject create();

  /**
   * 当該ファクトリによるTargetProjectの生成が適用できるかどうかを判定する．
   *
   * @return 適用の可否
   */
  boolean isApplicable();

}
