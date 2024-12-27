package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.DowsingRodItem;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DowsingRodUpdatePositionPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, DowsingRodUpdatePositionPacket> CODEC  = StreamCodec.ofMember(DowsingRodUpdatePositionPacket::encode, DowsingRodUpdatePositionPacket::new);
    public static final CustomPacketPayload.Type<DowsingRodUpdatePositionPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("dowsing_rod_update"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    ItemStack itemStack;
    BlockPos blockPos;
    Boolean swampMode;

    public DowsingRodUpdatePositionPacket(ItemStack itemStack, BlockPos blockPos, Boolean swampMode) {
        this.itemStack = itemStack;
        this.blockPos = blockPos;
        this.swampMode = swampMode;
    }
    public DowsingRodUpdatePositionPacket(RegistryFriendlyByteBuf buf) {
        this.itemStack = ItemStack.STREAM_CODEC.decode(buf);
        this.blockPos = buf.readBlockPos();
        this.swampMode = buf.readBoolean();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        ItemStack.STREAM_CODEC.encode(buffer, itemStack);
        buffer.writeBlockPos(blockPos);
        buffer.writeBoolean(swampMode);

    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ((DowsingRodItem)itemStack.getItem()).nearestPos = blockPos;
        ((DowsingRodItem)itemStack.getItem()).swampMode = swampMode;
    }
}