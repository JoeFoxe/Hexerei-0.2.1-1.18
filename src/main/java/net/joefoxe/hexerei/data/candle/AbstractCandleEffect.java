package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

public class AbstractCandleEffect implements CandleEffect {


    public int checkCooldown;

    public ParticleOptions particle;

    public List<ResourceLocation> particleLocation;

    public AbstractCandleEffect(ParticleOptions particleOptions){
        this.particle = particleOptions;
    }
    public AbstractCandleEffect(){
    }

    @Override
    public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
    }

    @Override
    public ParticleOptions getParticleType() {
        return particle;
    }


    public boolean isEmpty() {
        return getLocationName().equals(EMPTY);
    }

}
