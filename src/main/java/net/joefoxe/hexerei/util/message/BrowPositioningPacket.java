package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BrowPositioningPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BrowPositioningPacket> CODEC  = StreamCodec.ofMember(BrowPositioningPacket::encode, BrowPositioningPacket::new);
    public static final Type<BrowPositioningPacket> TYPE = new Type<>(HexereiUtil.getResource("brow_positioning"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    OwlEntity.BrowPositioning browPositioning;

    public BrowPositioningPacket(Entity entity, OwlEntity.BrowPositioning browPositioning) {
        this.sourceId = entity.getId();
        this.browPositioning = browPositioning;

    }
    public BrowPositioningPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.browPositioning = buf.readEnum(OwlEntity.BrowPositioning.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeEnum(browPositioning);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
            owl.setBrowPos(browPositioning);
        }
    }
}