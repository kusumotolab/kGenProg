package jp.kusumotolab.kgenprog;

import java.util.ArrayList;
import java.util.List;

public class KGenProgMain {

	private TargetProject targetProject;
	private FaultLocalization faultLocalization;
	private Mutation mutation;
	private Crossover crossover;
	private SourceCodeGeneration sourceCodeGeneration;
	private SourceCodeValidation sourceCodeValidation;
	private VariantSelection variantSelection;

	public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization, Mutation mutation, Crossover crossover,
			SourceCodeGeneration sourceCodeGeneration, SourceCodeValidation sourceCodeValidation,
			VariantSelection variantSelection) {
		this.targetProject = targetProject;
		this.faultLocalization = faultLocalization;
		this.mutation = mutation;
		this.crossover = crossover;
		this.sourceCodeGeneration = sourceCodeGeneration;
		this.sourceCodeValidation = sourceCodeValidation;
		this.variantSelection = variantSelection;
	}

	public void run() {
		List<Variant> selectedVariants = new ArrayList<>();
		selectedVariants.add(targetProject.getInitialVariant());

		while (true) {
			if (isTimedOut() || reachedMaxGeneration() || isSuccess(selectedVariants)) {
				break;
			}
			List<Gene> genes = new ArrayList<>();
			for (Variant variant : selectedVariants) {
				List<Suspiciouseness> suspiciousenesses = faultLocalization.exec(targetProject, variant);

				List<Base> bases = mutation.exec(suspiciousenesses);
				genes.addAll(variant.getGene().generateNextGenerationGenes(bases));
			}

			genes.addAll(crossover.exec(selectedVariants));

			List<Variant> variants = new ArrayList<>();
			for (Gene gene : genes) {
				GeneratedSourceCode generatedSourceCode = sourceCodeGeneration.exec(gene, targetProject);

				Fitness fitness = sourceCodeValidation.exec(generatedSourceCode, targetProject);

				Variant variant = new Variant(gene, fitness, generatedSourceCode);
				variants.add(variant);
			}

			selectedVariants = variantSelection.exec(variants);
		}
	}

	private boolean reachedMaxGeneration() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isTimedOut() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isSuccess(List<Variant> variants) {
		return false;
	}
}
