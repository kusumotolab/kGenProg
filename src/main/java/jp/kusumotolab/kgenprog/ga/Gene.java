package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;

public abstract class Gene {

    public abstract List<Base> getBases();
    public abstract List<Gene> generateNextGenerationGenes(List<Base> bases);
}

class TreeGene extends Gene {
	private Gene parent;
	private Base base;

	public TreeGene() {
		this(null, null);
	}

	public TreeGene(Gene parent, Base base) {
		this.parent = parent;
		this.base = base;
	}

	@Override
	public List<Base> getBases() {
		if (parent == null) {
			return new ArrayList<>();
		}
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
