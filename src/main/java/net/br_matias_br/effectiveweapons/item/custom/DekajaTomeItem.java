package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsEffects;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.entity.custom.DekajaEffectEntity;
import net.br_matias_br.effectiveweapons.entity.custom.LargeAreaNoEffectCloudEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DekajaTomeItem extends Item implements AttunableItem{
    public DekajaTomeItem(Settings settings) {
        super(settings);
    }

    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";
    public static final String PASSIVE_FRIGID = "effectiveweapons:passive_frigid";
    public static final String PASSIVE_POWER_SWING = "effectiveweapons:passive_power_swing";
    public static final String PASSIVE_ISOLATION = "effectiveweapons:passive_isolation";
    public static final String METER_NEUTRALIZING_TERRAIN = "effectiveweapons:meter_neutralizing_terrain";
    public static final String METER_REPULSION = "effectiveweapons:meter_repulsion";
    public static final String METER_GRAVITY = "effectiveweapons:meter_gravity";

    public static final int MAX_CHARGE = 10;
    public static final int MAX_CHARGE_REPULSION = 100;

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i);

            NbtCompound compound = this.getCompoundOrDefault(stack);
            String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
            String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
            boolean frigid = passiveAbility.equals(PASSIVE_FRIGID);
            int currentCharge = compound.getInt(CURRENT_CHARGE);

            if (world instanceof ServerWorld serverWorld) {
                Entity entity;
                if(meterAbility.equals(METER_NEUTRALIZING_TERRAIN) && currentCharge >= MAX_CHARGE){
                    entity = new LargeAreaNoEffectCloudEntity(EffectiveWeaponsEntities.LARGE_AREA_NO_EFFECT_CLOUD_ENTITY_TYPE,
                            user.getWorld(), 300, user, user.getX(), user.getY() - 2.5, user.getZ(), frigid);

                    compound.putInt(LightShieldItem.CURRENT_CHARGE, 0);
                    stack.setDamage(1001);
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                }
                else{
                    boolean gravity = false;
                    if(meterAbility.equals(METER_GRAVITY) && currentCharge >= MAX_CHARGE){
                        gravity = true;
                        compound.putInt(LightShieldItem.CURRENT_CHARGE, 0);
                        stack.setDamage(1001);
                        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                    }
                    DekajaEffectEntity dekajaEffect = new DekajaEffectEntity(EffectiveWeaponsEntities.DEKAJA_EFFECT_ENTITY_TYPE,
                            world, user, user.getX(), user.getEyeY() - 0.4, user.getZ(), stack, frigid, gravity);
                    dekajaEffect.addVelocity(this.getVector(user, 0.75 * f));
                    entity = dekajaEffect;
                }
                serverWorld.spawnEntity(entity);
            }

            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
            );

            playerEntity.getItemCooldownManager().set(this, 200);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(user.getItemCooldownManager().isCoolingDown(this)){
            return TypedActionResult.fail(stack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        if(meterAbility.equals(METER_REPULSION)){
            int charge = compound.getInt(this.getItemChargeId());
            if(charge < MAX_CHARGE_REPULSION){
                charge++;
                stack.setDamage(1001 - (charge * 10));
                compound.putInt(this.getItemChargeId(), charge);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
            }
        }
        if(passiveAbility.equals(PASSIVE_ISOLATION) && entity.isSneaking() && selected && (entity instanceof LivingEntity livingEntity)){
            livingEntity.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.ISOLATED_REGISTRY_ENTRY, 1, 0, false, false, true));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if(miner.isCreative()) return false;
        return super.canMine(state, world, pos, miner);
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        if (f > 3.0F) {
            f = 3.0F;
        }

        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    protected Vec3d getVector(Entity entity, double velocity){
        double vectorX, vectorY, vectorZ, horizontalComponents;
        double pitchRadian = Math.toRadians(entity.getPitch()), yawRadian = Math.toRadians(entity.getYaw());
        vectorY = velocity * -Math.sin(pitchRadian);
        horizontalComponents = velocity * Math.cos(pitchRadian);
        vectorX = horizontalComponents * -Math.sin(yawRadian);
        vectorZ = horizontalComponents * Math.cos(yawRadian);

        return new Vec3d(vectorX, vectorY, vectorZ);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        int color = 0xFFFFFF, damage = stack.getDamage(),  i = stack.getMaxDamage();
        float f = Math.max(0.0F, ((float)i - (float)damage) / (float)i);
        switch (compound.getString(EffectiveWeapons.METER_ABILITY)){
            case METER_REPULSION -> {
                if(damage > 501){
                    color = 0x808787;
                }
                else if(damage > 1){
                    color = 0xB1BABA;
                }
                else {
                    color = 0xB7E8E8;
                }
            }
            case METER_NEUTRALIZING_TERRAIN -> color = EffectiveWeapons.getColorFromGradient(0xA1A1A1, 0x3F4040, f);
            case METER_GRAVITY -> color = EffectiveWeapons.getColorFromGradient(0xF7650A, 0xFCF226, f);
            default -> super.getItemBarColor(stack);
        }
        return color;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean onCooldown = false;
        float cooldown = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean controlHeld = Screen.hasControlDown();
        boolean shiftHeld = Screen.hasShiftDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);
        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        if(player != null){
            onCooldown = player.getItemCooldownManager().isCoolingDown(this);
            cooldown = player.getItemCooldownManager().getCooldownProgress(this, 0);
        }

        NumberFormat formatter = new DecimalFormat("#0");

        if(controlHeld){
            if (shiftHeld) {
                tooltip.add(Text.translatable("tooltip.dekaja_tome").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.dekaja_tome_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.dekaja_tome_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.dekaja_tome_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
                tooltip.add(Text.translatable("tooltip.dekaja_tome_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                if (!meterAbility.equals(EffectiveWeapons.METER_NONE))
                    tooltip.add(Text.translatable(meterAbility.equals(METER_REPULSION) ? "tooltip.swing_any_meter" : "tooltip.auto_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
                tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            } else {
                tooltip.add(Text.translatable("tooltip.dekaja_tome_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            }
        }
        else{
            tooltip.add(Text.translatable("tooltip.show_weapon_summary").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.timeUntilRegen = 0;
        return super.postHit(stack, target, attacker);
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(PASSIVE_FRIGID);
        customizations.add(PASSIVE_POWER_SWING);
        customizations.add(PASSIVE_ISOLATION);
        customizations.add(METER_NEUTRALIZING_TERRAIN);
        customizations.add(METER_REPULSION);
        customizations.add(METER_GRAVITY);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(PASSIVE_FRIGID);
        customizations.add(PASSIVE_POWER_SWING);
        customizations.add(PASSIVE_ISOLATION);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_NEUTRALIZING_TERRAIN);
        customizations.add(METER_REPULSION);
        customizations.add(METER_GRAVITY);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return AttributeModifiersComponent.DEFAULT;
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
}
