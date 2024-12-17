package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenOwlCourierDepotNameEditorPacket {
    BlockPos pos;

    public OpenOwlCourierDepotNameEditorPacket(BlockPos pos) {
        this.pos = pos;

    }
    public OpenOwlCourierDepotNameEditorPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public static void encode(OpenOwlCourierDepotNameEditorPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
    }

    public static OpenOwlCourierDepotNameEditorPacket decode(FriendlyByteBuf buffer) {
        return new OpenOwlCourierDepotNameEditorPacket(buffer);
    }

    public static void consume(OpenOwlCourierDepotNameEditorPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            if(world.getBlockEntity(packet.pos) instanceof OwlCourierDepotTile depot) {
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                    setScreen(depot);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setScreen(OwlCourierDepotTile depot) {
        Minecraft.getInstance().setScreen(new net.joefoxe.hexerei.screen.OwlCourierDepotNameScreen(depot, Component.translatable("hexerei.owl_courier_depot_name.edit")));

    }
}