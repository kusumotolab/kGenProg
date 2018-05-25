package jp.kusumotolab.kgenprog.project.test;

import java.nio.file.Path;

import jp.kusumotolab.kgenprog.project.SourceFile;

public class TestFullyQualifiedName extends FullyQualifiedName {

	public TestFullyQualifiedName(final String value) {
		super(value);
	}

	public TestFullyQualifiedName(final SourceFile sourceFile) {
		super(sourceFile);
	}

	public TestFullyQualifiedName(final Path path) {
		super(path);
	}
}
