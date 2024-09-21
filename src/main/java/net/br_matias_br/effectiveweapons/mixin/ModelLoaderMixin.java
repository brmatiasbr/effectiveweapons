package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.item.render.EffectiveWeaponsModels;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void addIronLance(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.loadItemModel(EffectiveWeaponsModels.IRON_LANCE_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.BLESSED_LANCE_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.BLESSED_LANCE_UP_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.DEKAJA_TOME_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.LAPIS_CIRCLET_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.IRON_DAGGER_NORMAL_GRIP_MODEL);
        this.loadItemModel(EffectiveWeaponsModels.ROGUE_DAGGER_NORMAL_GRIP_MODEL);
    }
}
