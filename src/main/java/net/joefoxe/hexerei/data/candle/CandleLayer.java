package net.joefoxe.hexerei.data.candle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CandleLayer {
    public float meltingSpeedMultiplier;
    public float radiusMultiplier;
    public float effectAmplifierMultiplier;
    public float effectCooldownMultiplier;
    public float effectDurationMultiplier;
    public boolean layerFromBlockLocation;
    public ResourceLocation layer;

    public CandleLayer(float meltingSpeedMultiplier, float radiusMultiplier, float effectAmplifierMultiplier, float effectCooldownMultiplier, float effectDurationMultiplier, ResourceLocation layer, boolean layerFromBlockLocation){
        this.meltingSpeedMultiplier = meltingSpeedMultiplier;
        this.radiusMultiplier = radiusMultiplier;
        this.effectAmplifierMultiplier = effectAmplifierMultiplier;
        this.effectCooldownMultiplier = effectCooldownMultiplier;
        this.effectDurationMultiplier = effectDurationMultiplier;
        this.layerFromBlockLocation = layerFromBlockLocation;
        this.layer = layer;
    }
    public CandleLayer(){
        this(1, 1,1, 1,1,null,  false);
    }

    public CompoundTag save() {

        CompoundTag ct = new CompoundTag();
//        if (this.meltingSpeedMultiplier != 1)
        ct.putFloat("meltingSpeedMultiplier", this.meltingSpeedMultiplier);
//        if (this.radiusMultiplier != 1)
        ct.putFloat("radiusMultiplier", this.radiusMultiplier);
//        if (this.effectAmplifierMultiplier != 1)
        ct.putFloat("effectAmplifierMultiplier", this.effectAmplifierMultiplier);
//        if (this.effectCooldownMultiplier != 1)
        ct.putFloat("effectCooldownMultiplier", this.effectCooldownMultiplier);
//        if (this.effectDurationMultiplier != 1)
        ct.putFloat("effectDurationMultiplier", this.effectDurationMultiplier);
        if (this.layerFromBlockLocation)
            ct.putBoolean("layerFromBlockLocation", true);
//        if (this.layer != null)
        ct.putString("layer", this.layer.toString());

        return ct;
    }

    public void load(CompoundTag ct) {

        if (ct.contains("layer")) {
            if (ct.contains("meltingSpeedMultiplier"))
                this.meltingSpeedMultiplier = ct.getFloat("meltingSpeedMultiplier");
            else
                this.meltingSpeedMultiplier = 1;
            if (ct.contains("radiusMultiplier"))
                this.radiusMultiplier = ct.getFloat("radiusMultiplier");
            else
                this.radiusMultiplier = 1;
            if (ct.contains("effectAmplifierMultiplier"))
                this.effectAmplifierMultiplier = ct.getFloat("effectAmplifierMultiplier");
            else
                this.effectAmplifierMultiplier = 1;
            if (ct.contains("effectCooldownMultiplier"))
                this.effectCooldownMultiplier = ct.getFloat("effectCooldownMultiplier");
            else
                this.effectCooldownMultiplier = 1;
            if (ct.contains("effectDurationMultiplier"))
                this.effectDurationMultiplier = ct.getFloat("effectDurationMultiplier");
            else
                this.effectDurationMultiplier = 1;
            if (ct.contains("layerFromBlockLocation"))
                this.layerFromBlockLocation = ct.getBoolean("layerFromBlockLocation");
            else
                this.layerFromBlockLocation = false;
            this.layer = ResourceLocation.parse(ct.getString("layer"));
        }
    }
}

