package net.br_matias_br.effectiveweapons.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SwingActionPayload(int entityId, int swingActionType) implements CustomPayload {
    public static final Id<SwingActionPayload> ID = new Id<>(EffectiveWeaponsNetworking.SWING_ACTION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SwingActionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SwingActionPayload::entityId,
            PacketCodecs.INTEGER, SwingActionPayload::swingActionType,
            SwingActionPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
