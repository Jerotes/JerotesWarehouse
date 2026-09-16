package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MirrorImageMainEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    public MirrorImageMainEntity(EntityType<? extends MirrorImageMainEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public MirrorImageMainEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.MIRROR_IMAGE_MAIN.get(), p_36926_);
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
    public boolean moveToTarget() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (getOwner() != null) {
            if (!getOwner().level().isClientSide()) {
//            getOwner().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false));
                getOwner().addEffect(new MobEffectInstance(JerotesMobEffects.MIRROR_IMAGE.get(), 5, 0, false, false));
            }
        }
        if (this.getLifeTick() >= 20 && this.getLifeTick() % 10 == 0) {
            boolean bl = true;
            for (UUID uuid : getList()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if (entity instanceof MirrorImageEntity) {
                        bl = false;
                    }
                }
            }
            if (bl) {
                this.discard();
            }
        }
    }


    public List<UUID> ListMirrorImage = new ArrayList<>();

    public void add(UUID uuid) {
        ListMirrorImage.add(uuid);
    }

    public void remove(UUID uuid) {
        ListMirrorImage.remove(uuid);
    }

    public List<UUID> getList() {
        return ListMirrorImage;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ListTag listTag = new ListTag();
        for (UUID uuid : ListMirrorImage) {
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
        compoundTag.put("MirrorImageList", listTag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ListMirrorImage.clear();
        ListTag listTag = compoundTag.getList("MirrorImageList", Tag.TAG_INT_ARRAY);
        for (Tag tag : listTag) {
            if (tag instanceof IntArrayTag intArrayTag) {
                int[] ints = intArrayTag.getAsIntArray();
                if (ints.length == 4) {
                    long msb = ((long) ints[0] << 32) | (ints[1] & 0xFFFFFFFFL);
                    long lsb = ((long) ints[2] << 32) | (ints[3] & 0xFFFFFFFFL);
                    ListMirrorImage.add(new UUID(msb, lsb));
                }
            }
        }
    }
}

