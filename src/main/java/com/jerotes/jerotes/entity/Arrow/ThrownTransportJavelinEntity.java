package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownTransportJavelinEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.TRANSPORT_JAVELIN.get());
	private static final EntityType<ThrownTransportJavelinEntity> type = JerotesEntityType.THROWN_TRANSPORT_JAVELIN.get();
	public ThrownTransportJavelinEntity(EntityType<? extends ThrownTransportJavelinEntity> entityType, Level level) {
		super(entityType, level, item, 3.0f);
	}
	public ThrownTransportJavelinEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 3.0f);
	}

	@Override
	public boolean canLoyalty() {
		return false;
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		for (int i = 0; i < 32; ++i) {
			this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
		}
		if (!this.level().isClientSide && !this.isRemoved()) {
			Entity entity = this.getOwner();
			if (entity instanceof ServerPlayer) {
				ServerPlayer serverPlayer = (ServerPlayer)entity;
				if (serverPlayer.connection.isAcceptingMessages() && serverPlayer.level() == this.level() && !serverPlayer.isSleeping()) {
					Endermite endermite;
					if (this.random.nextFloat() < 0.05f && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && (endermite = EntityType.ENDERMITE.create(this.level())) != null) {
						endermite.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
						this.level().addFreshEntity(endermite);
					}
					if (entity.isPassenger()) {
						serverPlayer.dismountTo(this.getX(), this.getY(), this.getZ());
					} else {
						entity.teleportTo(this.getX(), this.getY(), this.getZ());
					}
					entity.resetFallDistance();
					this.playSound(JerotesSounds.TELEPORT, 1.0F, 1.0F);
				}
			}
			else if (entity != null) {
				entity.teleportTo(this.getX(), this.getY(), this.getZ());
				entity.resetFallDistance();
			}
		}
	}
}
