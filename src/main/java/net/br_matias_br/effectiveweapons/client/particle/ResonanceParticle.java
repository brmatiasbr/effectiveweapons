package net.br_matias_br.effectiveweapons.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class ResonanceParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;
	ResonanceParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.scale = 6F;
		this.maxAge = 20;
		this.gravityStrength = 0.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.spriteProvider = spriteProvider;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 0.75F, 0.0F, 1.5F);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.alpha = 1.0F - MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge, 0.0F, 1.0F);
		Quaternionf quaternionf = new Quaternionf();
		quaternionf.rotationX((float) (Math.PI/2));
		this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
		quaternionf.rotationYXZ((float) 0, (float) -Math.PI/2, 0.0F);
		this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.spriteProvider);
		this.alpha = MathHelper.lerp(0.05F, this.alpha, 1f);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			ResonanceParticle particle = new ResonanceParticle(world, x, y, z, spriteProvider);
			particle.setAlpha(1.0F);
			return particle;
		}
	}
}
