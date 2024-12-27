package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class TESyncPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, TESyncPacket> CODEC  = StreamCodec.ofMember(TESyncPacket::encode, TESyncPacket::new);
    public static final CustomPacketPayload.Type<TESyncPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("te_sync"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;
    CompoundTag tag;

    public TESyncPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public TESyncPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readNbt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(minecraft.level.getBlockEntity(pos) != null){
            minecraft.level.getBlockEntity(pos).loadWithComponents(tag, minecraft.level.registryAccess());
            minecraft.level.getBlockEntity(pos).setChanged();
        }
    }
}