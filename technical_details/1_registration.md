 # 注册

这篇是关于注册的分析总结
 
 观前声明:我所使用的方块实体的缩写为TE 别问为什么

## 引入
 
 首先我们来回顾一下如果要往游戏注册一个物品需要做什么:
 - 写一个物品类
 - 创建一个DeferredRegister<Item> 
 - 创建一个对应物品的RegistryObject<Item>
 - 把这个物品塞进一个CreativeModeTab
 - 使用dataGen或者手写的方法写一个model
 - 使用dataGen或者其他什么方法给它本地化

 那么 如果注册方块呢?
 - 写一个方块类
 - 创建一个DeferredRegister<Block>
 - 创建一个RegistryObject<Block>
 - 创建一个对应方块物品
 - 走一遍物品流程
 - 使用dataGen或者其他什么方法写blockState和model
 - 使用dataGen或者其他什么方法给它本地化

 更精彩的是 如果你需要方块实体
 - 需要额外的DeferredRegister<BlockEntityType<?>>  并且每次跟奇怪的循环依赖的注册打一遍交道
 - 可能需要额外渲染器

 在这个过程中 你还得数次使用你注册目标的String或ResourceLocation形式的ID
 
 是不是很麻烦?
 但是仔细想想 就会发现只有写物品/方块/TE类是实现功能的核心部分 注册,分配ID等都是重复性的工作
 所以可以使用一些手段自动化它!
 
 **警告:以下内容大部分情况我自己用的很舒服 但是可能也是本模组写的最大粪的地方 维护起来连我自己都觉得智熄
 你可以参考设计思路 但是绝对不建议参考实现**
 
##  @AutomaticRegistration
 是的 我们使用注解来实现自动注册
 请参阅 com.xkball.tin_tea_tech.api.annotation 包下的所有类
 这个包下所有类都是用于自动注册的

### 目标功能
 AutomaticRegistration注解类和其中的所有内部注解类都是用于标识一个类需要被自动注册的
 内部注解类主要为注册目标提供额外信息
 可以被这个注解注册的类包括<? extend Block>,<? extend Item>,<? extend CreativeModeTab>,<? extend MetaTileEntity>,<? extend IItemBehaviour>,<? extend IGuiOverlay>,<? extend

 CreativeTag注解用于表示一个 会产生物品 的注册对象注册出的物品放进什么CreativeTag,注意参数是类,因为本模组的CreativeTag也是自动注册的 不是很方便拿到它自己

 I18N注解代表一个 会产生本地化键 的注册对象(通常这与会产生物品的注册对象是同义词) 可以直接用注解在对应类里面甚至中英文

 Model注解是一个设计很差的注解 原因是在不同语境下其含义不同 在修过若干个非常弱智的bug后 真的,真的别写这种东西. 在MTE上 它代表要加载并给TE渲染的模型,在物品(或其他一些什么我不记得的地方)它代表给DataGen用的模型

 Tag注解会自动给方块/物品添加tag

 功能讲完了 让我们来看看
 ### 实现
 自动注册的核心实现基本都在com/xkball/tin_tea_tech/registration/AutoRegManager类
 这是一个灾难性的 有快500行的类 看之前请做好血压升高的准备

 在所需的类上使用@AutomaticRegistration后 我们需要在游戏进入注册阶段前知道哪些类使用的这个注解.
 我们不需要手动分析我们的所有类

    var c = ModLoadingContext.get().getActiveContainer();
    if(c instanceof FMLModContainer mc){
    
        try {
            //别问为什么得反射 可能forge不是很想让你拿到这个
            var scanResults = FMLModContainer.class.getDeclaredField("scanResults");
            scanResults.setAccessible(true);
            var modClass = FMLModContainer.class.getDeclaredField("modClass");
            modClass.setAccessible(true);
            //re是一个类的静态成员变量 后面还会用
            //实际上AutoRegManager里的东西基本都是静态的
            re = (ModFileScanData) scanResults.get(mc);
            var type = Type.getType(AutomaticRegistration.class);
            var list = re.getAnnotations().stream().filter(
                    (ad) -> ad.annotationType().equals(type) && ad.targetType() == ElementType.TYPE)
                            .toList();
        }
catch反射异常一类的就不写了
这样我们就拿到了一个List<ModFileScanData.AnnotationData> 里面保存了我们所需的类分析结果
非常坑的是 从AnnotationData里面只能拿到类名不能拿到实际类 (或许是因为分析类的时候没有进行类加载?)
所以我们还得获取类实例

如下:

       public static Class<?> getRealClass(String className) throws ClassNotFoundException {
        if(className.contains("$")){
            var facName = className.substring(0,className.lastIndexOf("$"));
            var fac = Class.forName(facName);
            for(var clazz : fac.getDeclaredClasses()){
                if(clazz.getSimpleName().equals(className.substring(className.lastIndexOf("$")+1))){
                    return clazz;
                }
            }
        }
        return Class.forName(className);
    }

这里有个莫名其妙的小坑 如果目标类是个类的内部类 Class.forName(className)会报错 但是把那个类去getName又和输入的className一致
不管原理如何 经过一点点绕行(拿到所有内部类一个个去试,这里没有考虑内部类套内部类) 我们终于拿到了使用了@AutomaticRegistration注解的类的Class对象 这为接下来反射等操作提供了空间

然后用XX(将产生的对象类型).class.isAssignableFrom(clazz)就可以判断你拿到的类是不是继承自某个类
从而可以区分方块物品等 然后可以创建对应一个或者多个RegistryObject

这里存在数个问题 暂时的解决方案如下(我都不是觉得很好):
- 创建真正的目标对象只能用反射 这里节选一段
     
        TTRegistration.BLOCKS.register(name,
                    () -> {
                try {
                    return (Block) clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
                );
非常丑陋 还不能给构造器任意传参 这基本意味着一个类只能对应一个对象

- 很难拿到目标对象 我们并没有对其的直接引用(我觉得这是最大的问题)
暂时解决方案是

       //仅在单例时使用
       private static final Map<Class<?>, RegistryObject<?>> classRegMap = new HashMap<>();

       //所有自动注册的玩意都放进去
       private static final Map<String,RegistryObject<?>> nameRegMap = new HashMap<>();

然后拿对象的时候对RegistryObject get后的东西进行一个强制转型(谔谔)

- 没有地方定义了目标对象的ID 现在解决方案是从类名生成 生成规则如下

        private static final Pattern pattern = Pattern.compile("[A-Z][a-z]*");

        public static String fromClassName(Class<?> clazz){
            if(clazz.isAnnotationPresent(AutomaticRegistration.class) && clazz.getAnnotation(AutomaticRegistration.class).needSpecialName()){
                return clazz.getAnnotation(AutomaticRegistration.class).name();
            }
            return fromClassName(clazz.getSimpleName());
        }
        
        public static String fromClassName(String className){
            var result = className;
            if(className.startsWith("TTT")){
                result = className.substring(3);
            }
            else if(className.startsWith("TT")){
                result = className.substring(2);
            }
            else if(className.startsWith("MTE")){
                result = className.substring(3);
            }
            else if(className.endsWith("Behaviour")){
                result = className.substring(0,className.lastIndexOf("B"));
            }
            StringBuilder builder = new StringBuilder();
            for(MatchResult s : pattern.matcher(result).results().toList()){
                builder.append(s.group().toLowerCase());
                builder.append("_");
            }
            builder.deleteCharAt(builder.length()-1);
            return builder.toString();
        }

存在实在不能生成名字的情况(见能量管道) 所以也给了不生成名字的途径 剩下则对类名掐头去尾后把大写全变成小写 然后单词之间插入"_"

剩下的注解和这个的实现差不多 就不详细介绍了

## 总结

使用例
       
       @CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
       @AutomaticRegistration
       @AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
       @Model(resources = {"tin_tea_tech:block/pipe_default"})
       @I18N(chinese = "末影物品管道",english = "Ender Power Item Pipe")
       @Tag.Item({"pipe"})
       public class MTEEnderPowerItemPipe extends MTEPipeWithNet{...}

现在的方案的确实现了打注解就能注册 的确让我能把大部分精力放在核心功能开发而不用把一堆类翻来覆去去注册

但是现在方案自由度较低 思路有些混乱 多次遇到需要不足半路加功能(不自动生成名称的功能就是一例) 导致类里面存在大量复杂还带重复性的代码
面目可憎 急需重写 (可惜也没什么重写的动力,毕竟能跑)


