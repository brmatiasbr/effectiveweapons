package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.material.EffectiveWeaponMaterial;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SpiralingSwordItem extends SwordItem implements AttunableItem{
    public static final float MAX_CHARGE = 200;
    public static final float MAX_DEATH_CHARGE = 100;
    public static final String METER_SPIRAL = "effectiveweapons:meter_spiral";
    public static final String METER_ALLIED_SPIRAL = "effectiveweapons:meter_allied_spiral";
    public static final String METER_CHAOTIC_SPIRAL = "effectiveweapons:meter_chaotic_spiral";
    public static final String PASSIVE_DEATH_CHARGE = "effectiveweapons:passive_death_charge";
    private static final String SPIRALING_SWORD_CHARGE = "effectiveweapons:spiraling_sword_charge";
    public SpiralingSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean returnThingy = super.postHit(stack, target, attacker);

        NbtCompound compound = getCompoundOrDefault(stack);
        float charge = compound.getFloat(SPIRALING_SWORD_CHARGE);
        String meterAbility = this.getMeterAbility(stack);
        String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);

        if(!target.isAlive() && passiveAbility.equals(PASSIVE_DEATH_CHARGE)){
            charge += target.getMaxHealth() * 0.5f;
            if(charge > 100) charge = 100;

            if(charge >= MAX_DEATH_CHARGE && !meterAbility.equals(METER_ALLIED_SPIRAL)) {
                charge = 0f;
                if(!attacker.getWorld().isClient()){
                    attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS);
                }

                extendEffects(attacker, meterAbility.equals(METER_CHAOTIC_SPIRAL));
            }

            compound.putFloat(SPIRALING_SWORD_CHARGE, charge);
            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);
            stack.setDamage(1001 - (int)(charge * 10));
        }

        return returnThingy;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        if(this.getMeterAbility(stack).equals(METER_ALLIED_SPIRAL) && this.hasMeterAbilityReady(stack)){
            compound.putFloat(SPIRALING_SWORD_CHARGE, 0);
            NbtComponent component = NbtComponent.of(compound);
            user.getStackInHand(hand).set(DataComponentTypes.CUSTOM_DATA, component);
            user.getStackInHand(hand).setDamage(1001);

            extendEffects(entity, false);
            return ActionResult.SUCCESS;
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    public static void extendEffects(LivingEntity entity, boolean chaotic){
        if(!entity.getWorld().isClient()){
            for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) entity.getWorld(),
                    new Vec3d(entity.getX(), entity.getY(), entity.getZ()), 128)) {
                ServerPlayNetworking.send(player, new ParticleRequestPayload(entity.getId(), 1));
            }
        }

        Collection<StatusEffectInstance> effects = entity.getStatusEffects();

        LinkedList<StatusEffectInstance> extendedEffects = new LinkedList<>();
        if(!effects.isEmpty()){
            for (StatusEffectInstance statusEffect : effects) {
                int duration = statusEffect.getDuration(), amplifier = statusEffect.getAmplifier(), newDuration;
                if(chaotic) {
                    float randomFactor = 1.2f + (entity.getWorld().getRandom().nextFloat() * 0.6f);
                    newDuration = (int) Math.min(Math.max(1200, duration * randomFactor), 6000);
                }
                else newDuration = (int) Math.min(Math.max(1200, duration * 1.5f), 6000);
                if (duration > 6000) newDuration = duration;

                extendedEffects.add(new StatusEffectInstance(statusEffect.getEffectType(), newDuration, amplifier));
            }
            entity.clearStatusEffects();
            for(StatusEffectInstance statusEffect : extendedEffects){
                entity.addStatusEffect(statusEffect);
            }
        }
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return false;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int i = stack.getMaxDamage();
        String meterAbility = this.getMeterAbility(stack);
        float f = Math.max(0.0F, ((float)i - (float)stack.getDamage()) / (float)i);
        int color1Hex = 0x54398A, color2Hex = 0xB38EF3;
        if(meterAbility.equals(METER_ALLIED_SPIRAL)){
            color1Hex = 0x39688A;
            color2Hex = 0x8EC0f3;
        }
        return EffectiveWeapons.getColorFromGradient(color1Hex, color2Hex, f);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);

        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        boolean deathCharge = passiveAbility.equals(PASSIVE_DEATH_CHARGE);

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.spiraling_sword").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable(deathCharge ? "tooltip.spiraling_sword_cont_death" : "tooltip.spiraling_sword_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable(deathCharge ? "tooltip.spiraling_sword_charge_death" : "tooltip.spiraling_sword_charge").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable(deathCharge ? "tooltip.spiraling_sword_charge_cont_death" : "tooltip.spiraling_sword_charge_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable(meterAbility.equals(METER_ALLIED_SPIRAL) ? "tooltip.entity_meter" : "tooltip.auto_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.spiraling_sword_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    private void buildCustomizationTooltip(List<Text> tooltip, String passive, String meter){
        String passiveTranslationKey = passive.replace("effectiveweapons:", "tooltip.");
        String meterTranslationKey = meter.replace("effectiveweapons:", "tooltip.");

        tooltip.add(Text.translatable(passiveTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable(meterTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_SWEEP);
        customizations.add(EffectiveWeapons.PASSIVE_STURDY);
        customizations.add(PASSIVE_DEATH_CHARGE);
        customizations.add(METER_SPIRAL);
        customizations.add(METER_ALLIED_SPIRAL);
        customizations.add(METER_CHAOTIC_SPIRAL);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_SWEEP);
        customizations.add(EffectiveWeapons.PASSIVE_STURDY);
        customizations.add(PASSIVE_DEATH_CHARGE);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_SPIRAL);
        customizations.add(METER_ALLIED_SPIRAL);
        customizations.add(METER_CHAOTIC_SPIRAL);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return SwordItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 5, -2.4f);
    }

    public NbtCompound getCompoundOrDefault(ItemStack stack){
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(component != null){
            return component.copyNbt();
        }

        NbtCompound compound = new NbtCompound();
        compound.putFloat(SPIRALING_SWORD_CHARGE, 0f);
        compound.putString(EffectiveWeapons.PASSIVE_ABILITY, EffectiveWeapons.PASSIVE_NONE);
        compound.putString(EffectiveWeapons.METER_ABILITY, METER_SPIRAL);

        NbtComponent nextComponent = NbtComponent.of(compound);
        stack.set(DataComponentTypes.CUSTOM_DATA, nextComponent);

        return compound;
    }

    @Override
    public String getDefaultPassiveCustomization() {
        return EffectiveWeapons.PASSIVE_NONE;
    }

    @Override
    public String getDefaultMeterCustomization() {
        return METER_SPIRAL;
    }

    @Override
    public String getItemChargeId() {
        return SPIRALING_SWORD_CHARGE;
    }

    @Override
    public int getDefaultDurabilityDamage() {
        return 1001;
    }

    public boolean hasMeterAbility(ItemStack stack){
        NbtCompound compound = this.getCompoundOrDefault(stack);
        return !(compound.getString(EffectiveWeapons.METER_ABILITY).equals(EffectiveWeapons.METER_NONE));
    }

    public String getMeterAbility(ItemStack stack){
        if (this.hasMeterAbility(stack)) {
            NbtCompound compound = this.getCompoundOrDefault(stack);
            return compound.getString(EffectiveWeapons.METER_ABILITY);
        }
        return EffectiveWeapons.METER_NONE;
    }

    public boolean hasMeterAbilityReady(ItemStack stack){
        if(!this.hasMeterAbility(stack)){
            return false;
        }
        else{
            NbtCompound compound = this.getCompoundOrDefault(stack);
            return compound.getInt(SPIRALING_SWORD_CHARGE) >= MAX_CHARGE;
        }
    }
}
