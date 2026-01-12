package com.jerotes.jerotes.alchemy.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class JerotesAlchemyMaterialConflictEvent extends Event {
    private final ItemStack material;
    private boolean conflictWith;

    public JerotesAlchemyMaterialConflictEvent(ItemStack material) {
        this.material = material;
        this.conflictWith = false;
    }

    public ItemStack getMaterial() {
        return this.material;
    }
    public boolean isConflictWith(ItemStack itemStack) {
        return this.conflictWith;
    }
    public void setBooleanConflictWith(boolean bl) {
        this.conflictWith = bl;
    }
}
