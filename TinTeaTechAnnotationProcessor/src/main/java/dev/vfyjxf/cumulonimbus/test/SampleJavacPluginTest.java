package dev.vfyjxf.cumulonimbus.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

public class SampleJavacPluginTest {

    private static final String CLASS_TEMPLATE
            = """
            package com.xkball.tin_tea_tech.network.c2s;
            
            import com.xkball.tin_tea_tech.TinTeaTech;
            import com.xkball.tin_tea_tech.api.annotation.NetworkPacket;
            import net.minecraft.core.component.DataComponentPatch;
            import net.minecraft.core.component.DataComponentType;
            import net.minecraft.network.RegistryFriendlyByteBuf;
            import net.minecraft.network.codec.ByteBufCodecs;
            import net.minecraft.network.codec.StreamCodec;
            import net.neoforged.neoforge.network.handling.IPayloadContext;
            
            import java.util.Map;
            import java.util.Optional;
            
            @NetworkPacket(modid = TinTeaTech.MODID,type = NetworkPacket.Type.PLAY_CLIENT_TO_SERVER)
            public record UpdateItemStackData(int slot, DataComponentPatch componentPatch) {
               \s
                @NetworkPacket.Codec
                public static final StreamCodec<RegistryFriendlyByteBuf, UpdateItemStackData> STREAM_CODEC = StreamCodec.composite(
                        ByteBufCodecs.VAR_INT,
                        UpdateItemStackData::slot,
                        DataComponentPatch.STREAM_CODEC,
                        UpdateItemStackData::componentPatch,
                        UpdateItemStackData::new
                );
               \s
                @NetworkPacket.Handler
                @SuppressWarnings({"unchecked", "rawtypes"})
                public void handle(IPayloadContext context){
                    context.enqueueWork(() -> {
                        var player = context.player();
                        var itemOnPlayer = player.getSlot(slot).get();
                        for(Map.Entry<DataComponentType<?>, Optional<?>> data: componentPatch.entrySet()){
                            if(data.getValue().isEmpty()) continue;
                            Object value = data.getValue().get();
                            itemOnPlayer.set((DataComponentType) data.getKey(),value);
                        }
                    });
                }
            }
            
                        """;

    private static TestCompiler compiler = new TestCompiler();
    private static TestRunner runner = new TestRunner();


    public static void main(String[] args) throws Throwable {
        compileAndRun();
    }

    private static Object compileAndRun()
            throws Throwable {
        String qualifiedClassName = "com.xkball.test.Test";
        byte[] byteCode = compiler.compile(
                qualifiedClassName,
                CLASS_TEMPLATE
        );
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        Printer printer = new ASMifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(byteCode).accept(traceClassVisitor, parsingOptions);
        return runner.run(byteCode, qualifiedClassName,
                          "test", null, (Object) null
        );
    }
}
