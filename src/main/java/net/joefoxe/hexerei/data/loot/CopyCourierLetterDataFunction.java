package net.joefoxe.hexerei.data.loot;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CourierLetterTile;
import net.joefoxe.hexerei.tileentity.CourierPackageTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyCourierLetterDataFunction extends LootItemConditionalFunction {
    protected CopyCourierLetterDataFunction(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CourierLetterTile courierLetterTile) {
            CompoundTag tag = courierLetterTile.saveData(new CompoundTag());
            if (!tag.isEmpty())
                BlockItem.setBlockEntityData(stack, courierLetterTile.getType(), courierLetterTile.save(new CompoundTag()));
            return stack;
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModItems.COPY_LETTER_DATA.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyCourierLetterDataFunction> {


        @Override
        public CopyCourierLetterDataFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new CopyCourierLetterDataFunction(pConditions);
        }
    }

    public static CopyCourierLetterDataFunction.Builder builder() {
        return new CopyCourierLetterDataFunction.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyCourierLetterDataFunction.Builder> {
        @Override
        protected CopyCourierLetterDataFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyCourierLetterDataFunction(getConditions());
        }
    }
}