package net.br_matias_br.effectiveweapons.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ItemModificationPayload(String componentKey, boolean revertToDefault) implements CustomPayload {
    public static final CustomPayload.Id<ItemModificationPayload> ID = new CustomPayload.Id<>(EffectiveWeaponsNetworking.ITEM_MODIFICATION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ItemModificationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ItemModificationPayload::componentKey,
            PacketCodecs.BOOL, ItemModificationPayload::revertToDefault,
            ItemModificationPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
