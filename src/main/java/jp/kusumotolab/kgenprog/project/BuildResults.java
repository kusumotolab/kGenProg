package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class BuildResults {

	public final boolean isBuildFailed;
	public final String outDir;
	// TODO コンパイルできないときのエラー情報はほんとにこの型でいいか？
	public final DiagnosticCollector<JavaFileObject> diagnostics;

	private final Map<Path, Set<Path>> sourceToClassMap;
	private final Map<Path, Path> classToSourceMap;

	public BuildResults(final boolean isBuildFailed, final String outDir,
			final DiagnosticCollector<JavaFileObject> diagnostics) {
		this.isBuildFailed = isBuildFailed;
		this.outDir = outDir;
		this.diagnostics = diagnostics;
		this.sourceToClassMap = new HashMap<>();
		this.classToSourceMap = new HashMap<>();
	}

	public void addMapping(final Path pathToSource, final Path pathToClass) {
		Set<Path> pathToClasses = this.sourceToClassMap.get(pathToSource);
		if (null == pathToClasses) {
			pathToClasses = new HashSet<>();
			this.sourceToClassMap.put(pathToSource, pathToClasses);
		}
		pathToClasses.add(pathToClass);
		this.classToSourceMap.put(pathToClass, pathToSource);
	}

	public Set<Path> getPathToClasses(final Path pathToSource) {
		return this.sourceToClassMap.get(pathToSource);
	}

	public Path getPathToSource(final Path pathToClass) {
		return this.classToSourceMap.get(pathToClass);
	}

	@Deprecated
	public void addMaping(final Path source, final Collection<Path> classes) {
		this.sourceToClassMap.put(source, new HashSet<Path>(classes));
		classes.forEach(c -> this.classToSourceMap.put(c, source));
	}
}
