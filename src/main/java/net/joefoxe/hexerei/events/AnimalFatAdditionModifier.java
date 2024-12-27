package net.joefoxe.hexerei.events;


import com.hollingsworth.arsnouveau.api.loot.DungeonLootEnhancerModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AnimalFatAdditionModifier extends LootModifier {
    private final Item addition;


    public static final MapCodec<AnimalFatAdditionModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(
                            Codec.STRING.optionalFieldOf("addition", "").forGetter(d -> BuiltInRegistries.ITEM.getKey(d.addition).toString())
                    )
                    .apply(instance, AnimalFatAdditionModifier::new));

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
            NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Hexerei.MOD_ID);
    private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AnimalFatAdditionModifier>> GRASS_DROPS = REGISTER.register(
            "animal_fat_drops", () -> CODEC
    );

    public AnimalFatAdditionModifier(final LootItemCondition[] conditionsIn, String addition) {
        super(conditionsIn);
        this.addition = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(addition)).orElse(Items.AIR);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.ENCHANTMENT_ACTIVE)) {
            if (context.getRandom().nextDouble() / (double) Math.min(context.getParam(LootContextParams.ENCHANTMENT_LEVEL) + 1, 4) < 0.45D)
                generatedLoot.add(new ItemStack(addition, context.getRandom().nextInt(Math.min(context.getParam(LootContextParams.ENCHANTMENT_LEVEL) + 1, 4)) + 1));
        } else {
            if (context.getRandom().nextDouble() < 0.45D)
                generatedLoot.add(new ItemStack(addition, 1));
        }

        return generatedLoot;
    }

    public static void init(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return GRASS_DROPS.get();
    }

}