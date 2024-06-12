package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;

import java.util.List;

public class BottlingRecipeCategory implements IRecipeCategory<BottlingRecipeJEI> {
    public final static ResourceLocation UID = new ResourceLocation(Hexerei.MOD_ID, "bottling");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Hexerei.MOD_ID, "textures/gui/bottling_gui_jei.png");
    private IDrawable background;
    private final IDrawable icon;
    public BottlingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 126, 59);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.BLOOD_BOTTLE.get()));
    }

    @Override
    public RecipeType<BottlingRecipeJEI> getRecipeType() {
        return new RecipeType<>(UID, BottlingRecipeJEI.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.bottling");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {

        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BottlingRecipeJEI recipe, IFocusGroup focuses) {
        builder.moveRecipeTransferButton(160, 90);

        builder.addSlot(RecipeIngredientRole.INPUT,14, 24).addItemStack(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT,62, 24)
                .setFluidRenderer(2000, true, 16, 16)
                .addFluidStack(recipe.getInputFluid().getFluid(), 250, recipe.getInputFluid().hasTag() ? recipe.getInputFluid().getTag() : new CompoundTag()).setOverlay(new IDrawable() {
            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {

                Lighting.setupFor3DItems();
                RenderSystem.enableDepthTest();
                guiGraphics.pose().pushPose();

                guiGraphics.pose().translate(xOffset, yOffset, 0);
                guiGraphics.pose().mulPoseMatrix(new Matrix4f().scale(1, -1, 1));
                guiGraphics.pose().translate(-3, -15, 0);
                guiGraphics.pose().scale(17, 17, 17);
                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                Vec3 rotationOffset = new Vec3(0, 0, 0);

                float zRot = 0;
                float xRot = 20;
                float yRot = 30;

                guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
                guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
                guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
                guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
                guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

                BlockState blockState = ModBlocks.MIXING_CAULDRON.get().defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);

                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                renderBlock(guiGraphics.pose(), buffer, LightTexture.FULL_BRIGHT, blockState, 0xFF404040);

                MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipe.getInputFluid(), 1, 1, OverlayTexture.NO_OVERLAY);

                guiGraphics.pose().popPose();
            }
        }, 0, 0);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 24).addItemStack(recipe.getOutput());

    }

    @Override
    public void draw(BottlingRecipeJEI recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        Minecraft minecraft = Minecraft.getInstance();
        Component outputName = recipe.getOutput().getHoverName();



        int width = minecraft.font.width(outputName);
        float lineHeight = minecraft.font.lineHeight / 2f;
        if(width > 80){
            float percent = width/80f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1/percent, 1/percent, 1/percent);
            minecraft.font.drawInBatch(outputName, 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            guiGraphics.pose().popPose();

        }else {
            minecraft.font.drawInBatch(outputName, 7, 5f + lineHeight - 4.5f, 0xFF404040, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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