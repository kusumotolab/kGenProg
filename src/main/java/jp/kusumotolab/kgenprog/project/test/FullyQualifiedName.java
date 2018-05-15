package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.Serializable;

import jp.kusumotolab.kgenprog.project.SourceFile;

public class FullyQualifiedName implements Serializable {
	private static final long serialVersionUID = 1L;

	final public String value;

	public FullyQualifiedName(String value) {
		this.value = value;
	}

	public FullyQualifiedName(SourceFile s) {
		this(s.path.substring(0, s.path.lastIndexOf('.')).replace("/", "."));
	}
}
