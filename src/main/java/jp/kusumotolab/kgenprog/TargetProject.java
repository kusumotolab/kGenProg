package jp.kusumotolab.kgenprog;

import java.util.Collections;
import java.util.List;

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
	
	private List<GeneratedAST> constructAST(){
		return null;
	}
}
