package com.xkball.tin_tea_tech.util;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;
import com.xkball.tin_tea_tech.annotation_processor.CodecProviderProcessor;
import com.xkball.tin_tea_tech.util.jctree.DataJCTreeUtils;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;

import javax.annotation.Nullable;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public class CodecManager {
    
    private static boolean init = false;
    public static final Map<String, Map<String, Pair<Integer, String>>> CODECS = new HashMap<>();
    public static final Map<String, Map<String, Pair<Integer, String>>> MAP_CODECS = new HashMap<>();
    public static final Set<CodecProviderData> NEW_CODEC_PROVIDERS = new HashSet<>();
    public static final TypeNameFixer FIXER = new TypeNameFixer();
    private static JavacTrees trees;
    private static Enter enter;
    private static Elements elementUtils;
    private static Filer filer;
    
    
    public static void init(ProcessingEnvironment processingEnv) {
        if (init) return;
        init = true;
        
        var symTab = Symtab.instance(JCTreeUtils.getContext(processingEnv));
        FIXER.setEnv(symTab);
        trees = JavacTrees.instance(processingEnv);
        enter = Enter.instance(JCTreeUtils.getContext(processingEnv));
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        readFromMETA_INF();
        readFromSources();
        
    }
    
    @Nullable
    public static DataJCTreeUtils.ICodecRefGetter findCodecRef(String codecName, String typeName) {
        var map1 = CODECS.get(typeName);
        if (map1 != null) {
            var temp = map1.get(codecName);
            if (temp != null) return () -> JCTreeUtils.makeIdent(temp.snd);
        }
        var map2 = MAP_CODECS.get(typeName);
        if (map2 != null) {
            var temp = map2.get(codecName);
            return () -> JCTreeUtils.treeMaker.App(JCTreeUtils.treeMaker.Select(JCTreeUtils.makeIdent(temp.snd), JCTreeUtils.name("codec")));
        }
        return null;
    }
    
    public static void readFromMETA_INF() {
        //格式: 类型,,codec目标类名,,引用,,名称,,优先级
        for (var str : FileUtils.readResourcesAllLines("META-INF/com.xkball.codec_provider")) {
            var args = str.split(",,");
            if (args.length != 5) {
                System.out.printf("invaild codec : %s%n", str);
                continue;
            }
            var type = args[0];
            var codecTypeName = args[1];
            var codecRef = args[2];
            var codecName = args[3];
            var order = args[4].trim();
            try {
                Integer.parseInt(order);
            } catch (NumberFormatException e) {
                System.out.printf("invaild codec : %s%n", str);
                continue;
            }
            var codecOrder = Integer.parseInt(order);
            
            addCodec(type, codecTypeName, codecRef, codecName, codecOrder);
        }
    }
    
    public static void addCodec(String type, String codecTypeName, String codecRef, String codecName, int order) {
        BiFunction<String, Map<String, Pair<Integer, String>>, Map<String, Pair<Integer, String>>> remappingFunc = (k, v) -> {
            var pair = Pair.of(order, codecRef);
            if (v == null) {
                var result = new HashMap<String, Pair<Integer, String>>();
                result.put(codecName.isEmpty() ? "" : codecName, pair);
                return result;
            } else {
                v.compute(codecName, (kn, vn) -> vn == null ? pair : vn.fst < order ? vn : pair);
                return v;
            }
        };
        if ("Codec".equals(type)) {
            CODECS.compute(codecTypeName, remappingFunc);
        } else if ("MapCodec".equals(type)) {
            MAP_CODECS.compute(codecTypeName, remappingFunc);
        }
    }
    
    public static void readFromSources() {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        for (var env : enter.getEnvs()) {
            var tree = env.tree;
            if (!(tree instanceof JCTree.JCClassDecl classTree)) return;
            var classSymbol = classTree.sym;
            var className = classSymbol.flatName().toString();
            getImportsFromJavaSourceFile(classSymbol.sourcefile);
            CodecProviderProcessor.handleJCTree(className, classTree, false);
        }
        writeToMETA_INF();
    }
    
    public static void getImportsFromJavaSourceFile(JavaFileObject source) {
        try (var in = source.openInputStream()) {
            var unit = StaticJavaParser.parse(in);
            FIXER.setContextClass(unit);
        } catch (Exception e) {
            System.out.println("Failed parse java source: " + e.getMessage());
        }
    }
    
    public static void writeToMETA_INF() {
        var path = "META-INF/com.xkball.codec_provider";
        try {
            var file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", path);
            try (var out = file.openWriter()) {
                for (var data : NEW_CODEC_PROVIDERS) {
                    out.write(data.toString() + '\n');
                }
            }
            System.out.println("Writing Codec Providers to: "+file.toUri().toURL());
            //throw new RuntimeException("Writing Codec Providers to: "+file.toUri().toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public record CodecProviderData(String type, String typeName, String ref, String insName, int insOrder) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CodecProviderData that = (CodecProviderData) o;
            return insOrder == that.insOrder && Objects.equals(ref, that.ref) && Objects.equals(type, that.type) && Objects.equals(insName, that.insName) && Objects.equals(typeName, that.typeName);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(type, typeName, ref, insName, insOrder);
        }
        
        @Override
        public String toString() {
            return type + ",," + typeName + ",," + ref + ",," + insName + ",," + insOrder;
        }
    }
    
}