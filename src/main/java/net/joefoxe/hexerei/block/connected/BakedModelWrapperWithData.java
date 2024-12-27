package net.joefoxe.hexerei.block.connected;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public abstract class BakedModelWrapperWithData extends BakedModelWrapper<BakedModel> {

    public BakedModelWrapperWithData(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public final ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        ModelData.Builder builder = ModelData.builder();
        if (originalModel instanceof BakedModelWrapperWithData)
            ((BakedModelWrapperWithData) originalModel).gatherModelData(builder, world, pos, state, blockEntityData);
        gatherModelData(builder, world, pos, state, blockEntityData);
        return builder.build();
    }

    protected abstract ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world,
                                                         BlockPos pos, BlockState state, ModelData blockEntityData);

}