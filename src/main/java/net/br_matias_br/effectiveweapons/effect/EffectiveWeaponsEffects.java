package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsEffects {
    public static void registerEffects() {
        EffectiveWeapons.LOGGER.info("Registering effects for " + EffectiveWeapons.MOD_ID);
    }
    public static final RegistryEntry<StatusEffect> FIRE_GUARD_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard"), new FireGuardEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_BURNING_TIME, Identifier.of(EffectiveWeapons.MOD_ID ,"effect.fire_guard"), -0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final RegistryEntry<StatusEffect> ELEVATED_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "elevated"), new ElevatedEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(EffectiveWeapons.MOD_ID ,"effect.elevated"), 0.65f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final RegistryEntry<StatusEffect> COUNTER_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "counter"), new CounterEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Identifier.of(EffectiveWeapons.MOD_ID ,"effect.counter"), 0.3f, EntityAttributeModifier.Operation.ADD_VALUE));
    public static final RegistryEntry<StatusEffect> REMOTE_COUNTER_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "remote_counter"), new RemoteCounterEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Identifier.of(EffectiveWeapons.MOD_ID ,"effect.remote_counter"), 0.3f, EntityAttributeModifier.Operation.ADD_VALUE));
    public static final RegistryEntry<StatusEffect> ISOLATED_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "isolated"), new IsolatedEffect());
}
