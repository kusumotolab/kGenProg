package jp.kusumotolab.kgenprog;

public class Variant {
	
	private final Gene gene;
	private final Fitness fitness;
	private final GeneratedSourceCode generatedSourceCode;
	
	public Variant(final Gene gene, final Fitness fitness, final GeneratedSourceCode generatedSourceCode) {
		this.gene = gene;
		this.fitness = fitness;
		this.generatedSourceCode = generatedSourceCode;
	}
	
	
	
    public Gene getGene() {
		return gene;
	}
    public Fitness getFitness() {
		return fitness;
	}
    public GeneratedSourceCode getGeneratedSourceCode() {
		return generatedSourceCode;
	}
}
