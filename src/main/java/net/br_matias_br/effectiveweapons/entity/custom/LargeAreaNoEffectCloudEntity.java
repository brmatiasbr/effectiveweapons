package net.br_matias_br.effectiveweapons.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
        this.frigid = frigid;
    }

    public LargeAreaNoEffectCloudEntity(EntityType<LargeAreaNoEffectCloudEntity> entityType, World world) {
        super(entityType, world, true);
    }
}
