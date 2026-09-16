package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HoldPersonMainEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {
    public HoldPersonMainEntity(EntityType<? extends HoldPersonMainEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public HoldPersonMainEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.HOLD_PERSON_MAIN.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 2;
    }
    @Override
    public int getMaxLife() {
        return 20 * 60;
    }
    public int getMaxStart() {
        return 20;
    }
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLifeTick() >= 20 && this.getLifeTick() % 10 == 0) {
            boolean bl = true;
            for (UUID uuid : getList()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if (entity instanceof HoldMobEntity) {
                        bl = false;
                    }
                }
            }
            if (bl) {
                this.endConcentration();
            }
        }
    }

    public List<UUID> ListHoldMob = new ArrayList<>();

    public void add(UUID uuid) {
        ListHoldMob.add(uuid);
    }

    public void remove(UUID uuid) {
        ListHoldMob.remove(uuid);
    }

    public List<UUID> getList() {
        return ListHoldMob;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ListTag listTag = new ListTag();
        for (UUID uuid : ListHoldMob) {
            long msb = uuid.getMostSignificantBits();
            long lsb = uuid.getLeastSignificantBits();
            int[] ints = new int[]{
                    (int) (msb >> 32),
                    (int) msb,
                    (int) (lsb >> 32),
                    (int) lsb
            };
            listTag.add(new IntArrayTag(ints));
        }
        compoundTag.put("HoldMobList", listTag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ListHoldMob.clear();
        ListTag listTag = compoundTag.getList("HoldMobList", Tag.TAG_INT_ARRAY);
        for (Tag tag : listTag) {
            if (tag instanceof IntArrayTag intArrayTag) {
                int[] ints = intArrayTag.getAsIntArray();
                if (ints.length == 4) {
                    // 还原 UUID
                    long msb = ((long) ints[0] << 32) | (ints[1] & 0xFFFFFFFFL);
                    long lsb = ((long) ints[2] << 32) | (ints[3] & 0xFFFFFFFFL);
                    ListHoldMob.add(new UUID(msb, lsb));
                }
            }
        }
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
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_HOLD_PERSON.magicSpellGet(1,null,null).getSpellName());
        }
        List<UUID> uuids = getList();
        for (UUID uuid : uuids) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof HoldMobEntity hold) {
                    hold.stop();
                }
            }
        }
        this.discard();
        return true;
    }
}

