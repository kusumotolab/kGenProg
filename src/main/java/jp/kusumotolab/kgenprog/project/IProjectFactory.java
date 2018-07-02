package jp.kusumotolab.kgenprog.project;

public interface IProjectFactory {

  abstract public TargetProject create();

  abstract public boolean isApplicable();

}
