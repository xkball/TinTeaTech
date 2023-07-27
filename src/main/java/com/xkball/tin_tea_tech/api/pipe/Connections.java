package com.xkball.tin_tea_tech.api.pipe;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mezz.jei.core.util.Pair;
import net.minecraft.core.Direction;

import java.util.*;

public enum Connections {
    //Direction顺序不能变 第一个是从旋转其的轴向看(P)其逆时针方向的第一个Direction
    UP(0,Direction.UP),
    DOWN(1,Direction.DOWN),
    NORTH(2,Direction.NORTH),
    EAST(3,Direction.EAST),
    SOUTH(4,Direction.SOUTH),
    WEST(5,Direction.WEST),
    UP_NORTH(6,Direction.NORTH,Direction.UP),
    UP_EAST(7,Direction.EAST,Direction.UP),
    UP_SOUTH(8,Direction.UP,Direction.SOUTH),
    UP_WEST(9,Direction.UP,Direction.WEST),
    DOWN_NORTH(10,Direction.DOWN,Direction.NORTH),
    DOWN_EAST(11,Direction.DOWN,Direction.EAST),
    DOWN_SOUTH(12,Direction.SOUTH,Direction.DOWN),
    DOWN_WEST(13,Direction.WEST,Direction.DOWN),
    NORTH_EAST(14,Direction.EAST,Direction.NORTH),
    SOUTH_EAST(15,Direction.SOUTH,Direction.EAST),
    NORTH_WEST(16,Direction.NORTH,Direction.WEST),
    SOUTH_WEST(17,Direction.WEST,Direction.SOUTH)
    ;
    
    public static final Collection<Connections> verticalConnections = List.of(UP,DOWN,NORTH,EAST,SOUTH,WEST);
    
    public static final Collection<Connections> ZRoundConnections = List.of(UP_EAST,DOWN_EAST,UP_WEST,DOWN_WEST);
    
    public static final Collection<Connections> YRoundConnections = List.of(NORTH_EAST,NORTH_WEST,SOUTH_EAST,SOUTH_WEST);
    
    public static final Collection<Connections> XRoundConnections = List.of(UP_NORTH,DOWN_NORTH,UP_SOUTH,DOWN_SOUTH);
    
    public static final Collection<Connections> values = List.of(Connections.values());
    private static class ConnectionsData{
        public static final Multimap<Direction, Pair<Connections,Direction>> dConnections = LinkedHashMultimap.create(6,5);
        public static final Map<Connections,Pair<Direction,Direction>> cConnections = new HashMap<>();
    }
    
    final int index;
    
    Connections(int index,Direction... contains) {
        this.index = index;
        if(contains.length == 1){
            var d = contains[0];
            ConnectionsData.dConnections.put(d,new Pair<>(this,d));
            ConnectionsData.cConnections.put(this,new Pair<>(d,d));
        }
        else if(contains.length == 2){
            var d1 = contains[0];
            var d2 = contains[1];
            ConnectionsData.dConnections.put(d1,new Pair<>(this,d2));
            ConnectionsData.dConnections.put(d2,new Pair<>(this,d1));
            ConnectionsData.cConnections.put(this,new Pair<>(d1,d2));
        }
    }
    
    public static Connections formDirection(Direction direction){
        return switch (direction){
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
    
    public Direction toDirection(){
        return ConnectionsData.cConnections.get(this).first();
    }
    
    public static Connections find(Direction d1,Direction d2){
        return ConnectionsData.dConnections.get(d1).stream()
                .filter((p) -> p.second().equals(d2)).findFirst()
                .orElseThrow().first();
    }
    
    public int toCID(){
        return this.index*2;
    }
    
    public int toBID(){
        return this.index*2+1;
    }
    
}
