package com.xkball.tin_tea_tech.annotation_processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.xkball.tin_tea_tech.util.CodecManager;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;
import com.xkball.tin_tea_tech.util.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({
        CodecProviderProcessor.CODEC_PROVIDER
})
public class CodecProviderProcessor extends AbstractProcessor {
    
    public static final String CODEC_PROVIDER = "com.xkball.tin_tea_tech.api.annotation.CodecProvider";
    public static final String LOG_HEAD = "[TinTeaTechAnnotationProcessor.CodecProviderProcessor] ";
    private ProcessingEnvironment processingEnv;
    private Elements elementUtils;
    private Filer filer;
    private JavacTrees trees;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        CodecManager.init(processingEnv);
        this.processingEnv = processingEnv;
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.trees = JavacTrees.instance(processingEnv);
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        JCTreeUtils.setup(processingEnv);
        roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(CODEC_PROVIDER)).forEach(
                anno -> {
                    var classSymbol = JCTreeUtils.findClassSymbolRecursive((Symbol) anno);
                    if (classSymbol == null) return;
                    var fullName = classSymbol.getQualifiedName().toString();
                    var tree = trees.getTree(elementUtils.getTypeElement(fullName));
                    handleJCTree(fullName,tree,true);
                }
        );
        return false;
    }
    
    private static final Set<String> sourceHandledClasses = new HashSet<>();
    private static final Set<String> processorHandledClasses = new HashSet<>();
    
    public static void handleJCTree(String className, JCTree.JCClassDecl tree,boolean inAnnoProcessor){
        if(!inAnnoProcessor){
            if(sourceHandledClasses.contains(className)) return;
            System.out.println(LOG_HEAD + "Scanning " + className);
            sourceHandledClasses.add(className);
        }
        else {
            if(processorHandledClasses.contains(className)) return;
            System.out.println(LOG_HEAD + "Checking " + className);
            processorHandledClasses.add(className);
        }
        JCTreeUtils.findFieldWithAnno(tree,CODEC_PROVIDER)
            .forEach(def -> {
                if(!JCTreeUtils.isPublicStatic(def)) return;
                var codecType = StringUtils.splitTypeNameWithArg(def.vartype.toString());
                var typeName = StringUtils.fixTypeNameWithArgRecursive(className,codecType.snd);
                var codecRef = className+"."+def.name.toString();
                var anno = Objects.requireNonNull(JCTreeUtils.findAnnotation(def,CODEC_PROVIDER));
                var codecInsOrder = 0;
                var codecInsName = "";
                for(var arg : anno.args){
                    if(arg instanceof JCTree.JCAssign assign){
                        if(assign.lhs.toString().equals("name")) codecInsName = StringUtils.removeDoubleQuotes(assign.rhs.toString());
                        if(assign.lhs.toString().equals("order")) codecInsOrder = Integer.parseInt(assign.rhs.toString());
                    }
                }
                var data = new CodecManager.CodecProviderData(codecType.fst,typeName,codecRef,codecInsName,codecInsOrder);
                if(!CodecManager.NEW_CODEC_PROVIDERS.contains(data)){
                    if(inAnnoProcessor)
                        throw new IllegalArgumentException("Can not support codec provider from code generation!!! " + data);
                    CodecManager.NEW_CODEC_PROVIDERS.add(data);
                    CodecManager.addCodec(codecType.fst,typeName,codecRef,codecInsName,codecInsOrder);
                }
        });
    }

}
