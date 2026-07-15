package com.jerotes.jerotes.forge;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.spell.MagicSpell;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class JerotesStopSpellEvent extends EntityEvent {
    private Entity caster;
    private Entity target;
    private MagicSpell magicSpell;
    private Entity spellEntity;
    public int type;
    public int level;

    public JerotesStopSpellEvent(Entity caster, @Nullable MagicSpell magicSpell, @Nullable Entity spellEntity, Entity target, int type, int level) {
        super(caster);
        this.caster = caster;
        this.target = target;
        this.type = type;
        this.level = level;
    }
    public JerotesStopSpellEvent(Entity caster, @Nullable MagicSpell magicSpell, @Nullable Entity spellEntity, Entity target) {
        this(caster, magicSpell, spellEntity, target, 0, spellEntity instanceof MagicAboutEntity magicAbout ? magicAbout.getSpellLevel() : magicSpell != null ? magicSpell.getSpellLevel() : 1);
    }
    public int getType() {
        //1-伤害性法术行为
        //2-中立性法术行为
        //3-友善性法术行为
        return this.type;
    }
    public int getLevel() {
        return this.level;
    }

    public Entity getCaster() {
        return this.caster;
    }
    public void setCaster(Entity caster) {
        this.caster = caster;
    }
    public Entity getTarget() {
        return this.target;
    }
    public void setTarget(Entity target) {
        this.target = target;
    }
    public Entity getSpellEntity() {
        return this.spellEntity;
    }
    public void setSpellEntity(Entity spellEntity) {
        this.spellEntity = spellEntity;
    }
    public MagicSpell getMagicSpell() {
        return this.magicSpell;
    }
    public void setMagicSpell(MagicSpell magicSpell) {
        this.magicSpell = magicSpell;
    }
}
