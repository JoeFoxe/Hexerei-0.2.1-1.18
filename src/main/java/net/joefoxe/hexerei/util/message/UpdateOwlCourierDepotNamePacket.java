package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateOwlCourierDepotNamePacket {
    GlobalPos pos;
    Component name;

    public UpdateOwlCourierDepotNamePacket(GlobalPos pos, String name) {
        this.pos = pos;
        Component component = Component.literal(name).withStyle(Style.EMPTY.withColor(0xAAAAAA));
        this.name = component;
    }

    public UpdateOwlCourierDepotNamePacket(FriendlyByteBuf buff) {
        this.pos = buff.readGlobalPos();
        this.name = buff.readComponent();
    }

    public static void encode(UpdateOwlCourierDepotNamePacket object, FriendlyByteBuf buffer) {
        buffer.writeGlobalPos(object.pos);
        buffer.writeComponent(object.name);
    }

    public static UpdateOwlCourierDepotNamePacket decode(FriendlyByteBuf buffer) {
        return new UpdateOwlCourierDepotNamePacket(buffer);
    }

    public static void consume(UpdateOwlCourierDepotNamePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            } else {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;
                world = sender.getCommandSenderWorld();
            }


            if(world instanceof ServerLevel server) {
                ServerLevel serverLevel = server.getServer().getLevel(packet.pos.dimension());
                if (serverLevel != null && world.getBlockState(packet.pos.pos()).getBlock() instanceof OwlCourierDepot depot){
                    depot.withTileEntityDo(serverLevel, packet.pos.pos(), te -> {
                        // change message depot to the custom name on the tile
                        te.setCustomName(packet.name);
                        OwlCourierDepotSavedData.get().addOwlCourierDepot(packet.name.getString(), packet.pos);
                        te.sync();
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}