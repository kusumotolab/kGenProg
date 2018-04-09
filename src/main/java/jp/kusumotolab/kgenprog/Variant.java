package jp.kusumotolab.kgenprog;

public abstract class Variant {

    public abstract Gene getGene();
    public abstract Fitness getFitness();
    public abstract GeneratedSourceCode getGeneratedSourceCode();
}
