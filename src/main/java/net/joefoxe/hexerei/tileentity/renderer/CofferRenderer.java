package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

public class CofferRenderer implements BlockEntityRenderer<CofferTile> {

    public static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return (float)(1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    @Override
    public void render(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if(!tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CofferTile))
            return;

        matrixStackIn.pushPose();
        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(1, 0D / 16D, 1);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(0));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(1D, 0D / 16D, 0D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        }
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_CHEST.get().defaultBlockState(), tileEntityIn.getDyeColor());
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(8D / 16D, 4D / 16D, 4D / 16D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.translate(8D / 16D, 4D / 16D, 12D / 16D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(0));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(12D / 16D, 4D / 16D, 8D / 16D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(4D / 16D, 4D / 16D, 8D / 16D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        }
        float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);

        float percent = (lerpDegreesOpened / (float) CofferTile.lidOpenAmount);
        percent = easeOutBack(percent);

        float sideRotation = (percent * 135);

        matrixStackIn.mulPose(Axis.XP.rotationDegrees(percent * CofferTile.lidOpenAmount));
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_LID.get().defaultBlockState(), tileEntityIn.getDyeColor());
        matrixStackIn.popPose();

        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH || tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(11.7299D / 16D, 2.4772D / 16D, 5.475D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(11.7299D / 16D, 2.4772D / 16D, 10.525D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(4.2701 / 16D, 2.4772D / 16D, 5.475D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(4.2701 / 16D, 2.4772D / 16D, 10.525D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(1D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 1.75D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 5D / 16D);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_CONTAINER.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(11D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 1.75D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 5D / 16D);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_CONTAINER.get().defaultBlockState());
            matrixStackIn.popPose();
        }

        if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST || tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {

            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(11.7299D / 16D, 2.4772D / 16D, 5.475D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(11.7299D / 16D, 2.4772D / 16D, 10.525D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(4.2701 / 16D, 2.4772D / 16D, 5.475D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(4.2701 / 16D, 2.4772D / 16D, 10.525D / 16D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_HINGE.get().defaultBlockState());
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(1D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 1.75D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 5D / 16D);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_CONTAINER.get().defaultBlockState());
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0D / 16D, 0D / 16D, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            matrixStackIn.translate(11D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 1.75D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 5D / 16D);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COFFER_CONTAINER.get().defaultBlockState());
            matrixStackIn.popPose();
        }

        //render items only if its at least slightly opened
        if (lerpDegreesOpened > 2) {
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH)
                renderItemsNorth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
                renderItemsWest(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH)
                renderItemsSouth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST)
                renderItemsEast(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);

        }


    }

    private void renderItemsNorth(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                  MultiBufferSource bufferIn, int combinedLightIn) {
        float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
        float percent = (lerpDegreesOpened / (float) CofferTile.lidOpenAmount);
        percent = easeOutBack(percent);

        float sideRotation = (percent * 135);

        // item row 1
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1, 0, -1 + 1D / 32D);
        matrixStackIn.translate(4.5D / 16D - 0.5D / 16D, 0.15D, 6.5D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 2

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 3

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 4

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 5
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1 + 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

    }

    private void renderItemsSouth(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn) {
        float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
        float percent = (lerpDegreesOpened / (float) CofferTile.lidOpenAmount);
        percent = easeOutBack(percent);

        float sideRotation = (percent * 135);

        // item row 1
        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(4.5D / 16D - 0.5D / 16D, 0.15D, 6.5D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 2

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 3

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 4

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 5
        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D, 0D / 16D, 0D / 16D - 1D / 32D);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

    }

    private void renderItemsWest(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn) {
        float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
        float percent = (lerpDegreesOpened / (float) CofferTile.lidOpenAmount);
        percent = easeOutBack(percent);

        float sideRotation = (percent * 135);

        // item row 1
        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1, 0, -1);
        matrixStackIn.translate(4.5D / 16D - 0.5D / 16D, 0.15D, 6.5D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 2

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 3

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 4

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 5
        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0D / 16D + 1D / 32D, 0D / 16D, 1.0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

    }

    private void renderItemsEast(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                                 MultiBufferSource bufferIn, int combinedLightIn) {
        float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
        float percent = (lerpDegreesOpened / (float) CofferTile.lidOpenAmount);
        percent = easeOutBack(percent);

        float sideRotation = (percent * 135);

        // item row 1
        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1, 0, -1);
        matrixStackIn.translate(4.5D / 16D - 0.5D / 16D, 0.15D, 6.5D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(3D / 16D - Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 6.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 2

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 7.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 3

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 8.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 4

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 9.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        // item row 5
        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(4.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(6D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(7.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 7D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 8D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(13D / 16D + Math.sin(((sideRotation - 90)/180f)*Math.PI) * 3D/16D, 4D / 16D-(Math.cos(((sideRotation + 90)/180f)*Math.PI) * 3D/16D), 9D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(9.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(11D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(1.0 - 1D / 32D, 0D / 16D, 0D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        matrixStackIn.translate(-1,0,-1);
        matrixStackIn.translate(12.5D/16D-0.5D/16D, 0.15D, 10.5D/16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5f));
        matrixStackIn.scale(0.20f, 0.20f, 0.20f);
        renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();

    }

    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
    }




    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }


    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, int color) {
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
