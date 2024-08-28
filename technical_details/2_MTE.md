# MetaTileEntity

原谅我使用这个奇怪的名称 实际上这个玩意抄自GTCEu 但是我在这上面的设计理念和CEu有所区别

## 这是啥

让我们想想实现一个方块实体的功能可能涉及哪些类
- 方块类
- 物品类
- 方块实体类
- 渲染类

毫无疑问 在这些类直接辗转腾挪并不是一个太好的体验(至少对我而言)

所以我所设计的MTE就是一个非常暴力的东西:把这些类里面的方法全部桥接到一个MTE类
有点像BlockState(Base)类调用了Block类的方法

其优势是基本所有逻辑都能写在一个类里 方便查阅修改

其劣势也非常明显 就是一个类里面东西非常多

这也带来一个非常厄厄的事情:使用了非常多@OnlyIn(Dist.CLIENT) 这其中的优劣取舍还请读者自己定夺

# Tick

大量TE都需要每tick运算 所以MTE默认是tickable的,不需要可以覆盖needTicker()方法 虽然暂时还没遇到不需要的情况

MTE提供了tick()和clientTick()分别控制服务端和客户端的tick 这样一些渲染数据根本不需要在服务端计算(据我观察 很多模组都会在服务端计算一些只需要在客户端渲染使用的数据)

还提供了firstTick()和firstClientTick()用来控制初始化行为

又:大量的TE并不需要每tick运算,实际上间隔几个tick运行完全够用 所以我提供了getOffsetTick()来获取当前(整个世界载入后)运行的tick数
然后把拿到的值跟另一个数取余就能每隔数个tick运行一次逻辑

offset的含义是每个MTE在被加载时都会分配一个[0,20)的随机数 防止大量TE在同一个tick内运算

实际上get的值是offset加上全局的服务器tick数 这似乎意味着一个事实:加速火把会使得offset的效果部分无效

# 数据同步

这算是MTE使用的特色内容 其实普通TE也可使用并且被我自己其他项目大量使用了 (其实大部分也抄自GTCEu)

MTE支持服务端到客户端和客户端到服务端双向的数据传递
分别为void sentCustomData(int id, Consumer<ByteBuf> bufConsumer) 和 void sentToServer(Consumer<CompoundTag> tag)

这意味着发送到客户端的数据不是简单地把整个te的数据发过去 而是由用户自己控制 于是一些跟渲染无关的数据可以根本不同步到客户端

能否有性能提升不明 但是CEu这样做了我单纯照抄(  

一个缺点是ByteBuf类在高版本并不如在1122好用,应该使用FriendlyByteBuf,如果有人看到这里记得发个issue让我改掉 我自己是绝对很懒的搞这个的