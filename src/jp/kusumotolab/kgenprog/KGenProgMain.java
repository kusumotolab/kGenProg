package jp.kusumotolab.kgenprog;

import java.util.ArrayList;
import java.util.List;

public class KGenProgMain {

    public static void main(String[] args) {
        // TODO: TargetProjectの名前の問題
        final TargetProject targetProject = new TargetProject();

        List<Variant> selectedVariants = new ArrayList<>();
        selectedVariants.add(new Variant());

        while (true) {
            if (isTimedOut() || reachedMaxGeneration() || isSuccess(selectedVariants)) {
                break;
            }
            List<Gene> genes = new ArrayList<>();
            for (Variant variant : selectedVariants) {
                FaultLocalization faultLocalization = new FaultLocalization();
                List<Suspiciouseness> suspiciousenesses = faultLocalization.exec(targetProject, variant);

                Mutation mutation = new Mutation();
                List<Base> bases = mutation.exec(suspiciousenesses);
                genes.addAll(variant.getGene().generateNextGenerationGenes(bases));
            }

            Crossover crossover = new Crossover();
            genes.addAll(crossover.exec(selectedVariants));

            List<Variant> variants = new ArrayList<>();
            for (Gene gene : genes) {
                SourceCodeGeneration sourceCodeGeneration = new SourceCodeGeneration();
                GeneratedSourceCode generatedSourceCode = sourceCodeGeneration.exec(gene, targetProject);

                SourceCodeValidation sourceCodeValidation = new SourceCodeValidation();
                Fitness fitness = sourceCodeValidation.exec(generatedSourceCode, targetProject);

                Variant variant = new Variant(gene, fitness, generatedSourceCode);
                variants.add(variant);
            }

            VariantSelection variantSelection = new VariantSelection();
            selectedVariants = variantSelection.exec(variants);
        }
    }
}
