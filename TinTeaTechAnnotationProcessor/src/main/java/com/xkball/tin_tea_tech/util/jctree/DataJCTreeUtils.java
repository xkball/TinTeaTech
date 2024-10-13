package com.xkball.tin_tea_tech.util.jctree;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.xkball.tin_tea_tech.annotation_processor.DataFieldProcessor;
import com.xkball.tin_tea_tech.api.annotation.DataField;
import com.xkball.tin_tea_tech.util.CodecManager;
import com.xkball.tin_tea_tech.util.StringUtils;
import com.xkball.tin_tea_tech.util.Types;

import java.lang.reflect.Modifier;
import java.util.Objects;

import static com.xkball.tin_tea_tech.util.jctree.JCTreeUtils.*;

public class DataJCTreeUtils {
    
    public static JCTree.JCClassDecl createDataClass(String outClassName, String className,List<DataFieldData> dataFields) {
        var mods = mods(Modifier.PUBLIC | Modifier.STATIC);
        var def = List.<JCTree>nil();
        var result = treeMaker.ClassDef(mods,name(className), List.nil(),null,List.nil(),def);
        def = def.append(createInitMethod(dataFields));
        if(dataFields.isEmpty()){
            result.defs = def.append(createEmptyCodec(outClassName, className));
            return result;
        }
        for(var data: dataFields) {
            def = def.append(createField(data));
        }
        def = def.append(createCodec(outClassName,className,dataFields));
        result.defs = def;
        return result;
    }
    
    public static JCTree.JCVariableDecl createField(DataFieldData data) {
        var mods = mods(Modifier.PUBLIC | Modifier.FINAL);
        return treeMaker.VarDef(mods,name(data.fieldName),makeIdent(data.fieldType),null);
    }
    
    public static JCTree.JCMethodDecl createInitMethod(List<DataFieldData> dataFields) {
        var mods = mods(Modifier.PUBLIC);
        var params = List.<JCTree.JCVariableDecl>nil();
        for(var data: dataFields) {
            params = params.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER),name(data.fieldName),makeIdent(data.fieldType),null));
        }
        var bodyBlock = List.<JCTree.JCStatement>nil();
        bodyBlock = bodyBlock.append(treeMaker.Exec(treeMaker.Apply(List.nil(),makeIdent("super"),List.nil())));
        for(var data: dataFields) {
            bodyBlock = bodyBlock.append(treeMaker.Exec(treeMaker.Assign(makeIdent("this."+data.fieldName),makeIdent(data.fieldName))));
        }
        var body = treeMaker.Block(0, bodyBlock);
        return treeMaker.MethodDef(mods,name("<init>"),null,List.nil(),params,List.nil(),body,null);
    }
    
    public static JCTree.JCVariableDecl createEmptyCodec(String outClassName, String className){
        var mods = mods(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        var varType = treeMaker.TypeApply(makeIdent("com.mojang.serialization.Codec"),List.of(makeIdent(outClassName + "." + className)));
        var encoder = makeApply(select("com.mojang.serialization.Encoder","empty"),List.nil());
        increaseTreeMakerPos();
        var decoder = makeApply(select("com.mojang.serialization.Decoder","unit"),
                List.of(treeMaker.Reference(MemberReferenceTree.ReferenceMode.NEW,name("<init>"),makeIdent(outClassName + "." + className),null)));
        
        var init = makeApply(treeMaker.Select(
                makeApply(select("com.mojang.serialization.MapCodec","of"),
                        List.of(encoder,decoder)), name("codec")),List.nil());
        return treeMaker.VarDef(mods,name("CODEC"),varType,init);
    }
    
    public static JCTree.JCVariableDecl createCodec(String outClassName, String className,List<DataFieldData> dataFields) {
        var mods = mods(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        var varType = treeMaker.TypeApply(makeIdent("com.mojang.serialization.Codec"),List.of(makeIdent(outClassName + "." + className)));
        var builderArgs = List.<JCTree.JCExpression>nil();
        for(var data: dataFields) {
            builderArgs = builderArgs.append(
                    makeApply(treeMaker.Select(
                            makeApply(treeMaker.Select(
                                    data.fieldCodecRef.getCodecRef(), name("fieldOf")),
                                    List.of(treeMaker.Literal(data.fieldName))),
                            name("forGetter")),List.of(
                                    makeLambda(List.of("obj"),
                                            treeMaker.Select(makeIdent("obj"),name(data.fieldName)))
                            )
                    )
            );
            increaseTreeMakerPos();
        }
        var builder = makeLambda(List.of("builder"),
                makeApply(treeMaker.Select(
                                makeApply(treeMaker.Select(makeIdent("builder"),name("group")),builderArgs),
                        name("apply")),
                    List.of(makeIdent("builder"),
                            treeMaker.Reference(MemberReferenceTree.ReferenceMode.NEW,name("<init>"),makeIdent(outClassName + "." + className),null))
                ));
        var init = makeApply(treeMaker.Select(makeIdent(Types.RECORD_CODEC_BUILDER),name("create")),List.of(builder));
        return treeMaker.VarDef(mods,name("CODEC"),varType,init);
    }
    
    public static List<JCTree.JCVariableDecl> tagRegMethodParams(){
        var tagParam = treeMaker.VarDef(mods(Flags.PARAMETER),name("tag"),makeIdent(Types.COMPOUND_TAG),null);
        increaseTreeMakerPos();
        var registriesParam = treeMaker.VarDef(mods(Flags.PARAMETER),name("reg"),makeIdent(Types.HOLDER_LOOKUP_PROVIDER),null);
        return List.of(tagParam,registriesParam);
    }
    
    @SuppressWarnings("DuplicatedCode")
    public static JCTree.JCMethodDecl createReadMethod(String outclassName, String innerClassName, String methodName, String codecGetterRef, List<DataFieldData> dataFields) {
        var mods = mods(Modifier.PUBLIC);
        var params = tagRegMethodParams();
        var body = treeMaker.Block(0, List.nil());
        var tagName = StringUtils.toSnakeCase(innerClassName);
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("ops"),null,makeApply(select("net.minecraft.resources.RegistryOps","create"),List.of(makeIdent("net.minecraft.nbt.NbtOps.INSTANCE"),ident("reg")))));
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("objTag"),makeIdent("net.minecraft.nbt.Tag"),makeApply(select("tag","get"),List.of(treeMaker.Literal(tagName)))));
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("obj"),makeIdent(outclassName+"."+innerClassName),makeApply(treeMaker.Select(makeApply(select(codecGetterRef,"parse"),List.of(ident("ops"),ident("objTag"))),name("getOrThrow")),List.nil())));
        for(var data: dataFields) {
            body.stats = body.stats.append(treeMaker.Exec(treeMaker.Assign(select("this",data.fieldName),select("obj",data.fieldName))));
        }
        return treeMaker.MethodDef(mods,name(methodName),null,List.nil(),params,List.nil(),body,null);
    }
    
    @SuppressWarnings("DuplicatedCode")
    public static JCTree.JCMethodDecl createWriteMethod(String outclassName, String innerClassName, String methodName, String codecGetterRef, List<DataFieldData> dataFields) {
        var mods = mods(Modifier.PUBLIC);
        var params = tagRegMethodParams();
        var body = treeMaker.Block(0, List.nil());
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("ops"),null,makeApply(select("net.minecraft.resources.RegistryOps","create"),List.of(makeIdent("net.minecraft.nbt.NbtOps.INSTANCE"),ident("reg")))));
        var newObjParams = List.<JCTree.JCExpression>nil();
        for(var data: dataFields) {
            newObjParams = newObjParams.append(select("this",data.fieldName));
            increaseTreeMakerPos();
        }
        var dataClassName = outclassName+"."+innerClassName;
        var tagName = StringUtils.toSnakeCase(innerClassName);
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("obj"),makeIdent(dataClassName),treeMaker.NewClass(null,List.nil(),makeIdent(dataClassName),newObjParams,null)));
        body.stats = body.stats.append(treeMaker.VarDef(noMods(),name("objTag"),makeIdent("net.minecraft.nbt.Tag"),makeApply(treeMaker.Select(makeApply(select(codecGetterRef,"encodeStart"),List.of(ident("ops"),ident("obj"))),name("getOrThrow")),List.nil())));
        body.stats = body.stats.append(treeMaker.Exec(makeApply(select("tag","put"),List.of(treeMaker.Literal(tagName),ident("objTag")))));
        return treeMaker.MethodDef(mods,name(methodName),null,List.nil(),params,List.nil(),body,null);
    }
    
    public static List<JCTree> createBlockEntityOverrides(){
        var bodySaveAdditional = treeMaker.Block(0, List.of(
                treeMaker.Exec(makeApply(select("super","saveAdditional"),List.of(ident("tag"),ident("reg")))),
                treeMaker.Exec(makeApply(select("this","save"),List.of(ident("tag"),ident("reg"))))));
        var saveAdditional = treeMaker.MethodDef(mods(Flags.PROTECTED),name("saveAdditional"),null,List.nil(),tagRegMethodParams(),List.nil(),bodySaveAdditional,null);
        var bodyLoadAdditional = treeMaker.Block(0, List.of(
                treeMaker.Exec(makeApply(select("super","loadAdditional"),List.of(ident("tag"),ident("reg")))),
                treeMaker.Exec(makeApply(select("this","load"),List.of(ident("tag"),ident("reg"))))));
        var loadAdditional = treeMaker.MethodDef(mods(Flags.PROTECTED),name("loadAdditional"),null,List.nil(),tagRegMethodParams(),List.nil(),bodyLoadAdditional,null);
        var bodyGetUpdateTag = treeMaker.Block(0, List.of(
                treeMaker.VarDef(noMods(),name("result"),makeIdent(Types.COMPOUND_TAG),makeApply(select("super","getUpdateTag"),List.of(ident("reg")))),
                treeMaker.Exec(makeApply(select("this","writeS2C"),List.of(ident("result"),ident("reg")))),
                treeMaker.Return(ident("result"))));
        var getUpdateTag = treeMaker.MethodDef(mods(Flags.PUBLIC),name("getUpdateTag"),makeIdent(Types.COMPOUND_TAG),List.nil(),List.of(treeMaker.VarDef(mods(Flags.PARAMETER),name("reg"),makeIdent(Types.HOLDER_LOOKUP_PROVIDER),null)),List.nil(),bodyGetUpdateTag,null);
        var bodyHandleUpdateTag = treeMaker.Block(0, List.of(
                treeMaker.Exec(makeApply(select("super","handleUpdateTag"),List.of(ident("tag"),ident("reg")))),
                treeMaker.Exec(makeApply(select("this","readS2C"),List.of(ident("tag"),ident("reg"))))));
        var handleUpdateTag = treeMaker.MethodDef(mods(Flags.PUBLIC),name("handleUpdateTag"),null,List.nil(),tagRegMethodParams(),List.nil(),bodyHandleUpdateTag,null);
        var bodyGetUpdatePacket = treeMaker.Block(0, List.of(
                treeMaker.Return(makeApply(select("net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket","create"),List.of(ident("this"))))));
        var typeGetUpdatePacket = treeMaker.TypeApply(makeIdent("net.minecraft.network.protocol.Packet"),List.of(makeIdent("net.minecraft.network.protocol.game.ClientGamePacketListener")));
        var getUpdatePacket = treeMaker.MethodDef(mods(Flags.PUBLIC),name("getUpdatePacket"),typeGetUpdatePacket,List.nil(),List.nil(),List.nil(),bodyGetUpdatePacket,null);
        var bodyOnDataPacket = treeMaker.Block(0, List.of(
                treeMaker.Exec(makeApply(select("super","onDataPacket"),List.of(ident("net"),ident("pkt"),ident("reg")))),
                treeMaker.Exec(makeApply(select("this","handleUpdateTag"),List.of(makeApply(select("pkt","getTag"),List.nil()),ident("reg"))))));
        var pConnection = treeMaker.VarDef(mods(Flags.PARAMETER),name("net"),makeIdent("net.minecraft.network.Connection"),null);
        increaseTreeMakerPos();
        var pPacket = treeMaker.VarDef(mods(Flags.PARAMETER),name("pkt"),makeIdent("net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket"),null);
        increaseTreeMakerPos();
        var pReg = treeMaker.VarDef(mods(Flags.PARAMETER),name("reg"),makeIdent(Types.HOLDER_LOOKUP_PROVIDER),null);
        var paramsGetUpdateTag = List.of(pConnection,pPacket,pReg);
        var onDataPacket = treeMaker.MethodDef(mods(Flags.PUBLIC),name("onDataPacket"),null,List.nil(),paramsGetUpdateTag,List.nil(),bodyOnDataPacket,null);
        return List.of(saveAdditional,loadAdditional,getUpdateTag,handleUpdateTag,getUpdatePacket,onDataPacket);
    }
    
    @FunctionalInterface
    public interface ICodecRefGetter{
        JCTree.JCExpression getCodecRef();
    }
    
    public record DataFieldData(String fieldName, String fieldType, ICodecRefGetter fieldCodecRef, DataField.SyncPolicy syncPolicy) {
        
        public static DataFieldData fromAnno(String className,JCTree.JCVariableDecl variable){
            
            var anno = Objects.requireNonNull(JCTreeUtils.findAnnotation(variable, DataFieldProcessor.DATA_FIELD));
            var fieldName = variable.name.toString();
            var fieldType = StringUtils.fixTypeNameWithArgRecursive(className,variable.vartype.toString());
            var codecName = "";
            var syncPolicy = DataField.SyncPolicy.OnlySave;
            for(var arg : anno.args){
                if(arg instanceof JCTree.JCAssign assign){
                    if(assign.lhs.toString().equals("codec")) codecName = StringUtils.removeDoubleQuotes(assign.rhs.toString());
                    if(assign.lhs.toString().equals("syncPolicy")) syncPolicy = DataField.SyncPolicy.fromString(assign.rhs.toString());
                }
            }
            var fieldCodecRef = Objects.requireNonNull(CodecManager.findCodecRef(codecName,fieldType),"Missing Codec for "+fieldType);
            return new DataFieldData(fieldName,fieldType,fieldCodecRef,syncPolicy);
        }
    }
}
