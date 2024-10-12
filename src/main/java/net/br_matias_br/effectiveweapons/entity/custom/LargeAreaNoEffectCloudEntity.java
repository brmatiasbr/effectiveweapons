package net.br_matias_br.effectiveweapons.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LargeAreaNoEffectCloudEntity extends AreaNoEffectCloudEntity {
    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world, int duration) {
        super(entityType, world, true);
        this.duration = duration;
        this.owner = null;
    }

    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner) {
        this(entityType, world, duration);
        this.owner = owner;
    }

    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner, double x, double y, double z) {
        this(entityType, world, duration, owner);
        this.setPosition(x, y, z);
    }


    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner, double x, double y, double z, boolean frigid) {
        this(entityType, world, duration, owner, x, y, z);
        this.setFrigid(frigid);
    }

    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world) {
        super(entityType, world, true);
    }

    @Override
    protected void createParticles() {
        Random random = this.getWorld().getRandom();
        for(int i = 0; i < 15; i++){
            double randomX = random.nextDouble() * 12.5 * (random.nextBoolean() ? 1 : -1);
            double randomZ = random.nextDouble() * 12.5 * (random.nextBoolean() ? 1 : -1);
            double randomY = random.nextDouble() * 2.5  * (random.nextBoolean() ? 1 : -1);

            this.getWorld().addParticle(ParticleTypes.EFFECT, this.getX() + randomX, this.getY() + randomY, this.getZ() + randomZ, 0, 0.01, 0);
        }
    }
}
