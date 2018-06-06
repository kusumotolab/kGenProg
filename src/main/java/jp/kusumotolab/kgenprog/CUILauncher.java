package jp.kusumotolab.kgenprog;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.SiglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.TestSourceFile;

public class CUILauncher {

	// region Fields
	private SourceFile rootDir;
	private List<SourceFile> sourceFiles = new ArrayList<>();
	private List<SourceFile> testFiles = new ArrayList<>();
	private List<ClassPath> classPaths = new ArrayList<>();
	// endregion

	// region Getter/Setter

	public SourceFile getRootDir() {
		return rootDir;
	}

	@Option(name = "-r", aliases = "--root-dir", required = true, metaVar = "<path>", usage = "Path of a root directory of a target project")
	public void setRootDir(String rootDir) {
		this.rootDir = new TargetSourceFile(Paths.get(rootDir));
	}

	public List<SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	@Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class, metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
	public void setSourceFiles(String sourceFiles) {
		this.sourceFiles.add(new TargetSourceFile(Paths.get(sourceFiles)));
	}

	public List<SourceFile> getTestFiles() {
		return testFiles;
	}

	@Option(name = "-t", aliases = "--test", required = true, handler = StringArrayOptionHandler.class, metaVar = "<path> ...", usage = "Paths of the root directories holding test codes")
	public void setTestFiles(String testFiles) {
		this.testFiles.add(new TestSourceFile(Paths.get(testFiles)));
	}

	public List<ClassPath> getClassPaths() {
		return classPaths;
	}

	@Option(name = "-c", aliases = "--cp", required = true, handler = StringArrayOptionHandler.class, metaVar = "<class path> ...", usage = "Class paths required to build the target project")
	public void setClassPaths(String classPaths) {
		this.classPaths.add(new ClassPath(Paths.get(classPaths)));
	}

	// endregion

	public static void main(String[] args) {
		CUILauncher launcher = new CUILauncher();
		CmdLineParser parser = new CmdLineParser(launcher);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(1);
		}
		launcher.launch();
	}

	public void launch() {
		TargetProject targetProject = new TargetProject(getSourceFiles(), getTestFiles(), getClassPaths());
		FaultLocalization faultLocalization = new Ochiai();
		Mutation mutation = new RandomMutation();
		Crossover crossover = new SiglePointCrossover();
		SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
		SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
		VariantSelection variantSelection = new DefaultVariantSelection();

		KGenProgMain kGenProgMain = new KGenProgMain(targetProject, faultLocalization, mutation, crossover,
				sourceCodeGeneration, sourceCodeValidation, variantSelection);
		kGenProgMain.run();
	}
}
