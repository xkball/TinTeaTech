package com.xkball.tin_tea_tech.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class JavaParserUtils {
    
    public static String getClassName(TypeDeclaration<?> type, String str){
        var typeName = type.getName().getIdentifier();
        if(type.getParentNode().isEmpty()) return str + typeName;
        var parent = type.getParentNode().orElseThrow();
        if(parent instanceof TypeDeclaration<?> t) return getClassName(t,str) + "." + typeName;
        if(parent instanceof CompilationUnit unit) return unit.getPackageDeclaration().isPresent() ? unit.getPackageDeclaration().get().getNameAsString() + "." + typeName : str + typeName;
        return str.isEmpty() ? typeName : str + "." + typeName;
    }
    
    public static FieldDeclaration getAsField(Node node){
        return (FieldDeclaration) node;
    }
    
    public static TypeDeclaration<?> getAsClass(Node node){
        return (TypeDeclaration<?>) node;
    }
}
