package net.br_matias_br.effectiveweapons.entity;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.custom.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsEntities {
    public static void registerEntities() {
        EffectiveWeapons.LOGGER.info("Registering Entities for " + EffectiveWeapons.MOD_ID);
    }
    public static final EntityType<DekajaEffectEntity> DEKAJA_EFFECT_ENTITY_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(EffectiveWeapons.MOD_ID, "dekaja_effect_entity_type"),
        EntityType.Builder.<DekajaEffectEntity>create(DekajaEffectEntity::new, SpawnGroup.MISC).dimensions(0.5f, 0.5f)
                .makeFireImmune()
                .build()
    );

    public static final EntityType<AreaNoEffectCloudEntity> AREA_NO_EFFECT_CLOUD_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(EffectiveWeapons.MOD_ID, "area_no_effect_cloud_entity_type"),
            EntityType.Builder.<AreaNoEffectCloudEntity>create(AreaNoEffectCloudEntity::new, SpawnGroup.MISC).dimensions(5f, 1/4f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<FixedDamageArrowEntity> FIXED_DAMAGE_ARROW_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(EffectiveWeapons.MOD_ID, "fixed_damage_arrow_entity"),
            EntityType.Builder.<FixedDamageArrowEntity>create(FixedDamageArrowEntity::new, SpawnGroup.MISC).dimensions(0.5f, 0.5f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<LargeAreaNoEffectCloudEntity> LARGE_AREA_NO_EFFECT_CLOUD_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(EffectiveWeapons.MOD_ID, "large_area_no_effect_cloud_entity_type"),
            EntityType.Builder.<LargeAreaNoEffectCloudEntity>create(LargeAreaNoEffectCloudEntity::new, SpawnGroup.MISC).dimensions(25f, 5f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<BladeBeamEntity> BLADE_BEAM_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(EffectiveWeapons.MOD_ID, "blade_beam_entity_type"),
            EntityType.Builder.<BladeBeamEntity>create(BladeBeamEntity::new, SpawnGroup.MISC).dimensions(2f, 0.5f)
                    .makeFireImmune()
                    .build()
    );
}
