package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RogueDaggerItem extends DaggerItem{
    public RogueDaggerItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float yawDifference = Math.abs(target.getHeadYaw() - attacker.getYaw());
        boolean positiveSteal = false;
        boolean success = false;
        boolean canSteal = true;
        int highestDuration = 0;
        if(yawDifference > 180) yawDifference = 360 - yawDifference;

        if(yawDifference < 40 || (yawDifference < 95 && attacker.isSneaking())){
            positiveSteal = true;
        }

        Collection<StatusEffectInstance> effectsToSteal = target.getStatusEffects();
        LinkedList<StatusEffectInstance> myBagAfterIStealThoseStatusEffects = new LinkedList<>();

        if(attacker instanceof PlayerEntity player){
            if(player.getItemCooldownManager().isCoolingDown(this)) canSteal = false;
        }

        if(!effectsToSteal.isEmpty() && canSteal) for(StatusEffectInstance statusEffect: effectsToSteal){
            if(!statusEffect.getEffectType().value().isBeneficial()){
                if(!positiveSteal) myBagAfterIStealThoseStatusEffects.add(statusEffect);
            }
            else myBagAfterIStealThoseStatusEffects.add(statusEffect);
        }
        if(!myBagAfterIStealThoseStatusEffects.isEmpty()) {
            success = true;

            for (StatusEffectInstance stolenStatusEffect : myBagAfterIStealThoseStatusEffects) {
                if(highestDuration < stolenStatusEffect.getDuration()) highestDuration = stolenStatusEffect.getDuration();
                attacker.addStatusEffect(new StatusEffectInstance(stolenStatusEffect.getEffectType(), stolenStatusEffect.getDuration(), stolenStatusEffect.getAmplifier()));

                if (target.hasStatusEffect(stolenStatusEffect.getEffectType())) {
                    target.removeStatusEffect(stolenStatusEffect.getEffectType());
                }
            }
        }

        if(success) {
            for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) attacker.getWorld(),
                    new Vec3d(target.getX(), target.getY(), target.getZ()), 128)){
                ServerPlayNetworking.send(player, new ParticleRequestPayload(target.getId(), 3));
            }
            if(attacker instanceof PlayerEntity player){
                int cooldown = (int) (highestDuration * 0.75f);
                player.getItemCooldownManager().set(this, Math.min(cooldown, 3600));
            }
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        if(damageSource.getSource() instanceof LivingEntity attacker){
            float yawDifference = Math.abs(target.getHeadYaw() - attacker.getYaw());
            if(yawDifference < 40) return 2f;
        }
        return 0f;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean onCooldown = false;
        float cooldown = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean controlHeld = Screen.hasControlDown();

        if(player != null){
            onCooldown = player.getItemCooldownManager().isCoolingDown(this);
            cooldown = player.getItemCooldownManager().getCooldownProgress(this, 0);
        }

        NumberFormat formatter = new DecimalFormat("#0");

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.rogue_dagger").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.rogue_dagger_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.rogue_dagger_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.rogue_dagger_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("tooltip.rogue_dagger_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        else{
            tooltip.add(Text.translatable("tooltip.rogue_dagger_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(stack.getDamage() != 0){
            stack.setDamage(0);
        }
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if(stack.getDamage() != 0){
            stack.setDamage(0);
        }
        return false;
    }
}
