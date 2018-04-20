package jp.kusumotolab.kgenprog.project;

import java.util.Collections;
import java.util.List;

import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;

public class TargetProject {
	private List<SourceFile> sourceFiles;
	private List<SourceFile> testFiles;
	private List<ClassPath> classPaths;

	public TargetProject(List<SourceFile> sourceFiles, List<SourceFile> testFiles, List<ClassPath> classPaths){
		this.sourceFiles = sourceFiles;
		this.testFiles = testFiles;
		this.classPaths = classPaths;
	}

	public Variant getInitialVariant(){
		Gene gene = new SimpleGene(Collections.emptyList());
		Fitness fitness = null;
		GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(constructAST());

		return new Variant(gene, fitness, generatedSourceCode);
	}

//	hitori
	private List<GeneratedAST> constructAST(){
		return null;
	}
}
