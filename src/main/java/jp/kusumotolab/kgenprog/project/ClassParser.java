package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassVisitor;

public class ClassParser extends ClassVisitor {

	List<String> packageName;
	String sourceFileName;
	List<String> fqnClassName;

	public ClassParser(final int opcode) {
		super(opcode);
		this.packageName = new ArrayList<>();
		this.sourceFileName = "";
		this.fqnClassName = new ArrayList<>();
	}

	@Override
	public void visit(final int version, final int access, final String name, final String signature,
			final String superName, final String[] interfaces) {

		// packageName に対する処理
		final int index = name.lastIndexOf('/');
		if (0 < index) {
			Arrays.asList(name.substring(0, index).split("/")).stream().forEach(t -> this.packageName.add(t));
		}

		// fqnClassName に対する処理
		for (final String token : name.split("/")) {
			Arrays.asList(token.split("$")).stream().forEach(t -> this.fqnClassName.add(t));
		}
	}

	@Override
	public void visitSource(final String source, final String debug) {
		this.sourceFileName = source;
	}

	public String getPartialPath() {
		return String.join(File.separator, String.join(File.separator, this.packageName), this.sourceFileName);
	}

	public String getFQN(final String delimiter) {
		return String.join(delimiter, this.fqnClassName);
	}
}
