package dev.vfyjxf.cumulonimbus.test;



import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class TestCompiler {
    public byte[] compile(String qualifiedClassName, String testSource) throws IOException {
        StringWriter output = new StringWriter();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        SimpleFileManager fileManager = new SimpleFileManager(
                compiler.getStandardFileManager(null, null, null));
        List<SimpleSourceFile> compilationUnits
                = singletonList(new SimpleSourceFile(qualifiedClassName, testSource));
        List<String> arguments = new ArrayList<>();
//        arguments.addAll(asList("-classpath", System.getProperty("java.class.path"),
//                                "-Xplugin:" + CumulonimbusPlugin.NAME));
        JavaCompiler.CompilationTask task
                = compiler.getTask(output, fileManager, null, arguments, null,
                                   compilationUnits);
        task.call();
        System.out.write(output.getBuffer().toString().getBytes(StandardCharsets.UTF_8));
        return fileManager.getCompiled().getFirst().getCompiledBinaries();
    }
}
