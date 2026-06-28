package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.WizardEntity;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.MagicSpell;
import com.jerotes.jerotes.spell.MagicType;
import com.jerotes.jerotes.spell.SpellListByString;
import com.jerotes.jerotes.spell.SpellTypeInterface;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.List;

public class JerotesAddSpellAttackGoal extends Goal {
    public final Mob mob;
    @Nullable
    public LivingEntity target;
    public int attackTime = -1;
    public int changeSpellTime = -1;
    public int changeSpellCooldown = 1;
    public int attackCooldown = 1;
    public int spellLevel = 1;
    public float randomSpellChance = 0.5f;
    public SpellTypeInterface SpellTypeInterface;

    public JerotesAddSpellAttackGoal(Mob mob, int spellLevel, int attackCooldown, int changeSpellCooldown, float randomSpellChance) {
        this.mob = mob;
        this.spellLevel = spellLevel;
        this.attackCooldown = attackCooldown;
        this.changeSpellCooldown = changeSpellCooldown;
        this.randomSpellChance = randomSpellChance;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (this.mob instanceof WizardEntity wizardEntity) {
            List<SpellTypeInterface> spellList = wizardEntity.AddSpellList();
            if (!spellList.isEmpty()) {
                this.target = livingEntity;
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.mob.getTarget() != null && this.mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        SpellTypeInterface = selectSpell();
        this.attackTime = 5;
    }

    @Override
    public void stop() {
        this.target = null;
        this.attackTime = -1;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextInt(changeSpellCooldown) == 1) {
            this.changeSpellTime = changeSpellCooldown;
            //重置法术选择
            SpellTypeInterface = selectSpell();
        }
        if (this.mob instanceof WizardEntity wizardEntity && wizardEntity.stopUseAddSpellInGoal()) {
            return;
        }
        if (Main.getJerotesPersistentData(this.mob).getDouble("jerotes_add_spell_cooldown") > 0) {
            return;
        }
        if (Main.getJerotesPersistentData(this.mob).getDouble("jerotes_spell_cooldown") > 0) {
            return;
        }
        Entity finalTarget = this.target;
        int TrueSpellLevel = this.spellLevel;
        //法术等级校对
        if (this.mob.getMainHandItem().getItem() instanceof MagicItem magicItem && magicItem.getAddSpellType(this.mob.getMainHandItem()).contains(SpellTypeInterface)) {
            TrueSpellLevel = Math.max(magicItem.getSpellLevel(this.mob.getMainHandItem()), TrueSpellLevel);
        }
        if (this.mob.getOffhandItem().getItem() instanceof MagicItem magicItem && magicItem.getAddSpellType(this.mob.getOffhandItem()).contains(SpellTypeInterface)) {
            TrueSpellLevel = Math.max(magicItem.getSpellLevel(this.mob.getOffhandItem()), TrueSpellLevel);
        }
        --this.attackTime;

        MagicSpell magicSpell = null;
        //目标
        if (SpellTypeInterface != null) {
            magicSpell = SpellListByString.getSpell(TrueSpellLevel, this.mob, finalTarget, SpellTypeInterface);
            if (magicSpell.getMagicType() == MagicType.SELF) {
                finalTarget = this.mob;
            }
            else if (magicSpell.isHelp()) {
                boolean bl = false;
                if (this.mob.getRandom().nextFloat() > magicSpell.shouldBeSelf()) {
                    float distance = magicSpell.getSpellDistance();
                    List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(distance, distance, distance));
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity == null) continue;
                        if (livingEntity == this.mob) continue;
                        if (livingEntity == this.mob.getTarget()) continue;
                        if (!AttackFind.SameFactionAvoidDamage(this.mob, livingEntity, false)) continue;
                       finalTarget = livingEntity;
                        bl = true;
                    }
                }
                if (!bl && magicSpell.canUseTargetNone()) {
                    finalTarget = this.mob;
                }
                else if (!bl) {
                    //重置法术选择
                    SpellTypeInterface = selectSpell();
                    return;
                }
            }
        }

        if (magicSpell == null || SpellTypeInterface == null) {
            SpellTypeInterface = selectSpell();
            return;
        }
        //不能用
        boolean canNotUse = !magicSpell.canUse();
        //目标不正确
        boolean notCanUseTarget = magicSpell.getMagicType() == MagicType.TARGET && finalTarget != null && !magicSpell.canUseToEntity(finalTarget);
        //间隔
        boolean hasLineOfSight = magicSpell.getMagicType() == MagicType.SHOOT && finalTarget != null
                && !this.mob.getSensing().hasLineOfSight(finalTarget);
        //过远
        boolean distanceTooFar = magicSpell.getMagicType() != MagicType.SELF && finalTarget != null
                && this.mob.distanceTo(finalTarget) > magicSpell.getSpellDistance();
        //治疗敌人
        boolean healEnemy = magicSpell.isHelp() && finalTarget == this.target;
        //无目标
        boolean noTarget = magicSpell.getMagicType() != MagicType.SELF && finalTarget == null;
        //此法术战斗中使用
        boolean useInCombat = magicSpell.mobUseInCombat() && this.target == null;

        boolean bl = !canNotUse && !hasLineOfSight && !distanceTooFar && !healEnemy && !noTarget && !notCanUseTarget && !useInCombat;

        if (!bl) {
            //重置法术选择
            SpellTypeInterface = selectSpell();
            return;
        }
        else {
            this.changeSpellTime --;
        }
        if (this.attackTime <= 0) {
            //如果没有这个法术
            if (!(this.mob instanceof WizardEntity wizardEntity) || !wizardEntity.AddSpellList().contains(SpellTypeInterface)) {
                SpellTypeInterface = selectSpell();
                return;
            }
            //施法前摇
            wizardEntity.SpellUseAfterAttack(SpellTypeInterface.getId(), magicSpell.getMagicType(), magicSpell.getMagicType2());
            //施法
            SpellListByString.getSpell(TrueSpellLevel, this.mob, finalTarget, SpellTypeInterface).spellUse();
            Main.getJerotesPersistentData(this.mob).putDouble("jerotes_add_spell_cooldown", Math.max(attackCooldown, Main.getJerotesPersistentData(this.mob).getDouble("jerotes_add_spell_cooldown")));
            this.mob.stopUsingItem();
            //如何损坏
            {
                boolean shouldBreak = false;
                boolean breakHandIsAdd = true;
                //如果没有这个法术还使用了
                if (!((wizardEntity.SelfAddSpellList().contains(SpellTypeInterface) && wizardEntity.isUseSelfNotStringSpellList() || (wizardEntity.SelfAddSpellStringList().contains(SpellTypeInterface))))) {
                    shouldBreak = true;
                }
                //如果使用了更强于自己水平的法术
                if (spellLevel < TrueSpellLevel) {
                    shouldBreak = true;
                }
                //判断哪个手
                int spell = this.spellLevel;
                if (this.mob.getMainHandItem().getItem() instanceof MagicItem magicItem && magicItem.getAddSpellType(this.mob.getMainHandItem()).contains(SpellTypeInterface)) {
                    if (magicItem.getSpellLevel(this.mob.getMainHandItem()) > spellLevel) {
                        spell = magicItem.getSpellLevel(this.mob.getMainHandItem());
                    }
                }
                if (this.mob.getOffhandItem().getItem() instanceof MagicItem magicItem && magicItem.getAddSpellType(this.mob.getOffhandItem()).contains(SpellTypeInterface)) {
                    if (magicItem.getSpellLevel(this.mob.getOffhandItem()) > spellLevel && magicItem.getSpellLevel(this.mob.getOffhandItem()) > spell) {
                        breakHandIsAdd = false;
                    }
                }
                if (shouldBreak) {
                    if (breakHandIsAdd) {
                        if (this.mob.getMainHandItem().getItem() instanceof MagicItem magicItem) {
                            magicItem.damageMagicItem(this.mob, this.mob.getMainHandItem());
                        }
                    }
                    else {
                        if (this.mob.getOffhandItem().getItem() instanceof MagicItem magicItem) {
                            magicItem.damageMagicItem(this.mob, this.mob.getOffhandItem());
                        }
                    }
                }
            }
            this.attackTime = attackCooldown;
            this.changeSpellTime = changeSpellCooldown;
            //重置法术选择
            SpellTypeInterface = selectSpell();
            return;
        }
        if (this.changeSpellTime <= 0) {
            this.changeSpellTime = changeSpellCooldown;
            //重置法术选择
            SpellTypeInterface = selectSpell();
        }
    }
    private int currentOrderIndex = 0;

    public SpellTypeInterface selectSpell() {
        if (this.mob instanceof WizardEntity wizardEntity) {
            List<SpellTypeInterface> spellList = wizardEntity.AddSpellList();
            if (spellList.isEmpty()) {
                throw new IllegalStateException("法术列表为空");
            }
            if (mob.getRandom().nextFloat() < randomSpellChance) {
                return getRandomSpell(spellList);
            } else {
                return getSequentialSpell(spellList);
            }
        }
        return null;
    }
    private SpellTypeInterface getRandomSpell(List<SpellTypeInterface> spellList) {
        return spellList.get(mob.getRandom().nextInt(spellList.size()));
    }
    public SpellTypeInterface getSequentialSpell(List<SpellTypeInterface> spellList) {
        if (currentOrderIndex >= spellList.size()) {
            currentOrderIndex = 0;
        }

        SpellTypeInterface selected = spellList.get(currentOrderIndex);
        currentOrderIndex = (currentOrderIndex + 1) % spellList.size();
        return selected;
    }
}

