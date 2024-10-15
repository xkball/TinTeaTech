package com.xkball.tin_tea_tech.util.jctree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.xkball.tin_tea_tech.util.Types;

import org.jetbrains.annotations.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class JCTreeUtils {
    
    public static JavacTrees trees;
    public static Elements elementUtils;
    public static TreeMaker treeMaker;
    public static Names names;
    
    public static void setup(ProcessingEnvironment processingEnv) {
        JCTreeUtils.trees = JavacTrees.instance(processingEnv);
        JCTreeUtils.elementUtils = processingEnv.getElementUtils();
        var context = getContext(processingEnv);
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }
    
    public static Context getContext(ProcessingEnvironment processingEnv) {
        try {
            var f = processingEnv.getClass().getDeclaredField("context"); // 得到 context
            f.setAccessible(true);
            return  (Context) f.get(processingEnv);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Nullable
    public static Symbol.ClassSymbol findClassSymbolRecursive(@Nullable Symbol symbol){
        if(symbol == null) return null;
        if(symbol instanceof Symbol.ClassSymbol classSymbol) return classSymbol;
        return findClassSymbolRecursive(symbol.owner);
    }
    
    public static JCTree.JCIdent ident(String name){
        return treeMaker.Ident(names.fromString(name));
    }
    
    public static Name name(String name){
        return names.fromString(name);
    }
    
    public static JCTree.JCModifiers noMods(){
        return treeMaker.Modifiers(0);
    }
    
    public static JCTree.JCModifiers mods(long... flag){
        long flags = 0;
        for(long l : flag) flags |= l;
        return treeMaker.Modifiers(flags);
    }
    
    public static JCTree.JCFieldAccess select(String selected,String selector){
        return treeMaker.Select(makeIdent(selected), name(selector));
    }
    
    public static JCTree.JCExpression makeIdent(String name){
        //name = name.replace('$','.');
        var ele = name.split("\\.");
        JCTree.JCExpression e = treeMaker.Ident(names.fromString(ele[0]));
        for (int i = 1; i < ele.length; i++) {
            e = treeMaker.Select(e, names.fromString(ele[i]));
        }
        return e;
    }
    
    public static JCTree.JCLambda makeLambda(List<String> params,JCTree body){
        var p = List.<JCTree.JCVariableDecl>nil();
        for (var str : params){
            p = p.append(treeMaker.VarDef(mods(Flags.PARAMETER),name(str),null,null));
        }
        return treeMaker.Lambda(p,body);
    }
    
    public static JCTree.JCMethodInvocation makeApply(JCTree.JCExpression fn, List<JCTree.JCExpression> args){
        return treeMaker.Apply(List.nil(),fn,args);
    }
    
    @Nullable
    public static JCTree.JCAnnotation findAnnotation(JCTree.JCVariableDecl tree,String annoName){
        return tree.mods.annotations.stream().filter(anno -> annoName.equals(anno.type.toString())).findFirst().orElse(null);
    }
    
    @Nullable
    public static JCTree.JCAnnotation findAnnotation(JCTree.JCClassDecl tree,String annoName){
        return tree.mods.annotations.stream().filter(anno -> annoName.equals(anno.type.toString())).findFirst().orElse(null);
    }
    
    public static void setPos(JCTree target){
        treeMaker.at(target.pos);
    }
    
    public static void increaseTreeMakerPos(){
        treeMaker.at(treeMaker.pos+1);
    }
    
    public static void addImplement2Class(JCTree.JCClassDecl target, String interfaceName) {
        target.implementing = target.implementing.append(makeIdent(interfaceName));
    }
    
    public static void addField2Class(JCTree.JCClassDecl target, String fieldType, String fieldName, JCTree.JCModifiers modifiers, JCTree.JCExpression initValue) {
        target.defs = target.defs.append(treeMaker.VarDef(modifiers,name(fieldName),makeIdent(fieldType),initValue));
    }
    
    public static void addAnnotation2Class(JCTree.JCClassDecl target, String annotationName,List<JCTree.JCExpression> args) {
        target.mods.annotations = target.mods.annotations.append(treeMaker.Annotation(makeIdent(annotationName), args));
    }
    
    public static void addAnnotation2Method(JCTree.JCMethodDecl target, String annotationName,List<JCTree.JCExpression> args) {
        target.mods.annotations = target.mods.annotations.append(treeMaker.Annotation(makeIdent(annotationName), args));
    }
    
    public static void addAnnotation2Methods(String annotationName,List<JCTree.JCExpression> args,JCTree.JCMethodDecl... target) {
        Arrays.stream(target).forEach(m -> addAnnotation2Method(m,annotationName,args));
    }
    
    public static void addMethod2Class(JCTree.JCClassDecl target, String methodName, JCTree.JCExpression returnType, JCTree.JCModifiers modifiers, List<JCTree.JCVariableDecl> args, JCTree.JCBlock block) {
        target.defs = target.defs.append(treeMaker.MethodDef(modifiers,name(methodName),returnType,List.nil(),args,List.nil(),block,null));
    }
    
    public static JCTree.JCMethodDecl findMethod(JCTree.JCClassDecl target,String methodName){
        return target.defs.stream().filter(t -> t instanceof JCTree.JCMethodDecl m && m.name.toString().equals(methodName)).map(t -> (JCTree.JCMethodDecl) t).findFirst().orElseThrow();
    }
    
    public static void addModBusSubscriber(JCTree.JCClassDecl target, String modid){
        var modid_ = treeMaker.Assign(ident("modid"), treeMaker.Literal(modid));
        var bus_ = treeMaker.Assign(ident("bus"), treeMaker.Select(makeIdent(Types.EVENT_BUS_SUBSCRIBER+".Bus"),name("MOD")));
        addAnnotation2Class(target,Types.EVENT_BUS_SUBSCRIBER,List.of(modid_,bus_));
    }
    
    public static java.util.List<JCTree.JCVariableDecl> findFieldWithAnno(JCTree.JCClassDecl target, String annoName){
        return target.defs.stream()
                .filter( def -> def instanceof JCTree.JCVariableDecl vd &&
                        vd.mods.annotations.stream().anyMatch(anno -> annoName.equals(anno.type.toString())))
                .map( def -> (JCTree.JCVariableDecl)def )
                .toList();
    }
    
    public static boolean isPublicStatic(JCTree.JCVariableDecl del){
        return isPublicStatic(del.mods.flags);
    }
    
    public static boolean isPublicStatic(long flag){
        return Modifier.isPublic((int) flag) && Modifier.isStatic((int) flag);
    }
}
