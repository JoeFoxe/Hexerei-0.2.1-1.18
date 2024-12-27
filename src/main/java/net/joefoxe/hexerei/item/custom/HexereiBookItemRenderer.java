package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HexereiBookItemRenderer extends CustomItemRendererWithPageDrawing {

    float degreesOpened;
    float degreesOpened2;
    float yPos;
    float xPos;
    float zPos;
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

//        matrixStackIn.pushPose();
//        matrixStackIn.translate(0.2, -0.1, -0.10);
//        BlockItem item = ((BlockItem) stack.getItem());
//        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, null);
//        matrixStackIn.popPose();

        this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    public static BookOfShadowsAltarTile loadBlockEntityFromItem(ItemStack item) {
        if (item.getItem() instanceof HexereiBookItem) {
            BookOfShadowsAltarTile te = new BookOfShadowsAltarTile(BlockPos.ZERO, ModBlocks.BOOK_OF_SHADOWS_ALTAR.get().defaultBlockState());
            te.itemHandler.setStackInSlot(0, item);
            return te;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTileStuff(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        BookOfShadowsAltarTile tileEntityIn = loadBlockEntityFromItem(stack);


        if(tileEntityIn == null)
            return;

        tileEntityIn.tickCount = Hexerei.getClientTicks();

        if(tileEntityIn.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem){

            BookData bookData = stack.getOrDefault(ModDataComponents.BOOK, BookData.EMPTY);

            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            matrixStackIn.pushPose();
            tileEntityIn.degreesOpened = 90;
            tileEntityIn.degreesFlopped = 90;
            if(bookData.isOpened()) {
                tileEntityIn.degreesOpened = 18;
                tileEntityIn.degreesFlopped = 0;
                this.degreesOpened = -10;
                tileEntityIn.degreesSpun = 270;
            }

            tileEntityIn.degreesSpunRender = tileEntityIn.degreesSpun;
            tileEntityIn.degreesFloppedRender = tileEntityIn.degreesFlopped;
            tileEntityIn.degreesOpenedRender = tileEntityIn.degreesOpened;
            tileEntityIn.pageOneRotationRender = tileEntityIn.pageOneRotation;
            tileEntityIn.pageTwoRotationRender = tileEntityIn.pageTwoRotation;
            if (tileEntityIn.degreesOpened != 90) {
                try {

                    drawPages(tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, true, transformType, 0);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }

//            this.itemRenderer = Minecraft.getInstance().getItemRenderer();


            yPos = 0;
            xPos = 0;
            zPos = 0;
            this.degreesOpened2 = 0;
            this.degreesOpened = 45;
            if (bookData.isOpened()) {
                tileEntityIn.degreesOpened = 18;
                tileEntityIn.degreesFlopped = 0;
                this.degreesOpened = -10;
                tileEntityIn.degreesSpun = 270;
            } else {
                tileEntityIn.degreesOpened = 90;
                tileEntityIn.degreesFlopped = 90;
                if (transformType == ItemDisplayContext.GUI) {
                    yPos = 6 / 16f;
                    xPos = 2 / 16f;
                    zPos = -12 / 32f;
                    matrixStackIn.scale(1.35f, 1.35f, 1.35f);
                }
                if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                    this.degreesOpened2 = 90;
                    xPos = 4 / 16f;
                    zPos = -12 / 32f;
                }
                if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                    this.degreesOpened2 = 90;
                    xPos = 4 / 16f;
                    zPos = -1 / 32f;
                }
            }


            tileEntityIn.degreesSpunRender = tileEntityIn.degreesSpun;
            tileEntityIn.degreesFloppedRender = tileEntityIn.degreesFlopped;
            tileEntityIn.degreesOpenedRender = tileEntityIn.degreesOpened;
            tileEntityIn.pageOneRotationRender = tileEntityIn.pageOneRotation;
            tileEntityIn.pageTwoRotationRender = tileEntityIn.pageTwoRotation;


            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(tileEntityIn.degreesOpened));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, (float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate(-(float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, -(float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-tileEntityIn.degreesOpened));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, -(float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get().defaultBlockState(), col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col));
            matrixStackIn.popPose();

            matrixStackIn.pushPose();
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_BINDING.get().defaultBlockState(), HexereiBookItem.getColor2(stack));
            matrixStackIn.popPose();

            if(tileEntityIn.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (tileEntityIn.pageTwoRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotation)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (tileEntityIn.pageTwoRotation / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.turnPage == 1 || tileEntityIn.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.degreesFloppedRender != 90){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f)*(-tileEntityIn.pageTwoRotationRender)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f)*(tileEntityIn.pageOneRotationRender / 16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }

            if(tileEntityIn.turnPage == 2 || tileEntityIn.turnPage == -1){
                matrixStackIn.pushPose();
                matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
                matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpened / 5f - 12f));
                matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpened / 2 + this.degreesOpened)));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened2));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
                matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
                matrixStackIn.translate(0, 1f / 32f, 0);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpened / 1.12f)));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpened / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender / 16f + 180/16f)));
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.BOOK_OF_SHADOWS_PAGE.get().defaultBlockState());
                matrixStackIn.popPose();
            }


            matrixStackIn.popPose();
        }



    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    float f = (float) (color >> 16 & 255) / 255.0F;
                    float f1 = (float) (color >> 8 & 255) / 255.0F;
                    float f2 = (float) (color & 255) / 255.0F;
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

}