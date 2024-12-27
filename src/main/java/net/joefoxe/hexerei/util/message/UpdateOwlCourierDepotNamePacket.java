package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class UpdateOwlCourierDepotNamePacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateOwlCourierDepotNamePacket> CODEC  = StreamCodec.ofMember(UpdateOwlCourierDepotNamePacket::encode, UpdateOwlCourierDepotNamePacket::new);
    public static final CustomPacketPayload.Type<UpdateOwlCourierDepotNamePacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier_depot_name_update"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    GlobalPos pos;
    String name;

    public UpdateOwlCourierDepotNamePacket(GlobalPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public UpdateOwlCourierDepotNamePacket(FriendlyByteBuf buff) {
        this.pos = buff.readGlobalPos();
        this.name = buff.readUtf();
    }

    public static void encode(UpdateOwlCourierDepotNamePacket object, FriendlyByteBuf buffer) {
        buffer.writeGlobalPos(object.pos);
        buffer.writeUtf(object.name);
    }

    public static UpdateOwlCourierDepotNamePacket decode(FriendlyByteBuf buffer) {
        return new UpdateOwlCourierDepotNamePacket(buffer);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        ServerLevel serverLevel = server.getLevel(pos.dimension());
        if (serverLevel != null && serverLevel.getBlockState(pos.pos()).getBlock() instanceof OwlCourierDepot depot){
            depot.withTileEntityDo(serverLevel, pos.pos(), te -> {
                // change message depot to the custom name on the tile
                te.name = Component.literal(name).withStyle(Style.EMPTY.withColor(0xAAAAAA));
                OwlCourierDepotSavedData.get().addOwlCourierDepot(name, pos);
                te.sync();
            });
        }
    }
}