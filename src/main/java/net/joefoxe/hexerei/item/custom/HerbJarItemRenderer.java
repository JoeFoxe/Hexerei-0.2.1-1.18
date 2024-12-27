package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.renderer.HerbJarRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.Random;


public class HerbJarItemRenderer extends CustomItemRenderer {

    private HerbJarRenderer renderer;

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
//
//        matrixStackIn.pushPose();
//        matrixStackIn.translate(0.2, -0.1, -0.10);
//        BlockItem item = ((BlockItem) stack.getItem());
//        BlockState state = item.getBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, RenderType.tripwire());
//        matrixStackIn.popPose();

        this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static int getCustomColor(CompoundTag tag) {
        CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    @OnlyIn(Dist.CLIENT)
    public static HerbJarTile loadBlockEntityFromItem(CompoundTag tag, ItemStack item) {
        if (item.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof HerbJar herbJar) {
                HerbJarTile te = (HerbJarTile)herbJar.newBlockEntity(BlockPos.ZERO, block.defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
                te.itemHandler.deserializeNBT(Hexerei.proxy.getLevel().registryAccess(), tag.getCompound("Inventory"));
                te.dyeColor = getCustomColor(tag);
                if(item.has(DataComponents.CUSTOM_NAME))
                    te.customName = item.getHoverName();
//                if (te != null) te.load(tag);
                return te;
            }
        }
        return null;
    }

    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
    }

    public void renderTileStuff(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        HerbJarTile tileEntityIn = loadBlockEntityFromItem(tag, stack);

        String name = tileEntityIn.getDisplayName().getString();
        DyeColor col = HexereiUtil.getDyeColorNamed(name);

        int color = col != null ? HexereiUtil.getColorValue(col) : HexereiUtil.getDyeColor(stack);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate(8D/16D, 4.25D/16D, 4D/16D);
        matrixStackIn.scale(0.30f, 0.30f, 0.30f);

//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();


        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(0));
        matrixStackIn.translate(0.2, -0.1, -0.1);
//        Lighting.setupFor3DItems();
        renderBlock(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModBlocks.HERB_JAR.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, color != 0x422F1E && color != 0), null, color);
//        renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.HERB_JAR.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, color != 0x422F1E && color != 0));

//        renderBlock(matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModBlocks.HERB_JAR.get().defaultBlockState().setValue(HerbJar.GUI_RENDER, true).setValue(HerbJar.DYED, (color != 0x422F1E && color != 0)), color);
        matrixStackIn.popPose();


        if(!tileEntityIn.itemHandler.isEmpty())
        {
            BlockState state = null; //ModBlocks.HERB_JAR_GENERIC.get().defaultBlockState()

            Item item = tileEntityIn.itemHandler.getContents().get(0).getItem();
            if(item == ModBlocks.BELLADONNA_PLANT.get().asItem() ||
                    item == ModItems.BELLADONNA_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_BELLADONNA.get().defaultBlockState();
            if(item == ModBlocks.MUGWORT_BUSH.get().asItem() ||
                    item == ModItems.MUGWORT_LEAVES.get().asItem() ||
                    item == ModItems.MUGWORT_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_MUGWORT.get().defaultBlockState();
            if(item == ModBlocks.MANDRAKE_PLANT.get().asItem() ||
                    item == ModItems.MANDRAKE_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_MANDRAKE_PLANT.get().defaultBlockState();
            if(item == ModItems.MANDRAKE_ROOT.get())
                state = ModBlocks.HERB_JAR_MANDRAKE_ROOT.get().defaultBlockState();
            if(item == ModBlocks.YELLOW_DOCK_BUSH.get().asItem() ||
                    item == ModItems.YELLOW_DOCK_LEAVES.get().asItem() ||
                    item == ModItems.YELLOW_DOCK_FLOWERS.get().asItem())
                state = ModBlocks.HERB_JAR_YELLOW_DOCK.get().defaultBlockState();

            Random rand = new Random(0);

            Minecraft minecraft = Minecraft.getInstance();
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            ItemModelShaper shaper = itemRenderer.getItemModelShaper();
            boolean is3dModel = shaper.getModelManager().getModel(new ModelResourceLocation(HexereiUtil.getRegistryName(item), "inventory")).isGui3d();
            for(int a = 0; a < ((float)tileEntityIn.itemHandler.getContents().get(0).getCount() / 1024f) * 10f; a++){
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.2, -0.1, -0.10);
//                boolean is3dModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory")).isGui3d();
                if(is3dModel) {
                    matrixStackIn.translate(0, 1.5D / 16D, 0);
                }

                if(state != null){
                    matrixStackIn.translate(8D / 16D, 0.5D / 16D * a, 8D / 16D);
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * a));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(0 ));
                    renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                } else {

                    matrixStackIn.translate(8D / 16D, 0.5D / 16D * a + 1D/16D, 8D / 16D);
                    matrixStackIn.scale(0.4f,0.4f,0.4f);
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(rand.nextInt(90) * a));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(0 ));
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(80 + rand.nextInt(20)));
                    if(is3dModel)
                        matrixStackIn.scale(1.20f, 1.20f, 1.20f);
                    renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                }


                matrixStackIn.popPose();
            }
        }


        int i = 0x464F56;
        int j = (int)((double) FastColor.ARGB32.red(i) * 0.4D);
        int k = (int)((double) FastColor.ARGB32.green(i) * 0.4D);
        int l = (int)((double) FastColor.ARGB32.blue(i) * 0.4D);
        int i1 = FastColor.ARGB32.color( 0, j, k, l);


        matrixStackIn.translate(0.2, -0.1, -0.10);
        matrixStackIn.translate(8D / 16D, 8D / 16D, 1 - 12.05D / 16D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));

        matrixStackIn.scale(0.010416667F / 1.5f, -0.010416667F / 1.5f, 0.010416667F / 1.5f);


        Component component = null;
        if(stack.has(DataComponents.CUSTOM_NAME))
            component = stack.getHoverName();
//
        if(component == null){
            if (tileEntityIn.getItemStackInSlot(0) != ItemStack.EMPTY) {
                if (tileEntityIn.getItemStackInSlot(0).getHoverName().getString().equals(""))
                    component = tileEntityIn.getItemStackInSlot(0).getItem().getName(tileEntityIn.getItemStackInSlot(0));
                else
                    component = tileEntityIn.getItemStackInSlot(0).getHoverName();
            }
        }


        if(component != null){
            List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 70);
            float f3 = (float) (-Minecraft.getInstance().font.width(list.get(0)) / 2);
            if(tileEntityIn.dyeColor != 0x422F1E && tileEntityIn.dyeColor != 0)
                matrixStackIn.translate(0, 5, 1);
            Minecraft.getInstance().font.drawInBatch(list.get(0), f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, Font.DisplayMode.NORMAL, 0, combinedLightIn);

            if (list.size() > 1) {
                matrixStackIn.translate(0, 10, 0);
                f3 = (float) (-Minecraft.getInstance().font.width(list.get(1)) / 2);
                Minecraft.getInstance().font.drawInBatch(list.get(1), f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, Font.DisplayMode.NORMAL, 0, combinedLightIn);
            }
        }

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
    }
    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);
    }


    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, net.minecraft.client.renderer.RenderType renderType, int color) {
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
                    for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }

//
//    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, RenderType renderType, int color) {
//        RenderShape rendershape = p_110913_.getRenderShape();
//        if (rendershape != RenderShape.INVISIBLE) {
//            switch(rendershape) {
//                case MODEL:
//                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
//                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
//                    int i = color;
//                    float f = (float)(i >> 16 & 255) / 255.0F;
//                    float f1 = (float)(i >> 8 & 255) / 255.0F;
//                    float f2 = (float)(i & 255) / 255.0F;
//
//                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
//                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
////                    dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, renderType);
//                    break;
//                case ENTITYBLOCK_ANIMATED:
//                    ItemStack stack = new ItemStack(p_110913_.getBlock());
//                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
//            }
//
//        }
//    }

}