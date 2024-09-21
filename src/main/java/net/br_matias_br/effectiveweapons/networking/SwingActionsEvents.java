package net.br_matias_br.effectiveweapons.networking;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.entity.custom.BladeBeamEntity;
import net.br_matias_br.effectiveweapons.item.custom.DekajaTomeItem;
import net.br_matias_br.effectiveweapons.item.custom.RogueDaggerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Collection;

public class SwingActionsEvents {
    public static void swingAction(ServerWorld world, int eventType, PlayerEntity player){
        switch (eventType) {
            case 1:
                bladeBeamShoot(world, player);
                break;
            case 2:
                windChargeShoot(world, player);
                break;
            default:
                return;
        }

    }

    public static void bladeBeamShoot(World world, LivingEntity entity){
        ItemStack stack = entity.getMainHandStack();
        RogueDaggerItem rogueDaggerItem = ((RogueDaggerItem) stack.getItem());
        NbtCompound compound = rogueDaggerItem.getCompoundOrDefault(stack);
        int charge = compound.getInt(rogueDaggerItem.getItemChargeId());
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        int effectNumber = 0;
        Collection<StatusEffectInstance> userEffects = entity.getStatusEffects();
        if(!userEffects.isEmpty()) for(StatusEffectInstance statusEffect : userEffects){
            if(statusEffect.getEffectType().value().isBeneficial()){
                effectNumber++;
            }
        }

        BladeBeamEntity bladeBeam = new BladeBeamEntity(EffectiveWeaponsEntities.BLADE_BEAM_ENTITY_TYPE, world,
                entity, entity.getX(), entity.getY() + entity.getHeight()/2, entity.getZ(), effectNumber);
        double velocity = 1 * (entity.getHealth() / entity.getMaxHealth());
        double verticalVelocity = velocity * -Math.sin(Math.toRadians(entity.getPitch()));
        double horizontalVelocity = velocity * Math.cos(Math.toRadians(entity.getPitch()));

        bladeBeam.setVelocity(horizontalVelocity * -Math.sin(Math.toRadians(entity.getYaw())),
                              verticalVelocity,
                              horizontalVelocity * Math.cos(Math.toRadians(entity.getYaw())));
        world.spawnEntity(bladeBeam);

        charge -= 20;
        stack.setDamage(1001 - (charge * 10));
        compound.putInt(rogueDaggerItem.getItemChargeId(), charge);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
    }

    public static void windChargeShoot(World world, PlayerEntity player){
        ItemStack stack = player.getMainHandStack();
        DekajaTomeItem dekajaTomeItem = ((DekajaTomeItem) stack.getItem());
        NbtCompound compound = dekajaTomeItem.getCompoundOrDefault(stack);
        int charge = compound.getInt(dekajaTomeItem.getItemChargeId());

        if (!world.isClient()) {
            WindChargeEntity windChargeEntity = new WindChargeEntity(player, world, player.getPos().getX(), player.getEyePos().getY(), player.getPos().getZ());
            windChargeEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(windChargeEntity);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        charge -= DekajaTomeItem.MAX_CHARGE_REPULSION/2;
        stack.setDamage(1001 - (charge * 10));
        compound.putInt(dekajaTomeItem.getItemChargeId(), charge);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
    }
}
