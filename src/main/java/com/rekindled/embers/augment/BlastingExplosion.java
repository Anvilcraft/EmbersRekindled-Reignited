package com.rekindled.embers.augment;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlastingExplosion extends Explosion {

	private final List<Entity> entitiesToBlast;
	private final float damage;
	@Nullable
	private final Entity source;

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, BlockInteraction interaction) {
		super(level, source, x, y, z, radius, fire, interaction);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void explode() {
		Vec3 center = center();
		double diameter = radius() * 2.0;
		for (Entity entity : entitiesToBlast) {
			if (entity.ignoreExplosion(this) || source != null && Objects.equals(entity.getUUID(), source.getUUID()))
				continue;

			double distance = Math.sqrt(entity.distanceToSqr(center)) / diameter;
			if (distance > 1.0)
				continue;

			Vec3 direction = entity.position().subtract(center).normalize();
			double exposure = (1.0 - distance) * getSeenPercent(center, entity);
			entity.hurt(Explosion.getDefaultDamageSource(entity.level(), source), damage);
			if (entity instanceof LivingEntity living)
				living.hurtTime = 0;
			Vec3 knockback = direction.scale(exposure);
			entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
			if (entity instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying))
				getHitPlayers().put(player, knockback);
		}
	}
}
