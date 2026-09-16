package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class ConcentrationBestowCurseEntity extends BestowCurseEntity implements ConcentrationSpellEntity {
    public ConcentrationBestowCurseEntity(EntityType<? extends ConcentrationBestowCurseEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public ConcentrationBestowCurseEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.CONCENTRATION_BESTOW_CURSE.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
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
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_BESTOW_CURSE.magicSpellGet(1,null,null).getSpellName());
        }
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 10));
        }
        return true;
    }
}
