package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;

public class DrainCauldronToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, DrainCauldronToServer> CODEC  = StreamCodec.ofMember(DrainCauldronToServer::encode, DrainCauldronToServer::new);
    public static final CustomPacketPayload.Type<DrainCauldronToServer> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("drain_cauldron_server"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos cauldronPos;

    public DrainCauldronToServer(BlockPos cauldronPos) {
        this.cauldronPos = cauldronPos;
    }

    public DrainCauldronToServer(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(cauldronPos);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getBlockEntity(cauldronPos) instanceof MixingCauldronTile mixingCauldronTile) {
            mixingCauldronTile.setFluidStack(FluidStack.EMPTY);
            mixingCauldronTile.setChanged();
        }
    }
}