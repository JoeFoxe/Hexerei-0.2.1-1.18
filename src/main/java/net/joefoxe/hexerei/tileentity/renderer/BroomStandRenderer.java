package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.custom.BroomStandWall;
import net.joefoxe.hexerei.tileentity.BroomStandTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BroomStandRenderer implements BlockEntityRenderer<BroomStandTile> {


    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    @Override
    public AABB getRenderBoundingBox(BroomStandTile blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(5);
    }

    @Override
    public void render(BroomStandTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        Level level = tileEntityIn.getLevel();
        if(level == null)
            return;

        if(!level.getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof BroomStandTile))
            return;

        Vec3 offset = new Vec3(0, 0, 0);
        ItemStack stack = tileEntityIn.itemHandler.getStackInSlot(0);
        Direction dir = Direction.NORTH;
        if(tileEntityIn.getBlockState().hasProperty(HorizontalDirectionalBlock.FACING)) {
            dir = tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            if(dir == Direction.NORTH || dir == Direction.SOUTH)
                dir = dir.getOpposite();
        }
        if(tileEntityIn.getBlockState().getBlock() instanceof BroomStandWall broomStandWall)
            offset = new Vec3(0, -6.75f / 16f, -0.15f);

        if(!stack.isEmpty()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f, 10.25f / 16f, 8f / 16f);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
            matrixStackIn.translate(0, offset.y(), 0.25f / 16f + offset.z());
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-16));
            matrixStackIn.scale(2.25f, 2.25f, 2.25f);
            renderItem(stack, tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            matrixStackIn.popPose();
        }

    }

    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int overlayLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                overlayLightIn, matrixStackIn, bufferIn, level, 1);
    }


    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }


}
