package com.xkball.tin_tea_tech.api.client.gui;

public interface ISubScreen {
    boolean canPenetrateInteraction();
    
    int getLayer();
    
    void init();
}
