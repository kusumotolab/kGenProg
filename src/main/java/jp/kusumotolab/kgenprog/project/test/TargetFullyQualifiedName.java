package jp.kusumotolab.kgenprog.project.test;

import java.nio.file.Path;

import jp.kusumotolab.kgenprog.project.SourceFile;

public class TargetFullyQualifiedName extends FullyQualifiedName {

	public TargetFullyQualifiedName(final String value) {
		super(value);
	}

	public TargetFullyQualifiedName(final SourceFile sourceFile) {
		super(sourceFile);
	}

	public TargetFullyQualifiedName(final Path path) {
		super(path);
	}
}
