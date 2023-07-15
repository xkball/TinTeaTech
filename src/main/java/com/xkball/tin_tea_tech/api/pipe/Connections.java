package com.xkball.tin_tea_tech.api.pipe;

public enum Connections {
    UP(0),
    DOWN(1),
    NORTH(2),
    EAST(3),
    SOUTH(4),
    WEST(5),
    UP_NORTH(6),
    UP_EAST(7),
    UP_SOUTH(8),
    UP_WEST(9),
    DOWN_NORTH(10),
    DOWN_EAST(11),
    DOWN_SOUTH(12),
    DOWN_WEST(13),
    NORTH_EAST(14),
    SOUTH_EAST(15),
    NORTH_WEST(16),
    SOUTH_WEST(17)
    ;
    
    
    final int index;
    
    Connections(int index) {
        this.index = index;
    }
}
