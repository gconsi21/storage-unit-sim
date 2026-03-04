package com.gcons.domain;

public class ItemInstance {
    private final ItemDefinition definition;

    public ItemInstance(ItemDefinition definition){

        if (definition == null){
            throw new IllegalArgumentException("definition must not be null");
        } 
        this.definition = definition;
    }

    public ItemDefinition getDefinition() {
        return definition;
    }
    
}
