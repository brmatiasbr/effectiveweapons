package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LightShieldItem extends ShieldItem implements AttunableItem{
    public LightShieldItem(Settings settings, boolean near) {
        super(settings);
        this.near = near;
    }
    private boolean near = false;
    public static final String METER_FIRE_GUARD = "effectiveweapons:meter_fire_guard";
    public static final String METER_LUNGE = "effectiveweapons:meter_lunge";
    public static final String METER_STAGGER = "effectiveweapons:meter_stagger";
    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.isSneaking() && this.hasMeterAbilityReady(user.getStackInHand(hand))){
            String abilityKey = this.getMeterAbility(user.getStackInHand(hand));
            if(abilityKey.equals(METER_FIRE_GUARD)){
                if(!world.isClient()) {
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);

                    for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world,
                            new Vec3d(user.getX(), user.getY(), user.getZ()), 128)){
                        ServerPlayNetworking.send(player, new ParticleRequestPayload(user.getId(), 6));
                    }
                }
                user.addStatusEffect(new StatusEffectInstance(EffectiveWeapons.FIRE_GUARD_REGISTRY_ENTRY, 1800, 0, false, true, true));
                NbtCompound compound = this.getCompoundOrDefault(user.getStackInHand(hand));
                compound.putInt(CURRENT_CHARGE, 0);
                user.getStackInHand(hand).setDamage(1001);
                user.getStackInHand(hand).set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
            }
            else if(abilityKey.equals(METER_LUNGE)){
                if(!world.isClient()) {
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS);
                }
                double power = 2, velocityX, velocityY, velocityZ, horizontalVelocity;
                double pitchRadian = Math.toRadians(user.getPitch()), yawRadian = Math.toRadians(user.getYaw());

                velocityY = Math.max(Math.sin(-pitchRadian) * power/2, 0.2);
                horizontalVelocity = Math.cos(pitchRadian) * power;
                velocityX = Math.sin(-yawRadian) * horizontalVelocity;
                velocityZ = Math.cos(yawRadian) * horizontalVelocity;
                Vec3d velocity = new Vec3d(velocityX, velocityY, velocityZ);

                user.setVelocity(user.getVelocity().add(velocity));
                user.currentExplosionImpactPos = user.getPos().add(0, -20, 0);
                user.setIgnoreFallDamageFromCurrentExplosion(true);

                NbtCompound compound = this.getCompoundOrDefault(user.getStackInHand(hand));
                compound.putInt(CURRENT_CHARGE, 0);
                user.getStackInHand(hand).setDamage(1001);
                user.getStackInHand(hand).set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);
        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.light_shield").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("tooltip.light_shield_cont").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            tooltip.add(Text.translatable(near ? "tooltip.close_shield" : "tooltip.distant_shield").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable(meterAbility.equals(METER_STAGGER) ? "tooltip.auto_meter" : "tooltip.sneak_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.light_shield_summary").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            tooltip.add(Text.translatable(near ? "tooltip.close_shield_summary" : "tooltip.distant_shield_summary").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
    }

    private void buildCustomizationTooltip(List<Text> tooltip, String passive, String meter){
        String passiveTranslationKey = passive.replace("effectiveweapons:", "tooltip.");
        String meterTranslationKey = meter.replace("effectiveweapons:", "tooltip.");

        tooltip.add(Text.translatable(passiveTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable(meterTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    public NbtCompound getCompoundOrDefault(ItemStack stack){
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(component != null){
            return component.copyNbt();
        }

        NbtCompound compound = new NbtCompound();
        compound.putString(EffectiveWeapons.PASSIVE_ABILITY, EffectiveWeapons.PASSIVE_NONE);
        compound.putString(EffectiveWeapons.METER_ABILITY, EffectiveWeapons.METER_NONE);

        compound.putInt(CURRENT_CHARGE, 0);
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
        return EffectiveWeapons.METER_NONE;
    }

    @Override
    public String getItemChargeId() {
        return CURRENT_CHARGE;
    }

    @Override
    public int getDefaultDurabilityDamage() {
        return 0;
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_FEATHERWEIGHT);
        customizations.add(EffectiveWeapons.PASSIVE_STRIDE);
        customizations.add(EffectiveWeapons.PASSIVE_LUCKY);
        customizations.add(METER_FIRE_GUARD);
        customizations.add(METER_LUNGE);
        customizations.add(METER_STAGGER);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_FEATHERWEIGHT);
        customizations.add(EffectiveWeapons.PASSIVE_STRIDE);
        customizations.add(EffectiveWeapons.PASSIVE_LUCKY);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_FIRE_GUARD);
        customizations.add(METER_LUNGE);
        customizations.add(METER_STAGGER);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return AttributeModifiersComponent.DEFAULT;
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
            return compound.getInt(CURRENT_CHARGE) >= 20;
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        String meterAbility = this.getMeterAbility(stack);
        return switch (meterAbility) {
            case METER_FIRE_GUARD -> 0x810202;
            case METER_LUNGE -> 0x4190EB;
            case METER_STAGGER -> 0x929292;
            default -> 0xFFFFFF;
        };
    }
}
