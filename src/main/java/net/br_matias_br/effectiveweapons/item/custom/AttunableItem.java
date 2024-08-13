package net.br_matias_br.effectiveweapons.item.custom;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;

public interface AttunableItem {
    public ArrayList<String> getPossibleAttunedCustomizations();
    public ArrayList<String> getPossibleAttunedPassiveCustomizations();
    public ArrayList<String> getPossibleAttunedMeterCustomizations();
    public AttributeModifiersComponent getDefaultAttributeModifiers();
    public NbtCompound getCompoundOrDefault(ItemStack stack);
    public String getDefaultPassiveCustomization();
    public String getDefaultMeterCustomization();
    public String getItemChargeId();
    public int getDefaultDurabilityDamage();
}
