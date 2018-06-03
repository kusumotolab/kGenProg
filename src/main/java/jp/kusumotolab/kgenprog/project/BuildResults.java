package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class BuildResults {

	public final boolean isBuildFailed;
	public final String outDir;
	// TODO コンパイルできないときのエラー情報はほんとにこの型でいいか？
	public final DiagnosticCollector<JavaFileObject> diagnostics;

	private final Map<Path, Set<Path>> sourceToClassMap;
	private final Map<Path, Path> classToSourceMap;
	private final Map<Path, Set<FullyQualifiedName>> sourceToFQNMap;
	private final Map<FullyQualifiedName, Path> fqnToSourceMap;

	private boolean isMappingAvaiable;

	public BuildResults(final boolean isBuildFailed, final String outDir,
			final DiagnosticCollector<JavaFileObject> diagnostics) {
		this.isBuildFailed = isBuildFailed;
		this.outDir = outDir;
		this.diagnostics = diagnostics;
		this.sourceToClassMap = new HashMap<>();
		this.classToSourceMap = new HashMap<>();
		this.fqnToSourceMap = new HashMap<>();
		this.sourceToFQNMap = new HashMap<>();
		this.isMappingAvaiable = true;
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

	public Set<FullyQualifiedName> getPathToFQNs(final Path pathToSource) {
		return this.sourceToFQNMap.get(pathToSource);
	}

	public void addMapping(final Path source, final FullyQualifiedName fqn) {

		Set<FullyQualifiedName> fqns = this.sourceToFQNMap.get(source);
		if (null == fqns) {
			fqns = new HashSet<>();
			this.sourceToFQNMap.put(source, fqns);
		}
		fqns.add(fqn);

		// TODO すでに同じfqnな別のsourceが登録されているかチェックすべき
		// 登録されている場合はillegalStateExceptionを投げるべき？
		this.fqnToSourceMap.put(fqn, source);
	}

	public Path getPathToSource(final Path pathToClass) {
		return this.classToSourceMap.get(pathToClass);
	}

	public Path getPathToSource(final FullyQualifiedName fqn) {
		return this.fqnToSourceMap.get(fqn);
	}

	public void setMappingAvailable(final boolean available) {
		this.isMappingAvaiable = available;
	}

	public boolean isMappingAvailable() {
		return this.isMappingAvaiable;
	}
}
