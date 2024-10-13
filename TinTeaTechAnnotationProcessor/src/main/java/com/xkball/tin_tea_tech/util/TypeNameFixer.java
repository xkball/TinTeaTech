package com.xkball.tin_tea_tech.util;

import com.github.javaparser.ast.CompilationUnit;
import com.sun.tools.javac.code.Symtab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeNameFixer {
    
    private static final Map<String,String> PRIMITIVE_TYPES = Map.of(
            "byte", "java.lang.Byte",
            "short", "java.lang.Short",
            "int", "java.lang.Integer",
            "long", "java.lang.Long",
            "float", "java.lang.Float",
            "double", "java.lang.Double",
            "char", "java.lang.Character",
            "boolean", "java.lang.Boolean");
    
    private final List<String> classNames = new ArrayList<>();
    private final Map<String,List<String>> imports = new HashMap<>();

    
    public TypeNameFixer() {}
    
    public void setEnv(Symtab symtab){
        var classes = symtab.getAllClasses();
        classes.forEach(c -> classNames.add(c.toString()));
        imports.put("",List.of());
    }
    
    public void setContextClass(CompilationUnit unit){
        var imports_ = unit.getImports();
        var temp1 = new ArrayList<String>();
        imports_.forEach(imp -> temp1.add(imp.getNameAsString()));
        var temp2 = temp1.stream().map(s -> s.endsWith("\\*") ? s.replace(".*",""):s).toList();
        unit.getTypes().forEach(c -> imports.put(JavaParserUtils.getClassName(c,""),temp2));
    }
    
    public String fixTypeName(String className,String typeName) {
        if(PRIMITIVE_TYPES.containsKey(typeName)) return PRIMITIVE_TYPES.get(typeName);
        
        //大概率就是完整路径
        if (typeName.contains(".")) return typeName;
        
        var possibleClasses = classNames.stream().filter(c -> c.endsWith(typeName)).parallel().toList();
        if(possibleClasses.isEmpty()) return typeName;
        if(possibleClasses.size() == 1) return possibleClasses.getFirst();
        
        possibleClasses = possibleClasses.stream().filter(c -> {
            var l = c.split("\\.");
            return l[l.length-1].equals(typeName);
        }).parallel().toList();
        if(possibleClasses.isEmpty()) return typeName;
        if(possibleClasses.size() == 1) return possibleClasses.getFirst();
        
        possibleClasses = possibleClasses.stream().filter(c -> imports.get(className).stream().anyMatch(c::startsWith)).parallel().toList();
        if(possibleClasses.size() == 1) return possibleClasses.getFirst();
        
        return typeName;
    }
}
