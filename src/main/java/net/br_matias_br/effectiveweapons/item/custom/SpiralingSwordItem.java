package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SpiralingSwordItem extends SwordItem {
    private static float MAX_CHARGE = 100;
    public SpiralingSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean returnThingy = super.postHit(stack, target, attacker);

        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(data != null){

            NbtCompound compound = data.copyNbt();
            float charge = compound.getFloat("effectiveweapons:spiraling_sword_charge");

            if(!target.isAlive()){
                charge += target.getMaxHealth() * 0.4f;

                if(charge > MAX_CHARGE) {
                    charge = 0f;
                    if(!attacker.getWorld().isClient()){
                        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS);
                    }

                    for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) attacker.getWorld(),
                            new Vec3d(attacker.getX(), attacker.getY(), attacker.getZ()), 128)){
                        ServerPlayNetworking.send(player, new ParticleRequestPayload(attacker.getId(), 1));
                    }

                    Collection<StatusEffectInstance> effects = attacker.getStatusEffects();

                    LinkedList<StatusEffectInstance> extendedEffects = new LinkedList<>();
                    if(!effects.isEmpty()){
                        for (StatusEffectInstance statusEffect : effects) {
                            int duration = statusEffect.getDuration();
                            int amplifier = statusEffect.getAmplifier();
                            int newDuration = (int) Math.min(Math.max(1200, duration * 1.5f), 6000);
                            if (duration > 6000) newDuration = duration;

                            extendedEffects.add(new StatusEffectInstance(statusEffect.getEffectType(), newDuration, amplifier));
                        }
                        attacker.clearStatusEffects();
                        for(StatusEffectInstance statusEffect : extendedEffects){
                            attacker.addStatusEffect(statusEffect);
                        }
                    }
                }

                compound.putFloat("effectiveweapons:spiraling_sword_charge", charge);
                NbtComponent component = NbtComponent.of(compound);
                stack.set(DataComponentTypes.CUSTOM_DATA, component);
            }
            stack.setDamage(1000 - (int)(charge * 10));
        }
        else{
            NbtCompound compound = new NbtCompound();
            compound.putFloat("effectiveweapons:spiraling_sword_charge", 0f);
            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);
        }

        return returnThingy;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int i = stack.getMaxDamage();
        float f = Math.max(0.0F, ((float)i - (float)stack.getDamage()) / (float)i);
        return EffectiveWeapons.getColorFromGradient(0x54398A, 0xB38EF3, f);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        float charge = 0;
        boolean controlHeld = Screen.hasControlDown();

        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(data != null){
            NbtCompound compound = data.copyNbt();
            charge = compound.getFloat("effectiveweapons:spiraling_sword_charge");
        }
        else{
            NbtCompound compound = new NbtCompound();
            compound.putFloat("effectiveweapons:spiraling_sword_charge", 0f);
            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);
        }

        float currentChargePercent = (charge/MAX_CHARGE) * 100;  //☐■
        String chargeBar = "";
        for(float i = 10; i <= currentChargePercent; i += 10){
            chargeBar = chargeBar.concat("■");
        }
        for(int j = chargeBar.length(); j < 10; j++){
            chargeBar = chargeBar.concat("☐");
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.spiraling_sword").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.spiraling_sword_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.spiraling_sword_charge").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.spiraling_sword_charge_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.spiraling_sword_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal(chargeBar + " " + formatter.format(currentChargePercent) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
