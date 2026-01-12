package com.jerotes.jerotes.alchemy.forge;

import com.jerotes.jerotes.alchemy.effect.AAAAlchemyEffect;
import com.jerotes.jerotes.alchemy.effect.AAANullAlchemyEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class JerotesAlchemySpecialEvent extends Event {
    private final ItemStack material1;
    private final ItemStack material2;
    private final ItemStack material3;
    private final ItemStack material4;
    private final ItemStack material5;
    private boolean special;
    private int specialLevel = 1;
    private AAAAlchemyEffect alchemyEffect = new AAANullAlchemyEffect(1,1);

    public JerotesAlchemySpecialEvent(ItemStack material1, ItemStack material2, ItemStack material3, ItemStack material4, ItemStack material5) {
        this.material1 = material1;
        this.material2 = material2;
        this.material3 = material3;
        this.material4 = material4;
        this.material5 = material5;
        this.special = false;
    }

    public ItemStack getMaterial1() {
        return this.material1;
    }
    public ItemStack getMaterial2() {
        return this.material2;
    }
    public ItemStack getMaterial3() {
        return this.material3;
    }
    public ItemStack getMaterial4() {
        return this.material4;
    }
    public ItemStack getMaterial5() {
        return this.material5;
    }
    public boolean isSpecial() {
        return this.special;
    }
    public void setSpecial(boolean bl) {
        this.special = bl;
    }
    public int getSpecialLevel() {
        return specialLevel;
    }
    public void setSpecialLevel(int specialLevel) {
        this.specialLevel = specialLevel;
    }

    public AAAAlchemyEffect getSpecial() {
        return alchemyEffect;
    }

    public void setSpecial(AAAAlchemyEffect alchemyEffect) {
        this.alchemyEffect = alchemyEffect;
    }
}
