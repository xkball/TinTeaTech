package com.xkball.tin_tea_tech.util;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.util.Pair;
import com.xkball.tin_tea_tech.annotation_processor.CodecProviderProcessor;
import com.xkball.tin_tea_tech.api.annotation.CodecProvider;
import com.xkball.tin_tea_tech.util.jctree.DataJCTreeUtils;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;

import javax.annotation.Nullable;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import static com.xkball.tin_tea_tech.util.JavaParserUtils.*;

public class CodecManager {
    
    private static boolean init = false;
    public static final Map<String,Map<String, Pair<Integer,String>>> CODECS = new HashMap<>();
    public static final Map<String,Map<String, Pair<Integer,String>>> MAP_CODECS = new HashMap<>();
    public static final Set<CodecProviderData> NEW_CODEC_PROVIDERS = new HashSet<>();
    public static final TypeNameFixer FIXER = new TypeNameFixer();
    private static JavacTrees trees;
    private static Elements elementUtils;
    private static Filer filer;
    
    public static void init(ProcessingEnvironment processingEnv){
        if(init) return;
        init = true;
        
        var symTab = Symtab.instance(JCTreeUtils.getContext(processingEnv));
        FIXER.setEnv(symTab);
        trees = JavacTrees.instance(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        readFromMETA_INF();
        readFromSources();
        
    }
    
    @Nullable
    public static DataJCTreeUtils.ICodecRefGetter findCodecRef(String codecName, String typeName){
        var map1 = CODECS.get(typeName);
        if(map1 != null){
            var temp = map1.get(codecName);
            if(temp != null) return () -> JCTreeUtils.makeIdent(temp.snd);
        }
        var map2 = MAP_CODECS.get(typeName);
        if(map2 != null){
            var temp = map2.get(codecName);
            return () -> JCTreeUtils.treeMaker.App(JCTreeUtils.treeMaker.Select(JCTreeUtils.makeIdent(temp.snd),JCTreeUtils.name("codec")));
        }
        return null;
    }
    
    public static void readFromMETA_INF(){
        //格式: 类型,,codec目标类名,,引用,,名称,,优先级
        for(var str : FileUtils.readResourcesAllLines("META-INF/com.xkball.codec_provider")){
            var args = str.split(",,");
            if(args.length != 5){
                System.out.printf("invaild codec : %s%n",str);
                continue;
            }
            var type = args[0];
            var codecTypeName = args[1];
            var codecRef = args[2];
            var codecName = args[3];
            var order = args[4].trim();
            try {
                Integer.parseInt(order);
            }catch(NumberFormatException e){
                System.out.printf("invaild codec : %s%n",str);
                continue;
            }
            var codecOrder = Integer.parseInt(order);
            
            addCodec(type, codecTypeName, codecRef, codecName, codecOrder);
        }
    }
    
    public static void addCodec(String type, String codecTypeName, String codecRef, String codecName, int order){
        BiFunction<String,Map<String, Pair<Integer,String>>,Map<String, Pair<Integer,String>>> remappingFunc = (k,v) -> {
            var pair = Pair.of(order, codecRef);
            if(v == null){
                var result = new HashMap<String, Pair<Integer,String>>();
                result.put(codecName.isEmpty() ? "" : codecName, pair);
                return result;
            }
            else{
                v.compute(codecName, (kn,vn) -> vn == null ? pair : vn.fst < order ? vn : pair);
                return v;
            }
        };
        if("Codec".equals(type)){
            CODECS.compute(codecTypeName,remappingFunc);
        }
        else if("MapCodec".equals(type)){
            MAP_CODECS.compute(codecTypeName,remappingFunc);
        }
    }
    
    public static void readFromSources(){
        var tempDir = System.getenv("TEMP");
        if(tempDir == null || tempDir.isEmpty()) return;
        var file = new File(tempDir, "/com.xkball/tin_tea_tech.annotation_processor/temp.source_classes");
        if(!file.exists()) return;
        var typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        var symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        FileUtils.readAllLines(file.getPath()).forEach(CodecManager::handleJavaSourceFile);
        writeToMETA_INF();
    }
    
    public static void handleJavaSourceFile(String path){
        try (var in = new FileInputStream(path)) {
            var unit = StaticJavaParser.parse(in);
            FIXER.setContextClass(unit);
            unit.findAll(Node.class).stream()
                    .filter(node -> node instanceof NodeWithAnnotations)
                    .flatMap(node -> ((NodeWithAnnotations<?>) node).getAnnotations().stream())
                    .filter(annotation -> annotation.getName().getIdentifier().equals(CodecProvider.class.getSimpleName()))
                    .forEach(CodecManager::handleCodecProviderAnnotation);
        } catch (Exception e){
            System.out.println("Failed parse java source: " + e.getMessage());
        }
    }
    
    public static void handleCodecProviderAnnotation(AnnotationExpr annotation){
        try {
            var field = getAsField(annotation.getParentNode().orElseThrow());
            var clazz = getAsClass(field.getParentNode().orElseThrow());
            var className = getClassName(clazz,"");
            var tree = trees.getTree(elementUtils.getTypeElement(className));
            CodecProviderProcessor.handleJCTree(className,tree,false);
        }catch(Exception e){
            System.out.println("Failed parse annotation: " + e.getMessage());
        }
      
    }
    
    public static void writeToMETA_INF(){
        var path = "META-INF/com.xkball.codec_provider";
        try {
            var file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", path);
            try(var out = file.openWriter()){
                for(var data: NEW_CODEC_PROVIDERS){
                    out.write(data.toString()+'\n');
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public record CodecProviderData(String type,String typeName,String ref,String insName,int insOrder){
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


//            var variable = field.getVariables().getFirst().orElseThrow();
//            var className = getClassName(clazz,"");
//            var codecRef = className + "." + variable.getNameAsString();
//            var codecType = ((ClassOrInterfaceType)variable.getType());
//            var codecRawType = codecType.getName().getIdentifier();
//            var typeArg = codecType.getTypeArguments().orElseThrow().getFirst().orElseThrow();
//            var typeName = fixer.fixTypeName(typeArg.asString());
//            if(annotation instanceof MarkerAnnotationExpr){
//               addCodec(codecRawType,typeName,codecRef,"",0);
//            }
//            if(annotation instanceof NormalAnnotationExpr normalAnnotation){
//                var values = normalAnnotation.getPairs();
//                var order = 0;
//                var name = "";
//                for(var pair : values){
//                    if("order".equals(pair.getName().asString())) order = Integer.parseInt(pair.getValue().toString());
//                    if("name".equals(pair.getName().asString())) name = pair.getValue().asStringLiteralExpr().asString();
//                }
//                addCodec(codecRawType,typeName,codecRef,name,order);
//            }