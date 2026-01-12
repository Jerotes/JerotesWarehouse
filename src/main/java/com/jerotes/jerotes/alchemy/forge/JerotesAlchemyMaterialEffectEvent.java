package com.jerotes.jerotes.alchemy.forge;

import com.jerotes.jerotes.alchemy.effect.AAAAlchemyEffect;
import com.jerotes.jerotes.alchemy.effect.AAANullAlchemyEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class JerotesAlchemyMaterialEffectEvent extends Event {
    private final ItemStack material;
    private int effectCount;
    private AAAAlchemyEffect effect1 = new AAANullAlchemyEffect(1,1);
    private AAAAlchemyEffect effect2 = new AAANullAlchemyEffect(1,1);
    private AAAAlchemyEffect effect3 = new AAANullAlchemyEffect(1,1);
    private AAAAlchemyEffect effect4 = new AAANullAlchemyEffect(1,1);
    private AAAAlchemyEffect effect5 = new AAANullAlchemyEffect(1,1);
    private int maxCount = 1;

    public JerotesAlchemyMaterialEffectEvent(ItemStack material) {
        this.material = material;
        this.effectCount = 0;
    }

    public ItemStack getMaterial() {
        return this.material;
    }

    public int getEffectCount() {
        return effectCount;
    }

    public void setEffectCount(int effectCount) {
        this.effectCount = effectCount;
    }
    public int getMaxCount() {
        return maxCount;
    }
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public AAAAlchemyEffect getEffect1() {
        return effect1;
    }

    public void setEffect1(AAAAlchemyEffect effect1) {
        this.effect1 = effect1;
    }

    public AAAAlchemyEffect getEffect2() {
        return effect2;
    }

    public void setEffect2(AAAAlchemyEffect effect2) {
        this.effect2 = effect2;
    }

    public AAAAlchemyEffect getEffect3() {
        return effect3;
    }

    public void setEffect3(AAAAlchemyEffect effect3) {
        this.effect3 = effect3;
    }

    public AAAAlchemyEffect getEffect4() {
        return effect4;
    }

    public void setEffect4(AAAAlchemyEffect effect4) {
        this.effect4 = effect4;
    }

    public AAAAlchemyEffect getEffect5() {
        return effect5;
    }

    public void setEffect5(AAAAlchemyEffect effect5) {
        this.effect5 = effect5;
    }
}