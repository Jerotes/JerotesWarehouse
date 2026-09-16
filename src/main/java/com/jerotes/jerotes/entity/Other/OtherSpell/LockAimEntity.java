package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class LockAimEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {

    public LockAimEntity(EntityType<? extends LockAimEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LockAimEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.LOCK_AIM.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public boolean scaleAboutBoundingBox() {
        return true;
    }
    @Override
    public int getMaxLife() {
        return 20 * 10 * 60;
    }
    public boolean moveToTarget() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() != null && this.getTarget() != null) {
            this.getOwner().lookAt(EntityAnchorArgument.Anchor.EYES, this.getTarget().getPosition(0.02f).add(0, Main.mobHeight(getTarget()) * 0.7f, 0));
          //  lookAt(getOwner(),getTarget(),90,90);
        }
    }


    public void lookAt(LivingEntity owner, Entity p_21392_, float p_21393_, float p_21394_) {
        double d0 = p_21392_.getX() - owner.getX();
        double d2 = p_21392_.getZ() - owner.getZ();
        double d1;
        if (p_21392_ instanceof LivingEntity livingentity) {
            d1 = livingentity.getEyeY() - owner.getEyeY();
        } else {
            d1 = (p_21392_.getBoundingBox().minY + p_21392_.getBoundingBox().maxY) / 2.0D - this.getEyeY();
        }

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
        float f1 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
        owner.setXRot(this.rotlerp(owner.getXRot(), f1, p_21394_));
        owner.setYRot(this.rotlerp(owner.getYRot(), f, p_21393_));
    }

    private float rotlerp(float p_21377_, float p_21378_, float p_21379_) {
        float f = Mth.wrapDegrees(p_21378_ - p_21377_);
        if (f > p_21379_) {
            f = p_21379_;
        }
        if (f < -p_21379_) {
            f = -p_21379_;
        }
        return p_21377_ + f;
    }


    @Override
    public boolean endConcentration() {
        if (getOwner() != null) {
            SpellType.setConcentrationSpellTypeJerotes(getOwner(), "");
            if (getOwner() instanceof Player player) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setConcentrationSpellTarget("");
                    capability.syncPlayerVariables(player);
                });
            }
            SpellType.setConcentrationSpellTargetJerotes(getOwner(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_LOCK_AIM.magicSpellGet(1,null,null).getSpellName());
        }
        this.discard();
        return true;
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }
}

