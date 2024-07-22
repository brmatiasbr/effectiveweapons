package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CircletArmorItem extends ArmorItem {
    public CircletArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient() && entity instanceof LivingEntity livingEntity){
            boolean wearing = false;
            Iterable<ItemStack> armorItems = livingEntity.getArmorItems();
            for(ItemStack itemStack : armorItems){
                if (itemStack.isOf(EffectiveWeaponsItems.LAPIS_CIRCLET)) {
                    wearing = true;
                    break;
                }
            }
            if(wearing || selected || livingEntity.getOffHandStack() == stack){
                Collection<StatusEffectInstance> entityStatusEffects = livingEntity.getStatusEffects();
                LinkedList<StatusEffectInstance> blockingEffects = new LinkedList<>();
                if(!entityStatusEffects.isEmpty()) for (StatusEffectInstance statusEffect : entityStatusEffects){
                    if(statusEffect.getEffectType().value().isBeneficial() && statusEffect.getDuration() < 210 && statusEffect.getAmplifier() < 1){
                        blockingEffects.add(statusEffect);
                    }
                }
                if(blockingEffects.isEmpty()){
                    RegistryEntry<StatusEffect> effectType = randomEffect(world);
                    livingEntity.addStatusEffect(new StatusEffectInstance(effectType, 200, 0, false, false, true));
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();

        tooltip.add(Text.translatable("tooltip.lapis_circlet").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.translatable("tooltip.lapis_circlet_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.lapis_circlet_exceptions").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.lapis_circlet_exceptions_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.lapis_circlet_exceptions_prompt").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    private RegistryEntry<StatusEffect> randomEffect(World world){
        int number = world.getRandom().nextInt(14);

        RegistryEntry<StatusEffect> statusEffectType = StatusEffects.ABSORPTION;
        switch (number){
            case 0: statusEffectType = StatusEffects.SPEED;
                break;
            case 1: statusEffectType = StatusEffects.HASTE;
                break;
            case 2: statusEffectType = StatusEffects.STRENGTH;
                break;
            case 3: statusEffectType = StatusEffects.JUMP_BOOST;
                break;
            case 4: statusEffectType = StatusEffects.REGENERATION;
                break;
            case 5: statusEffectType = StatusEffects.RESISTANCE;
                break;
            case 6: statusEffectType = StatusEffects.FIRE_RESISTANCE;
                break;
            case 7: statusEffectType = StatusEffects.WATER_BREATHING;
                break;
            case 8: statusEffectType = StatusEffects.NIGHT_VISION;
                break;
            case 9: // Absorption, already set
                break;
            case 10: statusEffectType = StatusEffects.LUCK;
                break;
            case 11: statusEffectType = StatusEffects.SLOW_FALLING;
                break;
            case 12: statusEffectType = StatusEffects.CONDUIT_POWER;
                break;
            case 13: statusEffectType = StatusEffects.DOLPHINS_GRACE;
                break;
            case 14: statusEffectType = StatusEffects.HERO_OF_THE_VILLAGE;
                break;
        }

        return statusEffectType;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        stack.setDamage(stack.getDamage() == 0 ? 1 : 0);
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x345EC3;
    }
}
