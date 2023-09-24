# 管道
终于讲到了这个模组目前为止最特色的内容
接下来内容可能有点长 由于时间不足 大量贴出了代码 读者如有不理解或者有对代码水平的不满意请一定联系我

## 如何记录斜向的管道连接
首先上一个事实:不能用方块状态
算上斜向 一个管道足足有18个可连接方向 再算上"阻挡"功能 方块状态会有灾难性的2^36种

但是一个long完全可以容纳这么多数据 

但是我不是计科学生 大脑处理对一个数字的与啊或啊总是很慢
于是再绕一下:用bitset(bitset具体是什么还请查阅java.util.BitSet,或者你可以当它一堆按顺序排的boolean)

用了bitset,最终保存的还是long 所以转换如下

我真的对数字的逻辑操作反应很慢很慢 下面这段的确是我自己写的,但每句的含义我自己都已经看不懂了

    public static long longValueOfBitSet(BitSet set){
        long result = 0;
        for(int i = 0; i<set.length();i++){
            result = result | (long) (set.get(i) ? 1 : 0) << i;
        }
        return result;
    }
    
    public static BitSet fromLongToBitSet(long i, int length){
        return fromLongToBitSet(i,length,new BitSet(length));
    }
    
    public static BitSet fromLongToBitSet(long i, int length, BitSet result){
        result.clear();
        for(int j = 0;j<length;j++){
            if(((i & (0b1L << j)) / (0b1L << j)) == 1){
                result.set(j);
            }
        }
        return result;
    }

最终在处理管道是否连接的时候 只需要 connections.get(int); 就像了

## GT九宫格

我知道我实在是三句不离GT 但是"GT九宫格"的名字已经被印在我脑子里了

这实际上也就是对GT用了不知道多少版本的九宫格的一个再实现 虽然在写完后我终于玩到GT6的时候觉得不如GT6写的好

### 画出这个九宫格

我也不知道我究竟是如何在这里抽象出一堆类的 但是的确这么做了 具体请查看com.xkball.tin_tea_tech.client.shape包下

具体用在绘制的图形如下,来自RenderUtil类

    static {
        H01 = 1d/16d;
        H1 = (1d/16d)*3;
        H2 = 1-H1;
        biCrossMusk = new Shape2D()
            .addLine(new Line2D(new Point2D(0.0,H1),new Point2D(1.0,H1)))
            .addLine(new Line2D(new Point2D(0.0,H2),new Point2D(1.0,H2)))
            .addLine(new Line2D(new Point2D(H1,0.0),new Point2D(H1,1.0)))
            .addLine(new Line2D(new Point2D(H2,0.0),new Point2D(H2,1.0))).end();
        crossMusk = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,H1),new Point2D(H2,H2)))
                .addLine(new Line2D(new Point2D(H1,H2),new Point2D(H2,H1))).end();
        leftCross = new Shape2D()
                .addLine(new Line2D(new Point2D(0,H1),new Point2D(H1,H2)))
                .addLine(new Line2D(new Point2D(0,H2),new Point2D(H1,H1))).end();
        rightCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H2,H1),new Point2D(1,H2)))
                .addLine(new Line2D(new Point2D(H2,H2),new Point2D(1,H1))).end();
        upCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,H2),new Point2D(H2,1)))
                .addLine(new Line2D(new Point2D(H1,1),new Point2D(H2,H2))).end();
        downCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,0),new Point2D(H2,H1)))
                .addLine(new Line2D(new Point2D(H1,H1),new Point2D(H2,0))).end();
        
    }

shape2D是一对2d线的合集,实际上对图形的封闭性没有任何要求,就是一堆2d线

line2d含两个二维向量,line3d含两个三维向量,我相信这很好理解

biCrossMusk就是九宫格

剩下则是绘制在九宫格里面,表示"有什么东西"的叉叉

我们介入RenderHighlightEvent.Block事件就能在目标方块上额外画出九宫格

绘制过程如下

    public static void drawShape(VertexConsumer pConsumer, PoseStack poseStack,Shape2D shape, Direction direction,
                                 double pX, double pY, double pZ,
                                 float r, float g, float b, float a){
        for(var line : shape.toDraw(direction)){
            drawLine(pConsumer,poseStack,line,pX,pY,pZ,r,g,b,a);
        }
    }
    
    public static void drawLine(VertexConsumer pConsumer, PoseStack poseStack,
                                Line3D line,
                                double pX, double pY, double pZ,
                                float r, float g, float b, float a){
        drawLine(pConsumer,poseStack,line.start().x(),line.start().y(),line.start().z(),
                line.end().x(),line.end().y(),line.end().z(),pX,pY,pZ,r,g,b,a);
    }
    
    public static void drawLine(VertexConsumer pConsumer, PoseStack poseStack,
                                double sx, double sy, double sz,
                                double ex, double ey, double ez,
                                double pX, double pY, double pZ,
                                float r, float g, float b, float a){
        PoseStack.Pose posestack$pose = poseStack.last();
        float f = (float)(ex - sx);
        float f1 = (float)(ey - sy);
        float f2 = (float)(ez - sz);
        float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
        f /= f3;
        f1 /= f3;
        f2 /= f3;
        pConsumer.vertex(posestack$pose.pose(), (float)(sx + pX), (float)(sy + pY), (float)(sz + pZ))
                .color(r, g, b, a)
                .normal(posestack$pose.normal(), f, f1, f2).endVertex();
        pConsumer.vertex(posestack$pose.pose(), (float)(ex + pX), (float)(ey + pY), (float)(ez + pZ))
                .color(r, g, b, a)
                .normal(posestack$pose.normal(), f, f1, f2).endVertex();
        
    }

可以看到 这里我们把shape里一堆2d线根据绘制方向转换成3d线然后正常用VertexConsumer提交了一堆点(并连成线)

    public Collection<Line3D> toDraw(Direction direction){
        if(!drawLines.containsKey(direction)){
            for(var line : lines){
                drawLines.put(direction,line.to3D(direction));
            }
        }
        return drawLines.get(direction);
    }

在这个转化中 实际上是延迟到第一次使用再计算并缓存了所有需要的line3d 而line2d的to3d实际上也只是把每个点单独to3d
x,y为二维点的两个坐标

    //在1*1*1立方体内的变换
    //默认为朝向东面计算(视野前面是西面)
    //每个面的坐标系原点为左下角
    @SuppressWarnings("SuspiciousNameCombination")
    public Point3D to3D(Direction direction){
        switch (direction){
            case DOWN -> {
                return new Point3D(1-y,0,x);
            }
            case UP -> {
                return new Point3D(y,1,x);
            }
            case NORTH -> {
                return new Point3D(1-x,y,0);
            }
            case SOUTH -> {
                return new Point3D(x,y,1);
            }
            case WEST -> {
                return new Point3D(0,y,x);
            }
            case EAST -> {
                return new Point3D(1,y,1-x);
            }
        }
        return new Point3D(0,0,0);
    }

很难解释什么了 当初在草稿纸上跟立方体对线了很久

### 使用这个九宫格
大概就是得知道你用什么东西点这个九宫格的时候究竟点到哪里了

于是先抽象出了一个RelativeFacing,表示九宫格的九个位置
这九宫格九个位置在不同情况对应两个东西:机器面和方块的相对位置
一个使用了原版Direction,一个使用了我又抽象出来的Connections(大概就是有18个方向的Direction)

物品对方块使用会产生一个UseOnContext
context.getClickLocation()可以获得一个表示具体点击点的vec3;

与上面类似 把3d点变回2d点过程如下

    @SuppressWarnings("SuspiciousNameCombination")
    public Point2D to2D(Direction direction){
        switch (direction){
            case DOWN -> {
                return new Point2D(z,1-x);
            }
            case UP -> {
                return new Point2D(z,x);
            }
            case NORTH -> {
                return new Point2D(1-x,y);
            }
            case SOUTH -> {
                return new Point2D(x,y);
            }
            case WEST -> {
                return new Point2D(z,y);
            }
            case EAST -> {
                return new Point2D(1-z,y);
            }
        }
        return new Point2D(0,0);
    }

这再判断点击点在九宫格的哪里就十分容易了

不对 还没完 这里有个非常大的问题是默认了点击的是一个完整方块的表面
我的选择是拿着对应物品时候转换方块碰撞箱

## 碰撞箱
这里由于方块相关内容过多 又单独开了个方块类 可见MTE的设计还是有些混乱的

mc的碰撞箱是AABB 换言之边框都是沿轴向的 我暂时放弃了斜向部分的碰撞箱

这里实现了上文提到的在需要时返回完整碰撞箱

      public VoxelShape _getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext,VoxelShape defaultShape){
        if(pContext instanceof EntityCollisionContext ec){
            var entity = ec.getEntity();
            if(entity instanceof Player player){
                if(ItemUtils.hasTagInHand(player,TTItemTags.get("tool"),TTItemTags.get("pipe"),TTItemTags.get("cover")))
                    return Shapes.block();
                var s = shape;
                var mte = LevelUtils.getMTE(pLevel,pPos);
                if(mte instanceof MTEPipe p){
                    s = collisions.get(p.getCollision());
                }
                return s;
            }
        }
        return defaultShape;
    }

这里把传进来的CollisionContext进行了一个强制转型 实际上原版这里应该只可能传进EntityCollisionContext 
考虑到只考虑六方向连接也有64种状态(和64种碰撞箱) 我对所有碰撞箱进行了一个提前生成和缓存

    //不是很符合规范的命名
    private static final VoxelShape shape = Block.box(6*dx1,6*dx1,6*dx1,10*dx2,10*dx2,10*dx2);
    private static final VoxelShape UP_shape = Block.box(6*dx1,10*dx1,6*dx1,10*dx2,16,10*dx2);
    private static final VoxelShape DOWN_shape= Block.box(6*dx1,0*dx1,6*dx1,10*dx2,6*dx2,10*dx2);
    private static final VoxelShape WEST_shape = Block.box(0,6*dx1,6*dx1,6*dx2,10*dx2,10*dx2);
    private static final VoxelShape EAST_shape = Block.box(10*dx1,6*dx1,6*dx1,16,10*dx2,10*dx2);
    private static final VoxelShape NORTH_shape = Block.box(6*dx1,6*dx1,0,10*dx2,10*dx2,6*dx2);
    private static final VoxelShape SOUTH_shape = Block.box(6*dx1,6*dx1,10*dx1,10*dx2,10*dx2,16);
    
    private static final VoxelShape[] shapes = new VoxelShape[]{UP_shape,DOWN_shape,NORTH_shape,EAST_shape,SOUTH_shape,WEST_shape};
    
    public static final Int2ObjectMap<VoxelShape> collisions;
    
    static {
        collisions = new Int2ObjectLinkedOpenHashMap<>();
        for(int i=0;i<64;i++){
            var bit = TTUtils.forIntToBitSet(i,6);
            ArrayList<VoxelShape> used = new ArrayList<>();
            for(int j=0;j<6;j++){
                if(bit.get(j)) used.add(shapes[j]);
            }
            collisions.put(i,Shapes.or(shape,used.toArray(new VoxelShape[0])));
        }
    }

最后 只需要按管道轴向连接情况产生的一个数字直接在map里面get就能拿到碰撞箱(或许用数组更好?)

值得一提的是"按管道轴向连接情况产生的一个数字"是利用MTE的特殊数据同步方式 完全只在客户端计算的

    
## 渲染

管道的渲染充斥着意义不明的垃圾代码 管道本身渲染和其高亮框的渲染不仅设计不同 甚至还都有bug

所以这里不仅把这部分内容列为todo,其渲染本身也得重做

## 管道网络

末影管道的核心部分

### 需求

相连的管道应该都有一个相同的对PipeNet的引用,而PipeNet也能找回去

在管道切换连接状态时候PipeNet也能快速反应

有中心方块,用于保存数据

### 实现

总结:非常暴力

先看看PipeNet是什么吧

    public interface PipeNet {
        
        void tick(BlockPos pos);
        
        int size();
        
        Collection<MTEPipeWithNet> getConnected();
        
        Map<BlockPos,MTEPipeWithNet> getConnectedRaw();
        
        MTEPipeWithNet getCenter();
        
        //实在不知道用不用的到
        @SuppressWarnings("UnusedReturnValue")
        PipeNet combine(BlockPos other);
        
        void cut(BlockPos pos);
        
        default void checkNet(boolean force){}
        
        default CompoundTag save(BlockPos pos){
            return new CompoundTag();
        }
        
        default void load(CompoundTag tag){
        
        }
        
        default <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
            return LazyOptional.empty();
        }
        
        default boolean canCombine(PipeNet other){
            return !other.workable() || other.getClass() == this.getClass();
        }
        
        default boolean workable(){
            return true;
        }

    }

#### 初始化
PipeNet有个一个实现用于初始化 这样可以模糊连接一个Pipe和加载一个Pipe的区别

    @Override
    public void tick(BlockPos pos) {
        //在此进行初始化
        //第一tick
        if(!firstTicked){
            //情况1 存在网络且不是自己
            if(!center.getPos().equals(centerPos)) {
                firstTicked = true;
                return;
            }
            //情况2 网络中心是自己
            //两种子情况
            //当加载时和当放置时
            var handled = false;
            //边上存在现成的网络
            for(var mte : LevelUtils.getConnected(center.getLevel(),center.getPos(),false)){
                if (mte.getBelongs().workable()){
                    mte.getBelongs().combine(center.getPos());
                    //centerMTE.setBelongs(mte.getBelongs())
                    handled = true;
                }
            }
            if(!handled){
                center.setBelongs(center.createPipeNet());
            }
            firstTicked = true;
        }
        //第二tick
        else {
            //将MTE的Net设置为已经存在的网络
            var mte = LevelUtils.getMTE(center.getLevel(),centerPos);
            if(mte instanceof MTEPipeWithNet pipe && !center.getPos().equals(centerPos)){
                pipe.getBelongs().combine(center.getPos());
            }
            //不应该执行?
            else {
                center.setBelongs(center.createPipeNet());
            }
        }
    }

我想注释已经说的比较清楚了 center一类的信息都会被卸载过的管道保存

#### 连接

    @Override
    public PipeNet combine(BlockPos other) {
        if(connected.containsKey(other)) return this;
        var mte = LevelUtils.getMTE(getCenter().getLevel(),other);
        if(mte instanceof MTEPipeWithNet pipe){
            tryLink(pipe.getPos());
        }
        return this;
    }
    
    public void tryLink(BlockPos blockPos){
        for(var mte : LevelUtils.getConnected(getLevel(),blockPos,true)){
            if(mte.havePipeNet() ){
                var net = mte.getBelongs();
                if(net != this){
                   // this.connected.putAll(net.getConnectedRaw());
                    for(var pipe:net.getConnected()){
                        if(canCombine(pipe.getBelongs())){
                            this.connected.put(pipe.getPos(),pipe);
                            pipe.setBelongs(this);
                            changed = true;
                            IOChanged = true;
                        }
                    }
                }
            }
        }
    }

md,绷不住了 不是递归执行 我自己真想不通为什么能跑

#### 断开

那更是暴力
直接去掉部分区域然后用BFS检测整个网络连通性

     public void _checkNet(boolean force, BlockPos... removed) {
        var needMarkDirty = false;
        if((getOffsetTick() % 100 == 0 && changed) || force){
            var s = new BFSSearcher();
            s.setRemoved(removed);
            while (s.hasNext()){
                s.next();
            }
            var result = new HashSet<>(s.getSearched());
            Map<BlockPos,MTEPipeWithNet> buff = new Object2ObjectLinkedOpenHashMap<>();
            for(var pos : connected.keySet()){
                var pipe = LevelUtils.getPipe(level,pos);
                if (result.contains(pos)) {
                    if(pipe != null){
                        buff.put(pos,pipe);
                    }
                    result.remove(pos);
                }
                else {
                    if(pipe != null){
                        pipe.setBelongs(new UninitPipeNet(pipe,pos));
                    }
                }
            }
            for(var pos : result){
                var pipe = LevelUtils.getPipe(level,pos);
                if(pipe != null){
                    buff.put(pos,pipe);
                    pipe.setBelongs(this);
                }
            }
            connected.clear();
            connected.putAll(buff);
            changed = false;
            IOChanged = true;
            needMarkDirty = true;
        }
        ......
        if(needMarkDirty) centerMTE.markDirty();
    }

原有部分不变 断开部分直接在分配一个未初始化的PipeNet让它自己重新初始化并联通它需要联通的部分(没有那种直接的转化)


### 网络逻辑执行

如何不需要中心方块加载也能执行网络逻辑?

其实很简单 网络里的每个方块都会执行一遍网络的逻辑 只不过会记录最后一次的tick数 如果tick数跟当前tick数相等直接返回 保证每tick只执行一次