package net.br_matias_br.effectiveweapons.item.render;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsModels {
    public static final ModelIdentifier IRON_LANCE_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "iron_lance_held"));
    public static final ModelIdentifier BLESSED_LANCE_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "blessed_lance_held"));
    public static final ModelIdentifier BLESSED_LANCE_UP_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "blessed_lance_held_up"));
    public static final ModelIdentifier DEKAJA_TOME_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "dekaja_tome_held"));
    public static final ModelIdentifier LAPIS_CIRCLET_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "lapis_circlet_held"));
    public static final ModelIdentifier IRON_DAGGER_NORMAL_GRIP_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "iron_dagger_normal_grip"));
    public static final ModelIdentifier ROGUE_DAGGER_NORMAL_GRIP_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of(EffectiveWeapons.MOD_ID, "rogue_dagger_normal_grip"));

    public static boolean notItemGUIorFrame(ModelTransformationMode renderMode){
        return (renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND
                && renderMode != ModelTransformationMode.FIXED);
    }
}
