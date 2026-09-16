package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
		if (entity instanceof JerotesEntity jerotes && !jerotes.SpecialDamageTypeChangeAmount())
			return;
		float f = 1.0f;
		if (attackBy != null) {
			//毒素
			if (AttackFind.isPoisonDamage(attackBy)) {
				if (EntityFactionFind.isConstruct(entity) || EntityFactionFind.isMachine(entity)) {
					f /= 2;
				}
				if (entity.getMobType() == MobType.UNDEAD) {
					f /= 2;
				}
				if (EntityFactionFind.isOoze(entity)) {
					f /= 2;
				}
			}
			//腐蚀
			if (AttackFind.isCorrosionDamage(attackBy)) {
				if (EntityFactionFind.isMachine(entity)) {
					f *= 2;
				}
				if (EntityFactionFind.isOoze(entity)) {
					f /= 2;
				}
			}
			//暗蚀
			if (AttackFind.isNecroticDamage(attackBy)) {
				if (entity.getMobType() == MobType.UNDEAD) {
					f /= 2;
				}
			}
//			//光耀
//			if (AttackFind.isRadiantDamage(attackBy)) {
//				if (entity.getMobType() == MobType.UNDEAD) {
//					f *= 2;
//				}
//			}
			//心灵
			if (AttackFind.isPsychicDamage(attackBy)) {
				if (EntityFactionFind.isConstruct(entity) || EntityFactionFind.isMachine(entity)) {
					f /= 2;
				}
				else if (entity instanceof Warden) {
					f *= 2;
				}
			}
			event.setAmount(event.getAmount() * f);
		}
	}
}