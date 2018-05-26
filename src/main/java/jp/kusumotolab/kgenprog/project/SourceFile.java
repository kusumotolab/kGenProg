package jp.kusumotolab.kgenprog.project;

import java.io.File;

public abstract class SourceFile {
	// TODO
	// pathはjava.nio.Pathで管理すべき．

	public final String path;

	protected SourceFile(final String path) {
		this.path = new File(path).toString(); // for class-platform compatibility
	}

	@Override
	public boolean equals(Object o) {
		final File f = new File(((SourceFile) o).path);
		return new File(this.path).equals(f);
	}

	@Override
	public String toString() {
		return this.path;
	}
}
