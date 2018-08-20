package jp.kusumotolab.kgenprog.project.build;


import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.util.List;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * The standard JavaFileManager uses a simple implementation of type
 * JavaFileObject to read/write bytecode into class files. This class extends
 * the standard JavaFileManager to read/write bytecode into memory using a
 * custom implementation of the JavaFileObject.
 * 
 * @see JavaMemoryObject
 */
public class InMemoryClassManager extends
        ForwardingJavaFileManager<JavaFileManager> {

    private List<CompilationUnit> memory = newArrayList();

    public InMemoryClassManager(JavaFileManager fileManager) {
        super(fileManager);
    }
    
    @Override
    public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName)  throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String name, Kind kind, FileObject sibling) throws IOException {
        JavaMemoryObject co = new JavaMemoryObject(name, kind);
        CompilationUnit cf = new CompilationUnit(name, co);
        memory.add(cf);
        return co;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return false;
    }

    /**
     * Gets the bytecode as a list of compiled classes. If the source code
     * generates inner classes, these classes will be placed in front of the
     * returned list and the class associated to the source file will be the
     * last element in the list.
     * 
     * @return List of compiled classes
     */
    public List<CompilationUnit> getAllClasses() {
        return memory;
    }
}