package jp.kusumotolab.kgenprog.project.build;

import com.google.common.base.Objects;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * A compilation package compiles a list of compilation units. This is particularly useful
 * when you have (anonymous) inner classes inside your compiled code, in which case this package
 * will contain the main class and the anonymous classes, which allow all of them to be loaded
 * as a package when necessary.
 *
 * <p>
 * Example:
 * - MyClass
 * - MyClass$1
 * - MyClass$MyInnerClass
 * </p>
 */
public class CompilationPackage {
    private final List<CompilationUnit> units;

    public CompilationPackage(List<CompilationUnit> units) {
        this.units = newArrayList(units);
    }

    public List<CompilationUnit> getUnits() {
        return units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompilationPackage that = (CompilationPackage) o;
        return Objects.equal(units, that.units);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(units);
    }
}