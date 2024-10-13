package com.xkball.tin_tea_tech.util;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;

public class AnnotationUtils {
    
    public static Attribute.Compound findAnnotation(Symbol classElement, String name){
        return classElement.getMetadata().getDeclarationAttributes().stream().filter(a -> a.type.equals(JCTreeUtils.elementUtils.getTypeElement(name).asType())).findFirst().orElseThrow();
    }
    
    public static Object getAnnotationValue(Attribute.Compound annotation, String name){
        return annotation.values.stream().filter(p -> name.equals(p.fst.name.toString())).findFirst().map(p -> p.snd.getValue()).orElseThrow();
    }
    
    public static Object getEnumAnnotationValue(Attribute.Compound annotation, String name, Class<? extends Enum> clazz){
        return annotation.values.stream().filter(p -> name.equals(p.fst.name.toString())).findFirst().map(p -> {
            if (p.snd instanceof Attribute.Enum) {
                return Enum.valueOf(clazz, ((Symbol.VarSymbol) p.snd.getValue()).name.toString());
            }
            else throw new RuntimeException("result is not an Enum");
            
        } ).orElseThrow();
    }
}
