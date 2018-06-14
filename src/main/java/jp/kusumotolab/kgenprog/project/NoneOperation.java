package jp.kusumotolab.kgenprog.project;

public class NoneOperation implements Operation {

  @Override
  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, Location location) {
    return generatedSourceCode;
  }

  @Override
  public GeneratedSourceCode applyDirectly(GeneratedSourceCode generatedSourceCode,
      Location location) {
    return generatedSourceCode;
  }
}
