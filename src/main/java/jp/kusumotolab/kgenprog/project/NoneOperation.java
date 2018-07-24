package jp.kusumotolab.kgenprog.project;

public class NoneOperation implements Operation {

  @Override
  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, ASTLocation location) {
    return generatedSourceCode;
  }

  @Override
  public GeneratedSourceCode applyDirectly(GeneratedSourceCode generatedSourceCode,
      ASTLocation location) {
    return generatedSourceCode;
  }
}
