package net.br_matias_br.effectiveweapons.networking;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ParticleEvents {
    public static void particleEvent(ClientWorld world, int eventType, @Nullable Entity entity, double x, double y, double z){
        if(entity == null){
            if(eventType >= 1 && eventType < 10){
                eventType = 0;
            }
        }

        switch (eventType){
            case 1: spiralParticleEvent(world, entity);
                break;
            case 2: cleanseParticleEvent(world, entity);
                break;
            case 3: effectsStolenParticleEvent(world, entity);
                break;
            case 4: entityHealed(world, entity);
                break;
            case 5: arrowCritical(world, entity);
                break;
            case 6: fireGuardActivation(world, entity);
                break;
            case 7: burstImpact(world, entity);
                break;
            case 8: domainOfFire(world, entity);
                break;
            case 9: resonance(world, entity);
                break;
            default: return;
        }
    }

    public static void particleEvent(ClientWorld world, int eventType, Entity entity){
        particleEvent(world, eventType, entity, entity.getX(), entity.getY(), entity.getZ());
    }

    private static void spiralParticleEvent(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();
        for(int i = 0; i < 36; i++){
            double radianAngle = Math.toRadians(i * 10);
            double radianAngleOpposite = Math.toRadians((i * 10) + 180);

            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    entityX - (1 * Math.sin(radianAngle)), entityY + (i * entity.getHeight() / 36),
                    entityZ + (1 * Math.cos(radianAngle)),
                    0, 0, 0);

            world.addParticle(ParticleTypes.SMALL_FLAME,
                    entityX - (1 * Math.sin(radianAngleOpposite)), entityY + (i * entity.getHeight() / 36),
                    entityZ + (1 * Math.cos(radianAngleOpposite)),
                    0, 0, 0);
        }
    }

    private static void cleanseParticleEvent(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();
        double entityVelocityX = entity.getVelocity().getX(), entityVelocityZ = entity.getVelocity().getZ();
        for(int i = 0; i < 36; i++){
            double radianAngle = Math.toRadians(i * 10);
            double radianAngleOpposite = Math.toRadians((i * 10) + 180);

            world.addParticle(ParticleTypes.END_ROD,
                    entityX - (1 * Math.sin(radianAngle)), entityY + (entity.getHeight() / 2),
                    entityZ + (1 * Math.cos(radianAngle)),
                    entityVelocityX + 0.05 * (-Math.sin(radianAngleOpposite)), 0,
                    entityVelocityZ + 0.05 * (Math.cos(radianAngleOpposite)));
        }
    }

    private static void effectsStolenParticleEvent(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();

        for(int i = 0; i < 36; i++){
            double radianAngle = Math.toRadians(i * 10);
            int nextInt = world.getRandom().nextInt(10);
            boolean paticleSuccessful = nextInt < 3;

            if(paticleSuccessful) world.addParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR,
                    entityX - (1 * Math.sin(radianAngle)), entityY + (entity.getHeight() / 2),
                    entityZ + (1 * Math.cos(radianAngle)),
                    0, 0,
                    0);
        }
    }

    private static void entityHealed(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();

        for(int i = 0; i < 36; i++){
            double radianAngle = Math.toRadians(i * 10);
            int nextInt = world.getRandom().nextInt(10);
            boolean paticleSuccessful = nextInt < 3;

            if(paticleSuccessful) world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    entityX - (1 * Math.sin(radianAngle)), entityY + (entity.getHeight() / 2),
                    entityZ + (1 * Math.cos(radianAngle)),
                    0, world.getRandom().nextDouble() * 0.1,
                    0);
        }
    }

    private static void arrowCritical(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();
        float yaw = (world.getRandom().nextFloat() * 360f) - 180f;
        float pitch = (world.getRandom().nextFloat() * 90f) - 45f;
        double yawRadian = Math.toRadians(yaw), pitchRadian = Math.toRadians(pitch);

        for(float i = -2; i <= 2; i += 0.1f){
            world.addParticle(EffectiveWeaponsParticles.DOUBLE_BOW_CRIT,
                    entityX, entityY + entity.getHeight()/2, entityZ,
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * -Math.sin(yawRadian),
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * Math.sin(pitchRadian),
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * Math.cos(yawRadian));
        }
        for(float i = -1f; i <= 1f; i += 0.1f){
            world.addParticle(EffectiveWeaponsParticles.DOUBLE_BOW_CRIT,
                    entityX, entityY + entity.getHeight()/2, entityZ,
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * -Math.sin(yawRadian),
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * Math.sin(pitchRadian - Math.PI),
                    i * (0.1 + (0.05 * world.getRandom().nextDouble())) * Math.cos(yawRadian));
        }

    }

    private static void fireGuardActivation(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY(), entityZ = entity.getZ();

        for(int i = 0; i <= 36; i ++){
            world.addParticle(ParticleTypes.SMALL_FLAME,
                    entityX, entityY, entityZ,
                    (0.2) * -Math.sin(Math.toRadians((world.getRandom().nextDouble() * 360) - 180)),
                    0,
                    (0.2) * Math.cos(Math.toRadians((world.getRandom().nextDouble() * 360) - 180)));
        }
    }

    private static void burstImpact(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY() + entity.getHeight()/2, entityZ = entity.getZ();

        for (int i = 0; i <= 72; i++) {
            double yawRadian = Math.toRadians((i * 5) - 180);
            world.addParticle(ParticleTypes.CLOUD,
                    entityX, entityY, entityZ,
                    (1) * -Math.sin(yawRadian),
                    0,
                    (1) * Math.cos(yawRadian));
        }
        world.addParticle(ParticleTypes.EXPLOSION_EMITTER, entityX, entityY, entityZ, 0, 0, 0);
    }

    private static void domainOfFire(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY() + entity.getHeight()/2, entityZ = entity.getZ();
        for (int i = 0; i <= 36; i++) {
            double yawRadian = Math.toRadians((i * 10) - 180);
            world.addParticle(ParticleTypes.FLAME,
                    entityX, entityY, entityZ,
                    (0.25) * -Math.sin(yawRadian),
                    0,
                    (0.25) * Math.cos(yawRadian));
        }
    }

    private static void resonance(ClientWorld world, Entity entity){
        double entityX = entity.getX(), entityY = entity.getY() + entity.getHeight()/2, entityZ = entity.getZ();
        for (int i = 0; i <= 36; i++) {
            double yawRadian = Math.toRadians((i * 10) - 180);
            world.addParticle(ParticleTypes.SCRAPE,
                    entityX, entityY, entityZ,
                    (6.33) * -Math.sin(yawRadian),
                    0,
                    (6.33) * Math.cos(yawRadian));
        }
    }
}

//        for(int j = 0; j < 19; j++){  // makes a ball of fire, but doesn't look very good
//            double pitchRadian = Math.toRadians((j * 10) - 90);
//            int particles = (int)(36 * Math.abs(Math.cos(pitchRadian)));
//            for (int i = 0; i <= particles; i++) {
//                double yawRadian = Math.toRadians((i * 360/(double)particles) - 180);
//                world.addParticle(ParticleTypes.FLAME,
//                        entityX, entityY, entityZ,
//                        (0.3) * -Math.sin(yawRadian) * Math.cos(pitchRadian),
//                        (0.3) * -Math.sin(pitchRadian),
//                        (0.3) * Math.cos(yawRadian) * Math.cos(pitchRadian));
//            }
//        }