package com.xkball.tin_tea_tech.common.robot;

import com.xkball.tin_tea_tech.api.pipe.Connections;

import java.util.EnumMap;

public class Node {
    
    public final EnumMap<Connections,Node> links = new EnumMap<>(Connections.class);
    
}
