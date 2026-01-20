package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.AACreativeClaw;
import com.jerotes.jerotes.item.AAExplorationEye;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Tool.ItemToolBaseBandage;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class DamageTypeEvent {
	@SubscribeEvent
	public static void DamageType(LivingDamageEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource attackBy = event.getSource();
		float amount = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		float f = 1.0f;
		if (attackBy != null) {
			//毒素
			if (attackBy.is(JerotesDamageTypes.POISON)) {
				if (EntityFactionFind.isConstruct(entity) || EntityFactionFind.isMachine(entity)) {
					f /= 4;
				}
				if (entity.getMobType() == MobType.UNDEAD) {
					f /= 2;
				}
				if (EntityFactionFind.isOoze(entity)) {
					f /= 2;
				}
			}
			//腐蚀
			if (attackBy.is(JerotesDamageTypes.CORROSIVE)) {
				if (EntityFactionFind.isConstruct(entity) || EntityFactionFind.isMachine(entity)) {
					f *= 2;
				}
				if (EntityFactionFind.isOoze(entity)) {
					f /= 2;
				}
			}
			event.setAmount(event.getAmount() * f);
		}
	}
}