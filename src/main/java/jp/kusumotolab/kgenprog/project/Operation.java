package jp.kusumotolab.kgenprog.project;

public interface Operation {

  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, ASTLocation location);
}
