package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.TestSourceFile;

import org.junit.Test;

public class DefaultSourceCodeGenerationTest {

	@Test
	public void execTest() {
		final List<SourceFile> sourceCodeFiles = new ArrayList<>();
		sourceCodeFiles.add(new TargetSourceFile("example/example01/src/jp/kusumotolab/BuggyCalculator.java"));

		final List<SourceFile> testFiles = new ArrayList<>();
		testFiles.add(new TestSourceFile("example/example01/src/jp/kusumotolab/BuggyCalculatorTest.java"));

		final List<ClassPath> classPaths = new ArrayList<>();

		final TargetProject targetProject = new TargetProject(sourceCodeFiles, testFiles, classPaths);

		final DefaultSourceCodeGeneration defaultSourceCodeGeneration = new DefaultSourceCodeGeneration();
		final Gene gene = new SimpleGene(new ArrayList<>());

		// TODO: None以外のOperationでテストする必要有り
		final Base base = new Base(null, new NoneOperation());

		final List<Gene> genes = gene.generateNextGenerationGenes(Collections.singletonList(base));

		final GeneratedSourceCode generatedSourceCode = defaultSourceCodeGeneration.exec(genes.get(0), targetProject);

		// TODO: Noneしかないのでテストができない
		// System.out.println(generatedSourceCode.getFiles().get(0).getSourceCode());
	}
}