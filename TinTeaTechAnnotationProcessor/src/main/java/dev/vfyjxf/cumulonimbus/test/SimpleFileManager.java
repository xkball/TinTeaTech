package dev.vfyjxf.cumulonimbus.test;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SimpleFileManager
        extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final List<SimpleClassFile> compiled = new ArrayList<>();

    /**
     * Creates a new instance of {@code ForwardingJavaFileManager}.
     *
     * @param fileManager delegate to this file manager
     */
    protected SimpleFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    // standard constructors/getters

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
                                               String className, JavaFileObject.Kind kind, FileObject sibling) {
        SimpleClassFile result = new SimpleClassFile(
                URI.create("string://" + className));
        compiled.add(result);
        return result;
    }

    public List<SimpleClassFile> getCompiled() {
        return compiled;
    }
}
