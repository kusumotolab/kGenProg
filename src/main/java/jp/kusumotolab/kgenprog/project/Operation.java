package jp.kusumotolab.kgenprog.project;

public interface Operation {

  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, Location location);

  public GeneratedSourceCode applyDirectly(GeneratedSourceCode generatedSourceCode,
      Location location);
}
