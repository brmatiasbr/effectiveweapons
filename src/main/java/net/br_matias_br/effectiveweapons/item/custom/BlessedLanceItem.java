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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BlessedLanceItem extends LanceItem{
    public BlessedLanceItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Collection<StatusEffectInstance> statusEffects = user.getStatusEffects();
        int cooldown = 0;

        if(!statusEffects.isEmpty() && !user.getItemCooldownManager().isCoolingDown(this)){
            cooldown = this.neutralizeEffects(user, world, user.getStackInHand(hand));
        }
        if(cooldown > 0){
            user.getItemCooldownManager().set(this, Math.min(cooldown, 2400));
        }

        return cooldown > 0 ? TypedActionResult.success(user.getStackInHand(hand)) : super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Collection<StatusEffectInstance> statusEffects = entity.getStatusEffects();
        int cooldown = 0;

        if(!statusEffects.isEmpty() && !user.getItemCooldownManager().isCoolingDown(this)){
            cooldown = this.neutralizeEffects(entity, user.getWorld(), stack);
        }
        if(cooldown > 0){
            user.getItemCooldownManager().set(this, Math.min(cooldown, 2400));
        }

        return cooldown > 0 ? ActionResult.success(true) :  super.useOnEntity(stack, user, entity, hand);
    }

    private int neutralizeEffects(LivingEntity entity, World world, ItemStack stack){
        Collection<StatusEffectInstance> statusEffects = entity.getStatusEffects();
        LinkedList<StatusEffectInstance> effectsToClear = new LinkedList<>();
        boolean success = false;
        int highestDuration = 0;
        int cooldown = 0;

        for(StatusEffectInstance statusEffect : statusEffects){
            if(!statusEffect.getEffectType().value().isBeneficial() && statusEffect.getEffectType() != StatusEffects.TRIAL_OMEN){
                effectsToClear.add(statusEffect);
            }
        }
        if(!effectsToClear.isEmpty()){
            success = true;
            for(StatusEffectInstance statusEffect : effectsToClear){
                if (highestDuration < statusEffect.getDuration()) {
                    highestDuration = statusEffect.getDuration();
                }
                entity.removeStatusEffect(statusEffect.getEffectType());
            }
        }

        if(success){
            if(!world.isClient()){
                for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world,
                        new Vec3d(entity.getX(), entity.getY(), entity.getZ()), 128)){
                    ServerPlayNetworking.send(player, new ParticleRequestPayload(entity.getId(), 2));
                }
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.PLAYERS);
            }
            cooldown = (int) (highestDuration * 0.75f);
        }
        return cooldown;
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
            tooltip.add(Text.translatable("tooltip.blessed_lance").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.blessed_lance_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        return target.getType().isIn(EntityTypeTags.SENSITIVE_TO_SMITE) ? 4 : 0;
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
