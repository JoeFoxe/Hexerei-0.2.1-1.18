package net.joefoxe.hexerei.block.connected;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE

public class CustomBlockModels {

    private final Multimap<ResourceLocation, Function<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
    private final Map<Block, Function<BakedModel, ? extends BakedModel>> finalModelFuncs = new IdentityHashMap<>();
    private boolean funcsLoaded = false;

    public void register(ResourceLocation block, Function<BakedModel, ? extends BakedModel> func) {
        modelFuncs.put(block, func);
    }

    public void forEach(BiConsumer<Block, Function<BakedModel, ? extends BakedModel>> consumer) {
        loadEntriesIfMissing();
        finalModelFuncs.forEach(consumer);
    }

    private void loadEntriesIfMissing() {
        if (!funcsLoaded) {
            loadEntries();
            funcsLoaded = true;
        }
    }

    private void loadEntries() {
        finalModelFuncs.clear();
        modelFuncs.asMap().forEach((location, funcList) -> {
            if (!BuiltInRegistries.BLOCK.containsKey(location))
                return;
            Block block = BuiltInRegistries.BLOCK.get(location);

            Function<BakedModel, ? extends BakedModel> finalFunc = null;
            for (Function<BakedModel, ? extends BakedModel> func : funcList) {
                if (finalFunc == null) {
                    finalFunc = func;
                } else {
                    finalFunc = finalFunc.andThen(func);
                }
            }

            finalModelFuncs.put(block, finalFunc);
        });
    }

}
