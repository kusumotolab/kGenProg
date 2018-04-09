package jp.kusumotolab.kgenprog;

public interface SourceCodeGeneration {

    public GeneratedSourceCode exec(Gene gene, TargetProject targetProject);
}
