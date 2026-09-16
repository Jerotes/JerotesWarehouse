package com.jerotes.jerotes.entity.Other.Other;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class InvisibleLightningBolt extends LightningBolt {
    public InvisibleLightningBolt(EntityType<? extends InvisibleLightningBolt> entityType, Level level) {
        super(entityType, level);
    }
    public void tick() {
        super.tick();
    }

}
