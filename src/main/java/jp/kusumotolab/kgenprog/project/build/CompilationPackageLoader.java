package jp.kusumotolab.kgenprog.project.build;

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Loads a compilation package into a list of Class instances.
 */
public class CompilationPackageLoader {

    /**
     * Returns the compiled classes.
     *
     * @param pkg Compilation package
     * @return Compiled classes
     * @throws ClassNotFoundException Thrown when class not found
     */
    public List<Class<?>> load(CompilationPackage pkg) throws ClassNotFoundException {
        ByteArrayClassLoader bacl = newByteArrayClassLoader();
        List<Class<?>> loadedClasses = newArrayList();

        for (CompilationUnit unit : pkg.getUnits()) {
            Class<?> cls = bacl.loadClass(unit.getName(), unit.getBytecode());
            loadedClasses.add(cls);
        }

        return loadedClasses;
    }

    /**
     * Loads the compiled classes into a map indexed by class name and returns that map.
     * @param pkg Compilation package
     * @return Map of compiled classes indexed by name
     * @throws ClassNotFoundException Thrown when class not found
     */
    public Map<String, Class<?>> loadAsMap(CompilationPackage pkg) throws ClassNotFoundException {
        List<Class<?>> classes = load(pkg);
        return Maps.uniqueIndex(classes, BY_CLASS_NAME);
    }

    private static final Function<Class, String> BY_CLASS_NAME = new Function<Class, String>() {
        @Override
        public String apply(Class aClass) {
            return aClass.getName();
        }
    };

    @VisibleForTesting
    ByteArrayClassLoader newByteArrayClassLoader() {
        return ByteArrayClassLoader.newInstance();
    }
}
