package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ClientboundOwlCourierDepotDataInventoryPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOwlCourierDepotDataInventoryPacket> CODEC  = StreamCodec.ofMember(ClientboundOwlCourierDepotDataInventoryPacket::encode, ClientboundOwlCourierDepotDataInventoryPacket::new);
    public static final CustomPacketPayload.Type<ClientboundOwlCourierDepotDataInventoryPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier_depot_inv_clientbound"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    CompoundTag tag;

    public CompoundTag getTag() {
        return tag;
    }

    public ClientboundOwlCourierDepotDataInventoryPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ClientboundOwlCourierDepotDataInventoryPacket(RegistryFriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientOwlCourierDepotData.update(this);
    }
}