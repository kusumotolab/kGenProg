package jp.kusumotolab.kgenprog;

import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.*;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.util.ArrayList;
import java.util.List;

public class CUILauncher {

	// region Fields
	private List<SourceFile> sourceFiles = new ArrayList<>();
	private List<SourceFile> testFiles = new ArrayList<>();
	private List<ClassPath> classPaths = new ArrayList<>();
	// endregion

	// region Getter/Setter

	public List<SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	@Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class, metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
	public void setSourceFiles(String sourceFiles) {
		this.sourceFiles.add(new SourceFile(sourceFiles));
	}

	public List<SourceFile> getTestFiles() {
		return testFiles;
	}

	@Option(name = "-t", aliases = "--test", required = true, handler = StringArrayOptionHandler.class, metaVar = "<path> ...", usage = "Paths of the root directories holding test codes")
	public void setTestFiles(String testFiles) {
		this.testFiles.add(new SourceFile(testFiles));
	}

	public List<ClassPath> getClassPaths() {
		return classPaths;
	}

	@Option(name = "-c", aliases = "--cp", required = true, handler = StringArrayOptionHandler.class, metaVar = "<class path> ...", usage = "Class paths required to build the target project")
	public void setClassPaths(String classPaths) {
		this.classPaths.add(new ClassPath(classPaths));
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

		KGenProgMain kGenProgMain = new KGenProgMain(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection);
		kGenProgMain.run();
	}
}
