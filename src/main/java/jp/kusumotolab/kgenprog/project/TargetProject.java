package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class TargetProject {
	private final String basePath;
	private final List<SourceFile> sourceFiles;
	private final List<SourceFile> testFiles;
	private final List<ClassPath> classPaths;

	// stub for compatibility
	public TargetProject(List<SourceFile> sourceFiles, List<SourceFile> testFiles, List<ClassPath> classPaths) {
		this("", sourceFiles, testFiles, classPaths);
	}

	public TargetProject(String basePath, List<SourceFile> sourceFiles, List<SourceFile> testFiles,
			List<ClassPath> classPaths) {
		this.basePath = basePath;
		this.sourceFiles = sourceFiles;
		this.testFiles = testFiles;
		this.classPaths = classPaths;
	}

	public List<SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	public List<SourceFile> getTestFiles() {
		return testFiles;
	}

	public List<ClassPath> getClassPaths() {
		return classPaths;
	}

	public Variant getInitialVariant() {
		Gene gene = new SimpleGene(Collections.emptyList());
		Fitness fitness = null;
		GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(constructAST());

		return new Variant(gene, fitness, generatedSourceCode);
	}

	// hitori
	private List<GeneratedAST> constructAST() {
		// TODO: ここにDIする方法を検討
		return new JDTASTConstruction().constructAST(this);
	}

	/**
	 * 指定のbasepathからTargetProjectを生成するstatic factoryメソッド．
	 * 単体テスト等でTargetProject生成を何度も行うので利便性のために用意．
	 * testFilesの判定は適当．
	 * 
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	public static TargetProject generate(String basePath) {
		final List<SourceFile> sourceFiles = new ArrayList<>();
		final List<SourceFile> testFiles = new ArrayList<>();

		final String[] extension = { "java" };
		final Collection<File> files = FileUtils.listFiles(new File(basePath), extension, true);
		for (File file : files) {
			if (file.getName().endsWith("Test.java")) {
				testFiles.add(new SourceFile(file.getPath()));
			} else {
				sourceFiles.add(new SourceFile(file.getPath()));
			}
		}

		final List<ClassPath> classPath = Arrays.asList( //
				new ClassPath("lib/junit4/junit-4.12.jar"), //
				new ClassPath("lib/junit4/hamcrest-core-1.3.jar"));
		return new TargetProject(basePath, sourceFiles, testFiles, classPath);
	}
}
