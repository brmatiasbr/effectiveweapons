package net.br_matias_br.effectiveweapons.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class RefreshParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = Random.create();
	private final SpriteProvider spriteProvider;
	private float defaultAlpha = 1.0F;

	RefreshParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0, velocityY, 0);
		this.velocityMultiplier = 0.96F;
		this.gravityStrength = -0.1F;
		this.ascending = true;
		this.spriteProvider = spriteProvider;
		this.velocityY *= 0.2F;

		this.velocityX = 0;
		this.velocityZ = 0;

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.spriteProvider);
			this.alpha = MathHelper.lerp(0.05F, this.alpha, this.defaultAlpha);
	}

	@Override
	protected void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.defaultAlpha = alpha;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new RefreshParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
