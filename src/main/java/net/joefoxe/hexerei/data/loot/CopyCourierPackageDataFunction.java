package net.joefoxe.hexerei.data.loot;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CourierPackageTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class CopyCourierPackageDataFunction extends LootItemConditionalFunction {

    public static final MapCodec<CopyCourierPackageDataFunction> CODEC = RecordCodecBuilder.mapCodec(
            p_298065_ -> commonFields(p_298065_)
                    .apply(p_298065_, CopyCourierPackageDataFunction::new)
    );
    protected CopyCourierPackageDataFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CourierPackageTile courierPackageTile) {
            CompoundTag tag = courierPackageTile.saveData(new CompoundTag(), blockEntity.getLevel().registryAccess());
            if (!tag.isEmpty())
                BlockItem.setBlockEntityData(stack, courierPackageTile.getType(), courierPackageTile.save(new CompoundTag(), blockEntity.getLevel().registryAccess()));
            return stack;
        }

        return stack;
    }

    @Override
    public LootItemFunctionType<CopyCourierPackageDataFunction> getType() {
        return ModItems.COPY_PACKAGE_DATA.get();
    }

//    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyCourierPackageDataFunction> {
//
//
//        @Override
//        public CopyCourierPackageDataFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
//            return new CopyCourierPackageDataFunction(pConditions);
//        }
//    }
//
//    public static CopyCourierPackageDataFunction.Builder builder() {
//        return new CopyCourierPackageDataFunction.Builder();
//    }
//
//    public static class Builder extends LootItemConditionalFunction.Builder<CopyCourierPackageDataFunction.Builder> {
//        @Override
//        protected CopyCourierPackageDataFunction.Builder getThis() {
//            return this;
//        }
//
//        @Override
//        public LootItemFunction build() {
//            return new CopyCourierPackageDataFunction(getConditions());
//        }
//    }
}