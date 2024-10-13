package com.xkball.tin_tea_tech.annotation_processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.xkball.tin_tea_tech.api.annotation.DataField;
import com.xkball.tin_tea_tech.api.annotation.DataSyncAdapter;
import com.xkball.tin_tea_tech.util.CodecManager;
import com.xkball.tin_tea_tech.util.jctree.DataJCTreeUtils;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;


import java.util.Objects;
import java.util.Set;

import static com.xkball.tin_tea_tech.util.jctree.JCTreeUtils.*;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({
        DataFieldProcessor.DATA_FIELD
})
public class DataFieldProcessor extends AbstractProcessor {
    
    public static final String DATA_FIELD = "com.xkball.tin_tea_tech.api.annotation.DataField";
    public static final java.util.List<String> handledClasses = new ArrayList<>();
    
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
        roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(DATA_FIELD)).forEach(
                anno -> {
                    var classSymbol = JCTreeUtils.findClassSymbolRecursive((Symbol) anno);
                    if (classSymbol == null) return;
                    var fullName = classSymbol.getQualifiedName().toString();
                    var tree = trees.getTree(elementUtils.getTypeElement(fullName));
                    handleClass(fullName, tree);
                }
        );
        return false;
    }
    
    public static void handleClass(String className, JCTree.JCClassDecl tree) {
        if (handledClasses.contains(className)) return;
        handledClasses.add(className);
        JCTreeUtils.setPos(tree);
        var fields = JCTreeUtils.findFieldWithAnno(tree,DATA_FIELD);
        var fieldsData = fields.stream().map(f -> DataJCTreeUtils.DataFieldData.fromAnno(className,f)).toList();
        var saveList = List.from(fieldsData.toArray(DataJCTreeUtils.DataFieldData[]::new));
        var s2cList = List.from(fieldsData.stream().filter(d -> d.syncPolicy() != DataField.SyncPolicy.SyncBoth).toArray(DataJCTreeUtils.DataFieldData[]::new));
        var c2sList = List.from(fieldsData.stream().filter(d -> d.syncPolicy() == DataField.SyncPolicy.SyncBoth).toArray(DataJCTreeUtils.DataFieldData[]::new));
        tree.defs = tree.defs.append(DataJCTreeUtils.createDataClass(className,"SaveData",saveList));
        tree.defs = tree.defs.append(DataJCTreeUtils.createDataClass(className,"Sync2ClientData",s2cList));
        tree.defs = tree.defs.append(DataJCTreeUtils.createDataClass(className,"SyncBothData",c2sList));
        handleISyncDataOfNBTTag(className,tree,saveList,s2cList,c2sList);
    }
    
    public static void handleISyncDataOfNBTTag(String className, JCTree.JCClassDecl tree, List<DataJCTreeUtils.DataFieldData> saveList, List<DataJCTreeUtils.DataFieldData> s2cList, List<DataJCTreeUtils.DataFieldData> c2sList) {
        if(tree.implementing.stream().noneMatch(impl -> "ISyncDataOfNBTTag".equals(impl.toString()))) return;
        var saveCodecRef = className + ".SaveData.CODEC";
        var s2cCodecRef = className + ".Sync2ClientData.CODEC";
        var c2sCodecRef = className + ".SyncBothData.CODEC";
        
        var returnCodecType = treeMaker.TypeApply(makeIdent("com.mojang.serialization.Codec"),List.of(treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.UNBOUND),null)));
        var saveCodecMethod = treeMaker.MethodDef(mods(Flags.PUBLIC),name("saveCodec"),returnCodecType,List.nil(),List.nil(),List.nil(),treeMaker.Block(0,List.of(treeMaker.Return(makeIdent(saveCodecRef)))),null);
        var s2cCodecMethod = treeMaker.MethodDef(mods(Flags.PUBLIC),name("server2clientCodec"),returnCodecType,List.nil(),List.nil(),List.nil(),treeMaker.Block(0,List.of(treeMaker.Return(makeIdent(s2cCodecRef)))),null);
        var c2sCodecMethod = treeMaker.MethodDef(mods(Flags.PUBLIC),name("client2serverCodec"),returnCodecType,List.nil(),List.nil(),List.nil(),treeMaker.Block(0,List.of(treeMaker.Return(makeIdent(c2sCodecRef)))),null);
        //addAnnotation2Methods("java.lang.Override",List.nil(),saveCodecMethod,s2cCodecMethod,c2sCodecMethod);
        tree.defs = tree.defs.appendList(List.of(saveCodecMethod,s2cCodecMethod,c2sCodecMethod));
        
        if(!saveList.isEmpty()){
            tree.defs = tree.defs.append(DataJCTreeUtils.createReadMethod(className,"SaveData","load",saveCodecRef,saveList));
            tree.defs = tree.defs.append(DataJCTreeUtils.createWriteMethod(className,"SaveData","save",saveCodecRef,saveList));
        }
        if(!s2cList.isEmpty()){
            tree.defs = tree.defs.append(DataJCTreeUtils.createReadMethod(className,"Sync2ClientData","readS2C",s2cCodecRef,s2cList));
            tree.defs = tree.defs.append(DataJCTreeUtils.createWriteMethod(className,"Sync2ClientData","writeS2C",s2cCodecRef,s2cList));
        }
        if(!c2sList.isEmpty()){
            tree.defs = tree.defs.append(DataJCTreeUtils.createReadMethod(className,"SyncBothData","readC2S",c2sCodecRef,c2sList));
            tree.defs = tree.defs.append(DataJCTreeUtils.createWriteMethod(className,"SyncBothData","writeC2S",c2sCodecRef,c2sList));
        }
        handleSyncAdapter(className,tree);
    }
    
    public static void handleSyncAdapter(String className, JCTree.JCClassDecl tree){
        if(JCTreeUtils.findAnnotation(tree,"com.xkball.tin_tea_tech.api.annotation.DataSyncAdapter") == null) return;
        var annoArg = Objects.requireNonNull(findAnnotation(tree, "com.xkball.tin_tea_tech.api.annotation.DataSyncAdapter")).args;
        var type = DataSyncAdapter.Type.fromString(annoArg.stream().filter(arg -> arg instanceof JCTree.JCAssign assign && "value".equals(assign.lhs.toString())).map(arg -> ((JCTree.JCAssign)arg).rhs.toString()).findFirst().orElseThrow());
        if(type == DataSyncAdapter.Type.BlockEntity){
            tree.defs = tree.defs.appendList(DataJCTreeUtils.createBlockEntityOverrides());
        }
    }
}
