package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.render.EffectiveWeaponsModels;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useCustomModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode,
                                        boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(stack.isOf(EffectiveWeaponsItems.IRON_LANCE) && EffectiveWeaponsModels.notItemGUIorFrame(renderMode)) {
            return ((ItemRendererAccessor)this).effweap$getModels().getModelManager().getModel(EffectiveWeaponsModels.IRON_LANCE_MODEL);
        }
        if(stack.isOf(EffectiveWeaponsItems.BLESSED_LANCE) && EffectiveWeaponsModels.notItemGUIorFrame(renderMode)) {
            return ((ItemRendererAccessor)this).effweap$getModels().getModelManager().getModel(EffectiveWeaponsModels.BLESSED_LANCE_MODEL);
        }
        if(stack.isOf(EffectiveWeaponsItems.DEKAJA_TOME) && EffectiveWeaponsModels.notItemGUIorFrame(renderMode)) {
            return ((ItemRendererAccessor)this).effweap$getModels().getModelManager().getModel(EffectiveWeaponsModels.DEKAJA_TOME_MODEL);
        }
        if(stack.isOf(EffectiveWeaponsItems.LAPIS_CIRCLET) && EffectiveWeaponsModels.notItemGUIorFrame(renderMode)) {
            return ((ItemRendererAccessor)this).effweap$getModels().getModelManager().getModel(EffectiveWeaponsModels.LAPIS_CIRCLET_MODEL);
        }
        return value;
    }
}