package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class BroomSyncPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncPacket> CODEC  = StreamCodec.ofMember(BroomSyncPacket::encode, BroomSyncPacket::new);
    public static final CustomPacketPayload.Type<BroomSyncPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    CompoundTag tag;

    public BroomSyncPacket(int id, CompoundTag tag) {
        this.sourceId = id;
        this.tag = tag;
    }

    public BroomSyncPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readNbt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(minecraft.level.getEntity(sourceId) instanceof BroomEntity broom) {
            broom.load(tag);
            broom.setChanged();
        }
    }
}