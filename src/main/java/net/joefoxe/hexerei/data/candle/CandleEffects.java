package net.joefoxe.hexerei.data.candle;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CandleEffects {

    public static Map<String,AbstractCandleEffect> effects;

     public static void init() {
         effects = new HashMap<>();

         ForgeRegistries.MOB_EFFECTS.forEach(mobEffect -> {
             ResourceLocation loc = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
             String str = loc != null ? loc.toString() : mobEffect.getDescriptionId();
             effects.put(str, new PotionCandleEffect(mobEffect));
         });
         effects.put(new ResourceLocation(Hexerei.MOD_ID, "growth_effect").toString(), new BonemealingCandleEffect());
         effects.put(new ResourceLocation(Hexerei.MOD_ID, "sunshine_effect").toString(), new SunshineCandleEffect());

//         effects.forEach(((s, candleEffect) -> {
//             System.out.println(s + " - " + candleEffect);
//         }));
//         ForgeRegistries.PARTICLE_TYPES.forEach(((particleType) -> {
//             ResourceLocation loc = ForgeRegistries.PARTICLE_TYPES.getKey(particleType);
//             String str = loc != null ? loc.toString() : particleType.toString();
//             System.out.println(str);
//         }));
    }

    public static AbstractCandleEffect getEffect(String key) {
         if(effects == null)
             CandleEffects.init();
        return effects.containsKey(key) ? effects.get(key).getCopy() : new AbstractCandleEffect();
    }
}
