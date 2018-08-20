package jp.kusumotolab.kgenprog.project.build;

import com.google.common.base.Objects;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Stores Java source code from a String into a JavaFileObject.
 * 
 * This class is responsible for creating a Java File Object from a string
 * containing the Java source code.
 */
public class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;
    private final String className;

    public JavaSourceFromString(String className, String javaSourceCode) {
        super(URI.create("string:///" + className.replace('.', '/')
                + Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.code = javaSourceCode;
    }

    @Override
    public final CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    @Override
    public final String getName() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JavaSourceFromString that = (JavaSourceFromString) o;
        return Objects.equal(code, that.code) &&
                Objects.equal(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code, className);
    }
}