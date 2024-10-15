# 锡茶科技 TinTeaTech

![sample image](https://github.com/xkball/TinTeaTech/blob/master/display_20230909.png "本模组部分方块和物品. Some blocks and items in this mod.")

## 简介 Introduction

锡茶科技是一个Minecraft模组,目前支持的环境包括 Minecraft 1.20.1 Forge.(兼容Forge 47.1.0)

本模组计划制作 成型的机器体系 多方块机器 管道和非管道物流 能量 地形生成 辅助功能 等.

目前完成的部分(2023.9.9更新):

- 云钩(或者说"全息眼镜"),头部装备,本质是一个hud和辅助功能等管理器,已经实现的辅助功能包括方位指示,方块信息显示(类似top和jade),强制显示永夜或永昼,玩家惯性消除等
- 管道,包含 物品,流体,末影物品,末影流体,FE,蒸汽(正在编写的一个能源系统),物流机器人(其实是正在写的非管道物流)版本 支持斜向连接,换言之每个管道都可以与18个其他方向连接
- 流体储罐,锅炉(对单方块机器的实现的验证)
- 铁脚手架,比原版更强大的一种脚手架

Tin Tea Technology is a Minecraft mod. Support Minecraft 1.20.1 Forge.(including Forge 47.1.0)

This mod plans to create a formed machine system, multi-block machines, pipes and non-pipeline logistics, energy, terrain generation, auxiliary functions, etc.

The currently completed part (updated on 2023.9.9):

- HoloGlass,head equipment, is essentially a manager of HUD and auxiliary functions. 
The auxiliary functions that have been implemented include orientation indication, 
block information display (similar to top or jade), forced display of eternal night or eternal day, elimination of player inertia, etc.

- Pipes, including items, fluids, ender items, ender fluids, FE, steam (an energy system being written), logistics robots
(actually non-pipeline logistics being written) version support oblique connections, in other words each pipe Can be connected with 18 other directions

- Fluid Storage Tank, Boiler (verification of implementation of single block machine)

- Iron scaffolding, a kind of scaffolding that is more powerful than the original version

![pipe demo](https://github.com/xkball/TinTeaTech/blob/master/images/pipe_demo.png "Pipe demo")

## 特别说明

本模组参与了TeaCon2023 https://www.teacon.cn/2023/

本模组参与"言传身教"奖项内容: https://github.com/xkball/TinTeaTech/tree/master/tecnical_details

## 许可证 License

 本仓库内容使用以下许可证:

- 源代码和其构建产物使用LGPL-3.0
- 原创美术资源使用CC BY-NC 4.0
- 特殊情况:命名空间为tectech下的美术资源来自 https://github.com/GTNewHorizons/TecTech/tree/master ,使用MIT协议


The contents of this repository are licensed as follows:
- in the case of original source code from tin_tea_tech or compiled artifacts generated from it, under LGPL-3.0.
- in the case of original art assets from tin_tea_tech, under CC BY-NC 4.0.
- Special case: The art assets under the namespace tectech come from https://github.com/GTNewHorizons/TecTech/tree/master, using the MIT license


## 构建 Build

 请添加如下jvm参数,或者解除build.gradle文件181行到189行注释.

 Please add the following jvm args or uncomment lines 181 to 189 of build.gradle.

```
--add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
```
