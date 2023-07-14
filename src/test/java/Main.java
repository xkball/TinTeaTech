import com.xkball.tin_tea_tech.capability.heat.HeatHandler;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
//        System.out.println(TTCreativeTab.TTBuildingBlockTab.class);
//        System.out.println(formClassName(Main.class.getSimpleName()));
//        System.out.println(formClassName(AutomaticRegistration.class.getSimpleName()));
//        System.out.println(formClassName(TTCreativeTab.class.getSimpleName()));
//        System.out.println(formClassName(TinTeaTech.class.getSimpleName()));
        for(int i=0;i<20;i++){
            var heat = new HeatHandler(i+990);
            System.out.println("t:" + heat.heatValue() +" gap: " +heat.heatGap());
        }
        var heat = new HeatHandler();
        System.out.println(heat);
        for(int i=0;i<10;i++){
            heat.increase(10);
            System.out.println(heat);
        }
        heat = new HeatHandler();
        for(int i=0;i<10;i++){
            heat.decrease(10);
            System.out.println(heat);
        }
        
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
