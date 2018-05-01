package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;

public class TreeGene extends Gene {
	private final Gene parent;
	private final Base base;

	public TreeGene(Gene parent, Base base) {
		if (parent == null || base == null)
			throw new NullPointerException("Parameters of Constructor of TreeGene must not be null.");
		this.parent = parent;
		this.base = base;
	}

	@Override
	public List<Base> getBases() {
		final List<Base> bases = parent instanceof TreeGene ? parent.getBases() : new ArrayList<>(parent.getBases());
		bases.add(base);
		return bases;
	}

	@Override
	public List<Gene> generateNextGenerationGenes(final List<Base> bases) {
		final List<Gene> genes = new ArrayList<>();
		for (Base base : bases) {
			genes.add(new TreeGene(this, base));
		}
		return genes;
	}
}
