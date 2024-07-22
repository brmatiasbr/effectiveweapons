package net.br_matias_br.effectiveweapons.mixin;

import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntityModel.class)
public interface PlayerEntityModelAccessor {
    @Accessor("thinArms")
    boolean effweap$getThinArms();
    }
