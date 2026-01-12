package com.jerotes.jerotes.item;

public interface ItemTwoHanded {
    public int getBlockReduction();

    default boolean canBlock() {
        return true;
    }
}

