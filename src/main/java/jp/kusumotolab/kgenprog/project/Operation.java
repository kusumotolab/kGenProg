package jp.kusumotolab.kgenprog.project;

public interface Operation {

  GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, ASTLocation location);

  default String getName() {
    return "";
  }

  default String getTargetSnippet() {
    return "";
  }
}
