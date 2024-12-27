package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class CandleEffectParticlePacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CandleEffectParticlePacket> CODEC  = StreamCodec.ofMember(CandleEffectParticlePacket::encode, CandleEffectParticlePacket::new);
    public static final Type<CandleEffectParticlePacket> TYPE = new Type<>(HexereiUtil.getResource("candle_effect_particle"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;

    List<String> particleLocations;

    int livingId;

    int stage;

    public CandleEffectParticlePacket(BlockPos pos, List<String> particleLocations, int livingId, int stage) {
        this.pos = pos;
        this.particleLocations = particleLocations;
        this.livingId = livingId;
        this.stage = stage;
    }

    public CandleEffectParticlePacket(RegistryFriendlyByteBuf buffer) {

        this.pos = buffer.readBlockPos();
        int size = buffer.readInt();
        List<String> list = new ArrayList<>();
        for(int i = 0; i < size; i++){
            list.add(buffer.readUtf());
        }
        this.particleLocations = list;
        this.livingId = buffer.readInt();
        this.stage = buffer.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(particleLocations.size());
        for (String particleLocation : particleLocations)
            buffer.writeUtf(particleLocation);
        buffer.writeInt(livingId);
        buffer.writeInt(stage);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getBlockEntity(pos) != null){
            BlockEntity blockEntity = player.level().getBlockEntity(pos);
            if(blockEntity instanceof CandleTile candleTile){
                if(stage == 0 && player.level().getEntity(livingId) instanceof LivingEntity livingEntity)
                    PotionCandleEffect.spawnParticles(player.level(), particleLocations, livingEntity);
                if(stage == 1)
                    Candle.spawnParticleWave(player.level(), pos, true, particleLocations, 10);
            }
        }
    }
}