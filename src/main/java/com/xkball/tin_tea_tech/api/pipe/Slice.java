package com.xkball.tin_tea_tech.api.pipe;

import java.util.ArrayList;
import java.util.Collection;

public enum Slice {
    Vertical(),
    X(),
    Y(),
    Z();
    
    final Collection<Connections> connections = new ArrayList<>(6);
    
    Slice(){}
    
    public void append(Connections connection){
        connections.add(connection);
    }
    
    public Collection<Connections> getAll(){
        return connections;
    }
}
