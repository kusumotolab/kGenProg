package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public class CrossoveredGene extends Gene {

	private Gene parentA;
	private Gene parentB;

	//	hitori
	public CrossoveredGene(Gene parentA, Gene parentB) {
		this.parentA = parentA;
		this.parentB = parentB;
	}

	@Override
	public List<Base> getBases() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public List<Gene> generateNextGenerationGenes(List<Base> bases) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
