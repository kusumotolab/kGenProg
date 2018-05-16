package jp.kusumotolab.kgenprog.project.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * A class loader that loads classes from in-memory data.
 * 
 * @see https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
 */
public class MemoryClassLoader extends URLClassLoader {

	private final Map<String, byte[]> definitions = new HashMap<>();

	public MemoryClassLoader(String[] classpathes) {
		super(new URL[] {});
	}

	public MemoryClassLoader(URL[] classpathes) {
		super(classpathes);
	}

	/**
	 * Add a in-memory representation of a class.
	 * 
	 * @param name name of the class
	 * @param bytes class definition
	 */
	public void addDefinition(final String name, final byte[] bytes) {
		definitions.put(name, bytes);
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final byte[] bytes = definitions.get(name);
		if (bytes != null) {
			return defineClass(name, bytes, 0, bytes.length);
		}
		return super.loadClass(name, resolve);
	}

}