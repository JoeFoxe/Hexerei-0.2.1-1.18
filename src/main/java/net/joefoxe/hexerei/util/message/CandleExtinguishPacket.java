package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CandleExtinguishPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CandleExtinguishPacket> CODEC  = StreamCodec.ofMember(CandleExtinguishPacket::encode, CandleExtinguishPacket::new);
    public static final Type<CandleExtinguishPacket> TYPE = new Type<>(HexereiUtil.getResource("candle_extinguish"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;

    public CandleExtinguishPacket(BlockPos pos) {
        this.pos = pos;
    }


    public CandleExtinguishPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public static CandleExtinguishPacket decode(FriendlyByteBuf buffer) {
        return new CandleExtinguishPacket(buffer.readBlockPos());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getBlockEntity(pos) != null){
            BlockEntity blockEntity = player.level().getBlockEntity(pos);
            if(blockEntity instanceof CandleTile candleTile){
                Candle.extinguish(player.level(), pos, player.level().getBlockState(pos), candleTile);
            }
        }
    }
}