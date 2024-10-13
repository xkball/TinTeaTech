package com.xkball.tin_tea_tech.annotation_processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.List;
import com.xkball.tin_tea_tech.api.annotation.NetworkPacket;
import com.xkball.tin_tea_tech.util.AnnotationUtils;
import com.xkball.tin_tea_tech.util.CodecManager;
import com.xkball.tin_tea_tech.util.jctree.JCTreeUtils;
import com.xkball.tin_tea_tech.util.Types;
import com.xkball.tin_tea_tech.util.jctree.NetworkJCTreeUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Set;

import static com.xkball.tin_tea_tech.util.jctree.JCTreeUtils.isPublicStatic;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({
    NetworkPackProcessor.NETWORK_PACK
})
public class NetworkPackProcessor extends AbstractProcessor {
    
    public static final String NETWORK_PACK = "com.xkball.tin_tea_tech.api.annotation.NetworkPacket";
    public static final String CODEC = "com.xkball.tin_tea_tech.api.annotation.NetworkPacket.Codec";
    public static final String HANDLER = "com.xkball.tin_tea_tech.api.annotation.NetworkPacket.Handler";
    
    public static final String LOG_HEAD = "[TinTeaTechAnnotationProcessor.NetworkPackProcessor] ";
    
    private ProcessingEnvironment processingEnv;
    private Elements elementUtils;
    private Filer filer;
    private JavacTrees trees;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        CodecManager.init(processingEnv);
        this.processingEnv = processingEnv;
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        trees = JavacTrees.instance(processingEnv);
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var a = System.getenv();
        JCTreeUtils.setup(processingEnv);
        var usingClasses = roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(NETWORK_PACK));
        var usingHandlers = roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(HANDLER));
        var usingCodec = roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(CODEC));
        assert usingClasses.stream().allMatch(element -> element.getKind().isClass());
        assert usingHandlers.stream().allMatch(element -> element.getKind().isExecutable());
        assert usingCodec.stream().allMatch(element -> element.getKind().isField());
        for (var e : usingClasses) {
            var classElement = (Symbol.ClassSymbol)e;
            var fullClassName = classElement.fullname.toString();
            
            System.out.println(LOG_HEAD + "Processing " + fullClassName);
            

            var codec = (Symbol.VarSymbol)usingCodec.stream().findFirst().orElseThrow();
            var handler = (Symbol.MethodSymbol)usingHandlers.stream().findFirst().orElseThrow();
            assert isPublicStatic(codec.flags());
            
            var netpackAnnotation = AnnotationUtils.findAnnotation(classElement,NETWORK_PACK);
            var modid = (String) AnnotationUtils.getAnnotationValue(netpackAnnotation,"modid");
            var type = (NetworkPacket.Type) AnnotationUtils.getEnumAnnotationValue(netpackAnnotation,"type",NetworkPacket.Type.class);
            
            var classTree = trees.getTree(classElement);
            JCTreeUtils.setPos(classTree);
            JCTreeUtils.addModBusSubscriber(classTree,modid);
            NetworkJCTreeUtils.addNetworkPacketType(classTree);
            NetworkJCTreeUtils.addNetworkPacketTypeGetter(classTree);
            JCTreeUtils.addImplement2Class(classTree,Types.CUSTOM_PACKET_PAYLOAD);
            NetworkJCTreeUtils.addNetworkRegListener(classTree,modid,type,codec.owner.flatName()+"."+codec.flatName(),handler.name.toString(),handler.owner.flatName().toString());
            JCTreeUtils.addAnnotation2Method(JCTreeUtils.findMethod(classTree,"register"),Types.SUBSCRIBE_EVENT, List.nil());
            
        }
        return false;
    }
    


}
