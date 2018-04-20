package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;

public class SimpleGene extends Gene {
	private List<Base> bases;
	
	public SimpleGene(List<Base> bases){
		this.bases = bases;
	}
	
	@Override
	public List<Base> getBases() {
		return bases;
	}

	@Override
	public List<Gene> generateNextGenerationGenes(List<Base> bases) {
		List<Gene> genes = new ArrayList<>();
		for(Base base : bases){
			List<Base> newBases = new ArrayList<>(this.bases);
			newBases.add(base);
			genes.add(new SimpleGene(newBases));
		}
		return genes;
	}
}
