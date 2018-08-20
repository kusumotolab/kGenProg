package jp.kusumotolab.kgenprog.project.build;

import com.google.common.annotations.VisibleForTesting;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A byte array class loader extends the standard class loader to transform an
 * array of bytes into a java.lang.Class.
 */
public class ByteArrayClassLoader extends ClassLoader {

    private byte[] bytecode;

    private ByteArrayClassLoader() {
        super(ByteArrayClassLoader.class.getClassLoader());
    }

    /**
     * Returns a Class object using the class name and its bytecode.
     * 
     * @param fqdn Fully qualified class name
     * @param byteCode Bytecode
     * @return Loaded class
     * @throws ClassNotFoundException Thrown when class not found
     */
    public Class loadClass(String fqdn, byte[] byteCode)
            throws ClassNotFoundException {
        setBytecode(byteCode.clone());
        return loadClass(fqdn);
    }

    @Override
    protected Class findClass(String name)
            throws ClassNotFoundException {
        Class<?> cls = null;

        try {
            if (bytecode != null) {
                cls = defineClass(name, bytecode);
            }
        } catch (ClassFormatError ex) {
            throw new ClassNotFoundException("Class name: " + name, ex);
        }

        return cls;
    }

    public static ByteArrayClassLoader newInstance() {
        return AccessController.doPrivileged(new BaclPrevilegedAction());
    }

    @VisibleForTesting
    Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, bytecode, 0, bytecode.length);
    }

    @VisibleForTesting
    void setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    @VisibleForTesting
    byte[] getBytecode() {
        return bytecode;
    }

    private static class BaclPrevilegedAction implements PrivilegedAction<ByteArrayClassLoader> {

        @Override
        public ByteArrayClassLoader run() {
            return new ByteArrayClassLoader();
        }

    }
}
