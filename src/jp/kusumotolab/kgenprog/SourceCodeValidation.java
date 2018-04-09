package jp.kusumotolab.kgenprog;

public interface SourceCodeValidation {

    public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project);
}
