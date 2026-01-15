package com.jerotes.jerotes.item.Interface;

public interface ItemTwoHanded {
    public int getBlockReduction();

    default boolean canBlock() {
        return true;
    }
}

