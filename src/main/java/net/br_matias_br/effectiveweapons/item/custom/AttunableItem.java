package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public interface AttunableItem {
    ArrayList<String> getPossibleAttunedCustomizations();
    ArrayList<String> getPossibleAttunedPassiveCustomizations();
    ArrayList<String> getPossibleAttunedMeterCustomizations();
    AttributeModifiersComponent getDefaultAttributeModifiers();
    String getDefaultPassiveCustomization();
    String getDefaultMeterCustomization();
    String getItemChargeId();
    int getDefaultDurabilityDamage();

    default NbtCompound getCompoundOrDefault(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(component != null){
            return component.copyNbt();
        }

        NbtCompound compound = new NbtCompound();
        compound.putString(EffectiveWeapons.PASSIVE_ABILITY, EffectiveWeapons.PASSIVE_NONE);
        compound.putString(EffectiveWeapons.METER_ABILITY, EffectiveWeapons.METER_NONE);

        compound.putInt(this.getItemChargeId(), 0);
        NbtComponent nextComponent = NbtComponent.of(compound);
        stack.set(DataComponentTypes.CUSTOM_DATA, nextComponent);

        return compound;
    }

    default void buildCustomizationTooltip(List<Text> tooltip, String passive, String meter){
        String passiveTranslationKey = passive.replace("effectiveweapons:", "tooltip.");
        String meterTranslationKey = meter.replace("effectiveweapons:", "tooltip.");

        tooltip.add(Text.translatable(passiveTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable(meterTranslationKey).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
    }

    default boolean canChargeByHit(){
        return false;
    }
}
