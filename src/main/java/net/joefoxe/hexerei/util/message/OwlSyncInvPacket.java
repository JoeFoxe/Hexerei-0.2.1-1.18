package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class OwlSyncInvPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, OwlSyncInvPacket> CODEC  = StreamCodec.ofMember(OwlSyncInvPacket::encode, OwlSyncInvPacket::new);
    public static final Type<OwlSyncInvPacket> TYPE = new Type<>(HexereiUtil.getResource("owl_sync_inv"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    CompoundTag tag;

    public OwlSyncInvPacket(Entity entity, CompoundTag tag) {
        this.sourceId = entity.getId();
        this.tag = tag;
    }
    public OwlSyncInvPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.tag = buf.readNbt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(player.level().getEntity(sourceId) instanceof OwlEntity owl) {
            owl.itemHandler.deserializeNBT(minecraft.level.registryAccess(), tag);
        }
    }
}