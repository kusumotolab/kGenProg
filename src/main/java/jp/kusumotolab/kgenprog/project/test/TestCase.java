package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.SourceFile;

public class TestCase {
	private SourceFile testFile;
	private String name;
	private int startLine;

	public TestCase(SourceFile testFile, String name, int startLine) {
		this.testFile = testFile;
		this.name = name;
		this.startLine = startLine;
	}
}
