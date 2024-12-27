package net.joefoxe.hexerei.data.candle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CandleEffectParticlePacket;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;

import java.util.Random;

public class SunshineCandleEffect extends AbstractCandleEffect{
    private static final int MAX_TIME = 8 * 20;

    public SunshineCandleEffect(){
    }


    private static int getDuration(RandomSource pSource, int pTime, IntProvider pTimeProvider) {
        return pTime == -1 ? pTimeProvider.sample(pSource) : pTime;
    }


    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
        if(candleData.lit){

            if (candleData.cooldown >= MAX_TIME) {
                if ((level instanceof ServerLevel serverLevel)) {
                    // look up command for setting weather to clear skies
//                    serverLevel.getLevel().setWeatherParameters(MAX_TIME + 1, 0, false, false);
                    serverLevel.getLevel().setWeatherParameters(getDuration(level.getRandom(), -1, ServerLevel.RAIN_DELAY), 0, false, false);
//                    serverLevel.sendParticles(particle, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.2);

                    if(!candleData.effectParticle.isEmpty())
                        HexereiPacketHandler.sendToNearbyClient(serverLevel, blockEntity.getBlockPos(), new CandleEffectParticlePacket(blockEntity.getBlockPos(), candleData.effectParticle, 0, 1));

                }
                candleData.cooldown = 0;
            }
            candleData.cooldown = (candleData.cooldown + 1) % Integer.MAX_VALUE;


            try {
                if (candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && !candleData.effectParticle.isEmpty())
                    particle = ParticleArgument.readParticle(new StringReader(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size()))), level.registryAccess());
            } catch (CommandSyntaxException e) {
                // shrug
            }
        }

    }


    @Override
    public <T> AbstractCandleEffect getCopy() {
        return new SunshineCandleEffect();
    }

    @Override
    public String getLocationName() {
        return HexereiUtil.getResource("sunshine_effect").toString();
    }
}
