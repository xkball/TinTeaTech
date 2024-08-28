import com.xkball.tin_tea_tech.utils.TTUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joml.sampling.BestCandidateSampling;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
//        System.out.println(TTCreativeTab.TTBuildingBlockTab.class);
//        System.out.println(formClassName(Main.class.getSimpleName()));
//        System.out.println(formClassName(AutomaticRegistration.class.getSimpleName()));
//        System.out.println(formClassName(TTCreativeTab.class.getSimpleName()));
//        System.out.println(formClassName(TinTeaTech.class.getSimpleName()));
//        for(int i=0;i<20;i++){
//            var heat = new HeatHandler(i+990);
//            System.out.println("t:" + heat.heatValue() +" gap: " +heat.heatGap());
//        }
//        var heat = new HeatHandler();
//        System.out.println(heat);
//        for(int i=0;i<10;i++){
//            heat.increase(10);
//            System.out.println(heat);
//        }
//        heat = new HeatHandler();
//        for(int i=0;i<10;i++){
//            heat.decrease(10);
//            System.out.println(heat);
//        }
        //visitPath(new File("E:\\U盘备份\\python_work\\work"),0);
        //visitPath(new File("E:\\U盘备份\\python_work\\work\\bicycles"),0);
//        int i = 100;
//        System.out.println(i);
//        test(i);
//        System.out.println(i);
//        BitSet bitSet = new BitSet();
//        bitSet.set(10);
//        bitSet.set(60);
//        bitSet.set(1);
//        bitSet.set(0);
//        long l = TTUtils.longValueOfBitSet(bitSet);
//        BitSet r = TTUtils.fromLongToBitSet(l,61);
//        System.out.println(Long.toString(l,2));
//        System.out.println(r);
        
        try(var client = HttpClients.createDefault();) {
            HttpGet get = new HttpGet("https://www.mcmod.cn/class/8.html");
            var response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
               // System.out.println(EntityUtils.toString(response.getEntity()));
                var entity = response.getEntity();
                var head = entity.getContentEncoding();
                for(var element : head.getElements()){
                    System.out.println(element);
                }
            }
            response.close();
        }
        
    
        
        

    }
    
    public static void visitPath(File directory,int stack){
        if(!directory.exists()){
            System.out.println(directory.getPath()+" not exist!");
            return;
        }
        for(int i=0;i<stack;i++){
            System.out.print("    ");
        }
        System.out.println(directory.getPath());
        if(directory.isDirectory()){
            for(var dr : Objects.requireNonNull(directory.listFiles())){
                visitPath(dr,stack+1);
            }
        }
        
        
    }
    
    public static void test(int i){
        System.out.println(i);
        i = 10;
        System.out.println(i);
    }
    
    private static String formClassName(String className){
        System.out.println("1."+className);
        var result = className;
        if(className.startsWith("TTT")){
            result = className.substring(3);
        }
        else if(className.startsWith("TT")){
            result = className.substring(2);
        }
        System.out.println("2."+result);
        StringBuilder builder = new StringBuilder();
        var pattern = Pattern.compile("[A-Z][a-z]*");
        for(MatchResult s : pattern.matcher(result).results().toList()){
            builder.append(s.group().toLowerCase());
            builder.append("_");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
