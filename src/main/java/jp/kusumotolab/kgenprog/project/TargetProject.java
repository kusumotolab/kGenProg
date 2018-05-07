package jp.kusumotolab.kgenprog.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;

public class TargetProject {
	private final List<SourceFile> sourceFiles;
	private final List<SourceFile> testFiles;
	private final List<ClassPath> classPaths;

	public TargetProject(List<SourceFile> sourceFiles, List<SourceFile> testFiles, List<ClassPath> classPaths) {
		this.sourceFiles = sourceFiles;
		this.testFiles = testFiles;
		this.classPaths = classPaths;
	}

	public Variant getInitialVariant() {
		Gene gene = new SimpleGene(Collections.emptyList());
		Fitness fitness = null;
		GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(constructAST());

		return new Variant(gene, fitness, generatedSourceCode);
	}

	// hitori
	private List<GeneratedAST> constructAST() {
		return null;
	}

	public List<SourceFile> getSourceFiles() {
		final List<SourceFile> sourceFiles = new ArrayList<>(this.sourceFiles);
		return sourceFiles;
	}

	public List<SourceFile> getTestFiles() {
		final List<SourceFile> testFiles = new ArrayList<>(this.testFiles);
		return testFiles;
	}
	
	public List<ClassPath> getClassPaths(){
		final List<ClassPath> classpaths = new ArrayList<>(this.classPaths);
		return classpaths;
	}
}
