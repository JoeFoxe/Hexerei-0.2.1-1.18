package net.joefoxe.hexerei.data.books;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJei;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.screen.tooltip.HexereiBookTooltip;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber(value = Dist.CLIENT)
public class PageDrawing {
    public float lineWidth;
    public float lineHeight;
    public float tick;
    public ItemStack tooltipStack;
    public List<Component> tooltipText;
    public BookImage slotOverlay;
    public boolean drawTooltip;
    public boolean drawTooltipStack;
    public boolean drawTooltipStackFlag;
    public boolean drawTooltipTextFlag;
    public float drawTooltipScale;
    public float drawTooltipScaleOld;
    public boolean drawTooltipText;
    public boolean drawSlotOverlay;
    public PageOn slotOverlayPageOn;
    public boolean isRightPressedOld;
    public boolean isLeftPressedOld;

    public static ItemRenderer itemRenderer;

    public double mouseXOld;
    public double mouseYOld;
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    public static final ResourceLocation SLOT_ATLAS = HexereiUtil.getResource("book/slot");
    public static final ResourceLocation SLOT = HexereiUtil.getResource("textures/book/slot.png");
    public static final ResourceLocation TITLE = HexereiUtil.getResource("book/title");
    public static final float CORNERS = (float) MixingCauldron.SHAPE.min(Direction.Axis.X) + 3 / 16f;
    public static final float MIN_Y = 4f / 16f;
    public static final float MAX_Y = 15f/ 16f;

    public PageDrawing() {
        this.lineWidth = 0;
        this.lineHeight = 0;
        this.tick = 0;
        this.tooltipStack = ItemStack.EMPTY;
        this.tooltipText = new ArrayList<>();
        this.slotOverlay = new BookImage(0, 0, 1, 0, 0, 20, 20, 20, 20, 1, "hexerei:textures/book/slot_hover.png", new ArrayList<>());
        this.drawTooltipStack = false;
        this.drawTooltipStackFlag = false;
        this.drawTooltipTextFlag = false;
        this.drawTooltipScale = 0;
        this.drawTooltipText = false;
        this.drawSlotOverlay = false;
        this.slotOverlayPageOn = PageOn.LEFT_PAGE;
        this.isRightPressedOld = false;
        this.isLeftPressedOld = false;
        itemRenderer = Hexerei.proxy.getLevel() == null ? null : Hexerei.proxy.getLevel().isClientSide ? Minecraft.getInstance().getItemRenderer() : null;
        this.mouseXOld = 0;
        this.mouseYOld = 0;
    }


    protected static final Quaternionf ITEM_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis((65) * Math.PI / 180, 1, 0, 0);
        quaternion.rotateAxis((float)((50) * Math.PI / 180), 0, 1, 0);


        return quaternion;
    });
    protected static final Quaternionf BLOCK_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis((35) * Math.PI / 180, 1, 0, 0);
        quaternion.rotateAxis((float)((35) * Math.PI / 180), 0, 1, 0);


        return quaternion;
    });
    protected static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = Util.make(() -> {
        Quaternionf quaternion = new Quaternionf();
        quaternion.setAngleAxis(-45 * Math.PI / 180, 1, 0, 0);
        return quaternion;
    });

    public static ItemStack getTagStack(TagKey<Item> key) {

        float fl = 0;
        if (FMLEnvironment.dist.isClient())
            fl = Hexerei.getClientTicks();
        return BuiltInRegistries.ITEM.getRandomElementOf(key, RandomSource.create((long) (fl * 1000f))).orElse(Holder.direct(Items.AIR)).value().getDefaultInstance();
    }

    public static Block getTagBlock(TagKey<Block> key) {

        float fl = 0;
        if (FMLEnvironment.dist.isClient())
            fl = Hexerei.getClientTicks();
        return BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create((long) (fl * 1000f))).orElse(Holder.direct(Blocks.AIR)).value();
    }


    public static void renderItem(BookOfShadowsAltarTile tileEntityIn, @NotNull BookItemsAndFluids itemStackElement, PoseStack matrixStackIn, MultiBufferSource buffer, float xIn, float yIn, float zLevel, int combinedLight, int combinedOverlay, PageOn pageOn, boolean isItem) {

        ItemStack itemStack = itemStackElement.item;

        if (itemStackElement.type.equals("tag")) {
            int mod = ((int) Hexerei.getClientTicks()) % 60;

            if (itemStackElement.item.isEmpty()) {
                itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
            }

            if ((mod == 59 || mod == 58) && itemStackElement.refreshTag) {
                itemStack = getTagStack(itemStackElement.key);
                if (itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                if (itemStack.is(itemStackElement.item.getItem()))
                    itemStack = getTagStack(itemStackElement.key);
                itemStackElement.item = itemStack;
                itemStackElement.refreshTag = false;
                itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);
            }
            if (mod == 1 || mod == 2) {
                itemStackElement.refreshTag = true;
            }
        }

        matrixStackIn.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.021f / 16f);
        matrixStackIn.scale(0.049f, 0.049f, 0.001f);
        matrixStackIn.translate(yIn * 1.259f, -xIn * 1.259f, 0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90));

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-180));
        matrixStackIn.translate(-4.75f / 8f, -4.5f / 8f, 0);
        matrixStackIn.scale(0.065f, 0.065f, 0.05f);

        renderGuiItemDecorations(buffer, Minecraft.getInstance().font, itemStack, matrixStackIn, 0, 0, combinedOverlay, combinedLight);
        matrixStackIn.translate(0.75f / 8f / 0.065f, 0.5f / 8f / 0.065f, 0);
        matrixStackIn.scale(0.965f, 0.965f, 0.965f);
        renderGuiItemCount(buffer, Minecraft.getInstance().font, itemStack, matrixStackIn, 0, 0, combinedOverlay, combinedLight);
        matrixStackIn.popPose();
        Vector3f[] shaderLightDirections = new Vector3f[2];
        shaderLightDirections[0] = new Vector3f(RenderSystem.shaderLightDirections[0]);
        shaderLightDirections[1] = new Vector3f(RenderSystem.shaderLightDirections[1]);
        int[] originalLightmap = Util.make(() -> {
            int[] vals = new int[12];
            for(int i = 0; i < 12; ++i) {
                vals[i] = RenderSystem.getShaderTexture(i);
            }
            return vals;
        });

        try {
            if (itemRenderer == null)
                itemRenderer = Minecraft.getInstance().getItemRenderer();
            if (itemStackElement.modelCache == null)
                itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemStackElement.modelCache.isGui3d()) {
                matrixStackIn.last().normal().rotate(ITEM_LIGHT_ROTATION_3D);
            } else {
                matrixStackIn.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
            }
//            Lighting.setupForFlatItems();

            itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, matrixStackIn, buffer, combinedLight, combinedOverlay, itemStackElement.modelCache);

        } catch (Exception e) {
            // Shrug
        }
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
            bufferSource.endBatch();

        // Restore original lighting settings
        for(int i = 0; i < 12; ++i) {
            RenderSystem.setShaderTexture(i, originalLightmap[i]);
        }
        ShaderInstance shaderinstance = RenderSystem.getShader();
        RenderSystem.setShaderLights(shaderLightDirections[0], shaderLightDirections[1]);
        RenderSystem.setupShaderLights(shaderinstance);

        matrixStackIn.popPose();

    }


    public static void renderBlock(BookOfShadowsAltarTile tileEntityIn, @NotNull BookBlocks blockElement, PoseStack matrixStackIn, MultiBufferSource buffer, float xIn, float yIn, float zLevel, int combinedLight, int combinedOverlay, PageOn pageOn) {

        BlockState blockState = blockElement.blockState;

        if (blockElement.type.equals("tag")) {
            int mod = ((int) Hexerei.getClientTicks()) % 60;
            if (blockState.is(Blocks.AIR)) {
                blockState = getTagBlock(blockElement.key).defaultBlockState();
                blockElement.blockState = blockState;
            }

            if ((mod == 59 || mod == 58) && blockElement.refreshTag) {
                blockState = getTagBlock(blockElement.key).defaultBlockState();
                if (blockState.equals(blockElement.blockState))
                    blockState = getTagBlock(blockElement.key).defaultBlockState();
                if (blockState.equals(blockElement.blockState))
                    blockState = getTagBlock(blockElement.key).defaultBlockState();
                blockElement.blockState = blockState;
                blockElement.refreshTag = false;
            }
            if (mod == 1 || mod == 2) {
                blockElement.refreshTag = true;
            }
        }

        matrixStackIn.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, false, ItemDisplayContext.NONE);

        float scale = 0.62f;
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.57f / 16f, -0.021f / 16f);
        matrixStackIn.scale(0.049f * scale, 0.049f * scale, 0.001f);
        matrixStackIn.translate(yIn * 1.259f * (1 / scale), -xIn * 1.259f * (1 / scale), 0);
        matrixStackIn.translate(0.25f, 0.25f, 0.25f);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(225f - 180f - 15f));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(45));
        matrixStackIn.translate(-0.25f, -0.25f, -0.25f);


        try {
            if (blockState.getBlock() instanceof LiquidBlock liquidBlock) {
//                blockState = liquidBlock.getFluidState(liquidBlock.defaultBlockState()).createLegacyBlock().setValue(LiquidBlock.LEVEL, 7);
                matrixStackIn.last().normal().set(matrixStackIn.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
                renderFluidBlockGUI(matrixStackIn, buffer, new FluidStack(liquidBlock.fluid, 2000), 1, combinedLight, combinedOverlay);
                if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
                    bufferSource.endBatch();
            }else {
                matrixStackIn.last().normal().set(matrixStackIn.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, matrixStackIn, buffer, combinedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            }
        } catch (Exception e) {
            // Shrug
        }

        matrixStackIn.popPose();

    }

    public static void renderFluidBlockGUI(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, int combinedLight, int combinedOverlay){
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);

        alpha *= (color >> 24 & 255) / 255f;

        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuadsBlock(matrixStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, combinedLight, combinedOverlay);
    }

    private static void renderQuadsBlock(Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, int light, int overlay){
        float height = (MIN_Y + (MAX_Y - MIN_Y)) * 0.8f;
        float minU = sprite.getU(CORNERS * 16);
        float maxU = sprite.getU((1 - CORNERS) * 16);
        float minV = sprite.getV(CORNERS * 16);
        float maxV = sprite.getV((1 - CORNERS) * 16);

        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, CORNERS / 5f).setColor(r, g, b, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r, g, b, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r, g, b, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, CORNERS / 5f).setColor(r, g, b, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);


        float shading = 0.75f;
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);


        shading = 0.45f;
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, minV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, height, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(minU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, maxV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        vertexBuilder.addVertex(matrix, 1 - CORNERS / 5f, 0, 1 - CORNERS / 5f).setColor(r * shading, g * shading, b * shading, alpha).setUv(maxU, minV).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
    }


    public static void renderGuiItemDecorations(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light) {

        if (itemStack.isBarVisible()) {

            matrixStackIn.pushPose();
            int i = itemStack.getBarWidth();
            int j = itemStack.getBarColor();
            fillRect(matrixStackIn, bufferSource, xIn + 2.75f, yIn + 13.75f, 0, 13, 1.5f, 0, 0, 0, 255, overlay, light);
            fillRect(matrixStackIn, bufferSource, xIn + 2.75f, yIn + 13.75f, -0.5f, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255, overlay, light);
            matrixStackIn.popPose();
        }

    }


    public static void renderGuiItemCount(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light) {

        if (itemStack.getCount() > 1) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0, -7f);
            String s = String.valueOf(itemStack.getCount());
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)) + 1f, (yIn + 6 + 3) + 1f, HexereiUtil.getColorValueAlpha(0.245f, 0.245f, 0.245f, 1), false, matrixStackIn.last().pose(), bufferSource, Font.DisplayMode.NORMAL, overlay, light);
//              drawInBatch(pText, float pX,                        float pY,           int pColor,                                                  boolean pDropShadow,   Matrix4f pMatrix,   MultiBufferSource pBuffer, Font.DisplayMode pDisplayMode, int pBackgroundColor, int pPackedLightCoords) {
            matrixStackIn.translate(0, 0, -6f);
            font.drawInBatch(s, (xIn + 19 - 2 - font.width(s)), (yIn + 6 + 3), 16777215, false, matrixStackIn.last().pose(), bufferSource, Font.DisplayMode.NORMAL, overlay, light);
            multibuffersource$buffersource.endBatch();
            matrixStackIn.popPose();
        }

    }

    public static void renderGuiItem(MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack matrixStackIn, float xIn, float yIn, int overlay, int light) {


        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90));
        matrixStackIn.scale(16, 16, 1f);
        matrixStackIn.translate(yIn * 1.25f * 2 / 40 + 0.55f, -xIn * 1.25f * 2 / 40 - 0.55f, -2f);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));


        try {
            BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemModel.isGui3d()) {
                matrixStackIn.last().normal().set(matrixStackIn.last().normal().rotate(ITEM_LIGHT_ROTATION_3D));
            }
            else
                matrixStackIn.last().normal().set(matrixStackIn.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT));


            itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, matrixStackIn, bufferSource, light, overlay, itemModel);
        } catch (Exception e) {
            // Shrug
        }


        matrixStackIn.popPose();
    }


    private static void fillRect(PoseStack poseStack, MultiBufferSource p_115153_, float xIn, float yIn, float zIn, float widthIn, float heightIn, int p_115158_, int p_115159_, int p_115160_, int p_115161_, int overlay, int light) {

        poseStack.pushPose();
        poseStack.translate(0, 0, -4.15f);
        PoseStack.Pose normal = poseStack.last();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth = 1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;


        VertexConsumer buffer = p_115153_.getBuffer(RenderType.entityCutout(HexereiUtil.getResource("hexerei:textures/book/blank.png")));
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + 0), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + heightIn), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, (xIn + widthIn), (yIn + 0), zIn).setColor(p_115158_, p_115159_, p_115160_, p_115161_).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        poseStack.popPose();
    }


    public static void translateToLeftPageUnder(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if (!isItem)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        else
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
        if (isItem && transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.translate(0, 1f / 32f, 0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpenedRender / 1.12f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (tileEntityIn.pageOneRotationRender / 16f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-180));
        matrixStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    public static void translateToLeftPage(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemDisplayContext transformType) {


        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if (!isItem)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        else
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
        if (isItem && transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
        matrixStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));

        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 33);
        matrixStack.translate(0, 1f / 32f, 0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpenedRender / 1.12f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (tileEntityIn.pageOneRotationRender / 16f)));
//        matrixStack.translate(0,1/64f,0);
    }

    public static void translateToRightPageUnder(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if (!isItem)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        else
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
        if (isItem && transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
        matrixStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.translate(0, 1f / 32f, 0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpenedRender / 1.12f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (tileEntityIn.pageTwoRotationRender / 16f)));
//        matrixStack.translate(0, 1 / 64f, 0);

    }

    public static void translateToRightPage(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if (!isItem)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        else
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
        if (isItem && transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
        matrixStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.translate(0, 1f / 32f, 0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpenedRender / 1.12f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (tileEntityIn.pageTwoRotationRender / 16f)));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-180));
        matrixStack.translate(0, -1 / 2f + 1 / 8f - 1 / 128f, 0);
    }

    public static void translateToLeftPagePrevious(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        if (tileEntityIn.turnPage != 2 && tileEntityIn.turnPage != -1) {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if (!isItem)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
            else
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
            if (isItem && transformType != ItemDisplayContext.NONE)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(tileEntityIn.degreesOpenedRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, (float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90));
//            matrixStackIn.translate(0, 1 / 64f, 0);
        } else {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if (!isItem)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
            else
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
            if (isItem && transformType != ItemDisplayContext.NONE)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80f - tileEntityIn.degreesOpenedRender / 1.12f)));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees((-(80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageTwoRotationRender / 16f + 180 / 16f)));
//            matrixStackIn.translate(0, 1 / 64f, 0);
        }
    }

    public static void translateToRightPagePrevious(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        if (tileEntityIn.turnPage != 1 && tileEntityIn.turnPage != -1) {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(-(float) Math.sin((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f, 0f / 16f, -(float) Math.cos((tileEntityIn.degreesSpunRender + 90f) / 57.1f) / 32f);
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if (!isItem)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
            else
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
            if (isItem && transformType != ItemDisplayContext.NONE)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-55));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-tileEntityIn.degreesOpenedRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, (-0.5f * (tileEntityIn.degreesFloppedRender / 90)) / 16f, -(float) Math.sin((tileEntityIn.degreesFloppedRender) / 57.1f) / 32f);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90));
            matrixStackIn.translate(0, -0.375f - 1 / 128f, 0);
        } else {
            matrixStackIn.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
            matrixStackIn.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
            matrixStackIn.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
            if (!isItem)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
            else
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 10)));
            if (isItem && transformType != ItemDisplayContext.NONE)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(degreesOpened));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
            matrixStackIn.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
            matrixStackIn.translate(0, 1f / 32f, 0);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80f - tileEntityIn.degreesOpenedRender / 1.12f)));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(((80f - tileEntityIn.degreesOpenedRender / 1.12f) / 90f) * (-tileEntityIn.pageOneRotationRender / 16f + 180 / 16f)));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-180));
            matrixStackIn.translate(0, -0.375f - 1 / 128f, 0);
        }
    }

    public void translateToMiddleButton(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, boolean isItem, ItemDisplayContext transformType) {

        float yPos = 0;
        float xPos = 0;
        float zPos = 0;
        float degreesOpened = 0;

        if (transformType == ItemDisplayContext.GUI)
            yPos = 3 / 16f;
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -12 / 32f;
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            degreesOpened = 90;
            xPos = 4 / 16f;
            zPos = -1 / 32f;
        }

        matrixStack.translate(8f / 16f + xPos, 18f / 16f + yPos, 8f / 16f + zPos);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        if (!isItem)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        else
            matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 - 13)));
        if (isItem && transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
        matrixStack.mulPose(Axis.XP.rotationDegrees(degreesOpened));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(270));
        matrixStack.translate(2.95f / 64f, 7.1f / 16f, 11f / 32f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-180));
//        matrixStack.translate(0,0,0);
//        matrixStack.scale(0.003f,0.003f,0.003f);
//        matrixStack.translate(-16, -16, -10);

    }

    public void drawPage(BookPage page, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem, ItemDisplayContext transformType) throws CommandSyntaxException {
        drawPage(page, tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, pageOn, isItem, transformType, -1);
    }

    public void drawPage(BookPage page, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem, ItemDisplayContext transformType, int pageNum) throws CommandSyntaxException {

        if (page != null) {


            Player playerIn = Hexerei.proxy.getPlayer();

            double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
            Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

            for (int i = 0; i < page.paragraph.size(); i++) {
                drawString(((BookParagraph) (page.paragraph.toArray()[i])), tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
            }

            //draw page number
            if (pageOn == PageOn.LEFT_PAGE || pageOn == PageOn.LEFT_PAGE_PREV || pageOn == PageOn.RIGHT_PAGE_UNDER) {
                BookEntries bookEntries = BookManager.getBookEntries();
                if (bookEntries != null) {
                    int pageOnNum = pageNum + 1 - bookEntries.chapterList.get(0).endPage;
                    BookParagraphElements bookParagraphElements = new BookParagraphElements(14.3f, 19.25f, 1, 30, "top");
                    ArrayList<BookParagraphElements> list = new ArrayList<>();
                    list.add(bookParagraphElements);
                    BookParagraph bookParagraph;
                    if (pageOnNum > 0)
                        bookParagraph = new BookParagraph(list, String.valueOf(pageOnNum), "left");
                    else
                        bookParagraph = new BookParagraph(list, HexereiUtil.intToRoman(pageNum + 1), "left");
                    bookParagraph.paragraphElements.get(0).x -= Minecraft.getInstance().font.width(bookParagraph.passage) / 8f;

                    drawString(bookParagraph, tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
            }
            if (pageOn == PageOn.RIGHT_PAGE || pageOn == PageOn.RIGHT_PAGE_PREV || pageOn == PageOn.LEFT_PAGE_UNDER) {
                BookEntries bookEntries = BookManager.getBookEntries();
                if (bookEntries != null) {
                    int pageOnNum = pageNum + 1 - bookEntries.chapterList.get(0).endPage;
                    BookParagraphElements bookParagraphElements = new BookParagraphElements(0, 19.25f, 1, 30, "top");
                    ArrayList<BookParagraphElements> list = new ArrayList<>();
                    list.add(bookParagraphElements);
                    BookParagraph bookParagraph;
                    if (pageOnNum > 0)
                        bookParagraph = new BookParagraph(list, String.valueOf(pageOnNum), "left");
                    else
                        bookParagraph = new BookParagraph(list, HexereiUtil.intToRoman(pageNum + 1), "left");

                    drawString(bookParagraph, tileEntityIn, matrixStackIn, bufferIn, 0, 0, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                }
            }

            for (int i = 0; i < page.itemList.size(); i++) {
                BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page.itemList.toArray()[i]));
                drawItemInSlot(tileEntityIn, bookItemStackInSlot, matrixStackIn, bufferIn, bookItemStackInSlot.x, bookItemStackInSlot.y, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);
            }

            for (int i = 0; i < page.blockList.size(); i++) {
                BookBlocks bookBlocks = ((BookBlocks) (page.blockList.toArray()[i]));
                drawBlock(tileEntityIn, bookBlocks, matrixStackIn, bufferIn, bookBlocks.x, bookBlocks.y, 0, combinedLightIn, combinedOverlayIn, pageOn);
            }

            if (transformType == ItemDisplayContext.NONE) {
                for (int i = 0; i < page.itemList.size(); i++) {
                    BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page.itemList.toArray()[i]));

                    if (pageOn == PageOn.LEFT_PAGE) {

                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {


                            if (bookItemStackInSlot.item != null) {
                                if (!bookItemStackInSlot.item.isEmpty()) {
                                    this.tooltipStack = bookItemStackInSlot.item;
                                    this.tooltipText = bookItemStackInSlot.extra_tooltips;
                                    this.drawTooltipStack = true;
                                }
                            } else {
                                this.tooltipText = getFluidTooltip(bookItemStackInSlot);
                                this.tooltipStack = ItemStack.EMPTY;
                                this.drawTooltipText = true;
                            }
                            this.slotOverlay.x = bookItemStackInSlot.x;
                            this.slotOverlay.y = bookItemStackInSlot.y;
                            ArrayList<BookImageEffect> effects = new ArrayList<>();
                            effects.add(new BookImageEffect("scale", 20, 1.1f));
                            this.slotOverlay.effects = effects;
                            this.slotOverlayPageOn = pageOn;
                            this.drawSlotOverlay = true;
                            break;
                        }
                        if (this.drawTooltipStack)
                            break;
                    }
                    if (pageOn == PageOn.RIGHT_PAGE) {


                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            if (bookItemStackInSlot.item != null) {
                                if (!bookItemStackInSlot.item.isEmpty()) {
                                    this.tooltipStack = bookItemStackInSlot.item;
                                    this.tooltipText = bookItemStackInSlot.extra_tooltips;
                                    this.drawTooltipStack = true;
                                }
                            } else {
                                this.tooltipText = getFluidTooltip(bookItemStackInSlot);
                                this.tooltipStack = ItemStack.EMPTY;
                                this.drawTooltipText = true;
                            }
                            this.slotOverlay.x = bookItemStackInSlot.x;
                            this.slotOverlay.y = bookItemStackInSlot.y;
                            ArrayList<BookImageEffect> effects = new ArrayList<>();
                            effects.add(new BookImageEffect("scale", 20, 1.1f));
                            this.slotOverlay.effects = effects;
                            this.slotOverlayPageOn = pageOn;
                            this.drawSlotOverlay = true;
                            break;
                        }

                        if (this.drawTooltipStack)
                            break;
                    }
                }
                for (int i = 0; i < page.blockList.size(); i++) {
                    BookBlocks bookBlock = ((BookBlocks) (page.blockList.toArray()[i]));

                    if (pageOn == PageOn.LEFT_PAGE) {

                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookBlock.x * 0.06f, 0.5f - bookBlock.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookBlock.x, bookBlock.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {


                            if (!bookBlock.blockState.is(Blocks.AIR)) {
                                List<Component> tooltipList = new ArrayList<>(bookBlock.extra_tooltips);
                                tooltipList.add(0, bookBlock.blockState.getBlock().getName().withStyle(ChatFormatting.WHITE));
                                this.tooltipText = tooltipList;
                                this.drawTooltipText = true;
                                this.tooltipStack = ItemStack.EMPTY;
                            }
                            this.slotOverlay.x = bookBlock.x;
                            this.slotOverlay.y = bookBlock.y;
                            ArrayList<BookImageEffect> effects = new ArrayList<>();
                            effects.add(new BookImageEffect("scale", 20, 1.1f));
                            this.slotOverlay.effects = effects;
                            this.slotOverlayPageOn = pageOn;
                            this.drawSlotOverlay = true;
                            break;
                        }
                        if (this.drawTooltipText)
                            break;
                    }
                    if (pageOn == PageOn.RIGHT_PAGE) {


                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f - bookBlock.x * 0.06f, 0.5f - bookBlock.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));


                        AABB aabb = new AABB(vec.add(-0.03f, -0.03f, -0.03f), vec.add(0.03f, 0.03f, 0.03f));

                        Vec3 intersectionVec = intersectPoint(bookBlock.x, bookBlock.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            if (!bookBlock.blockState.is(Blocks.AIR)) {
                                List<Component> tooltipList = new ArrayList<>(bookBlock.extra_tooltips);
                                tooltipList.add(0, bookBlock.blockState.getBlock().getName().withStyle(ChatFormatting.WHITE));
                                this.tooltipText = tooltipList;
                                this.drawTooltipText = true;
                                this.tooltipStack = ItemStack.EMPTY;
                            }
                            this.slotOverlay.x = bookBlock.x;
                            this.slotOverlay.y = bookBlock.y;
                            ArrayList<BookImageEffect> effects = new ArrayList<>();
                            effects.add(new BookImageEffect("scale", 20, 1.1f));
                            this.slotOverlay.effects = effects;
                            this.slotOverlayPageOn = pageOn;
                            this.drawSlotOverlay = true;
                            break;
                        }

                        if (this.drawTooltipText)
                            break;
                    }
                }

                for (int i = 0; i < page.entityList.size(); i++) {
                    BookEntity bookEntity = ((BookEntity) (page.entityList.toArray()[i]));

                    if (bookEntity.entity != null)
                        bookEntity.entity.tickCount = (int) Hexerei.getClientTicksWithoutPartial();

                    if (pageOn == PageOn.LEFT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - (bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                        Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 1, 0.04f);

                            MouseHandler handler = Minecraft.getInstance().mouseHandler;

                            if (handler.isRightPressed() && !this.isRightPressedOld && tileEntityIn.slotClicked == -1)
                                Hexerei.entityClicked = true;
                            if (Hexerei.entityClicked)
                                bookEntity.toRotate += (this.mouseXOld - handler.xpos());
                        } else {
                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 0, 0.08f);
                        }


                        if (bookEntity.hoverTick > 0) {
                            MouseHandler handler = Minecraft.getInstance().mouseHandler;

                            BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64, 32, 64, 32, 0.75f * bookEntity.hoverTick, "hexerei:textures/book/rotate_entity.png", new ArrayList<>());
                            drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            if (handler.isRightPressed()) {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTick, "hexerei:textures/book/right_click_icon_hover.png", new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            } else {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTick, "hexerei:textures/book/right_click_icon.png", new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                        }


                        if (bookEntity.toRotate != 0) {
                            if (bookEntity.toRotate > 0) {
                                bookEntity.rot += Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            } else {
                                bookEntity.rot -= Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            }
                            bookEntity.toRotate = moveTo(bookEntity.toRotate, 0, Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f));
                        }
                    } else if (pageOn == PageOn.RIGHT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f + -(bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                        Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);

                        MouseHandler handler = Minecraft.getInstance().mouseHandler;

                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 1, 0.04f);

                            if (handler.isRightPressed() && !this.isRightPressedOld && tileEntityIn.slotClicked == -1)
                                Hexerei.entityClicked = true;
                            if (Hexerei.entityClicked)
                                bookEntity.toRotate += (this.mouseXOld - handler.xpos());
                        } else {
                            bookEntity.hoverTick = moveTo(bookEntity.hoverTick, 0, 0.08f);
                        }

                        if (bookEntity.hoverTick > 0) {

                            BookImage bookImage = new BookImage(bookEntity.x, bookEntity.y + 0.5f, 0, 0, 0, 64, 32, 64, 32, 0.75f * bookEntity.hoverTick, "hexerei:textures/book/rotate_entity.png", new ArrayList<>());
                            drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            if (handler.isRightPressed()) {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTick, "hexerei:textures/book/right_click_icon_hover.png", new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            } else {
                                BookImage bookImage2 = new BookImage(bookEntity.x - (bookEntity.toRotate > 0 ? Math.min(bookEntity.toRotate / 2000f, 0.8f) : Math.max(bookEntity.toRotate / 2000f, -0.8f)), bookEntity.y + 0.85f - (Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f) * Math.min(Math.abs(bookEntity.toRotate) / 4000f, 0.4f)) * 2.25f, 1, 0, 0, 32, 48, 32, 48, 0.45f * bookEntity.hoverTick, "hexerei:textures/book/right_click_icon.png", new ArrayList<>());
                                drawImage(bookImage2, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                            }
                        }

                        if (bookEntity.toRotate != 0) {
                            if (bookEntity.toRotate > 0) {
                                bookEntity.rot += Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            } else {
                                bookEntity.rot -= Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f) / 3f;
                            }
                            bookEntity.toRotate = moveTo(bookEntity.toRotate, 0, Math.max(Math.abs(bookEntity.toRotate) / 100f, 0.01f));
                        }
                    }
                }


                for (int i = 0; i < page.nonItemTooltipList.size(); i++) {
                    BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page.nonItemTooltipList.toArray()[i]));

                    if (pageOn == PageOn.LEFT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                        Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                            this.tooltipText = bookNonItemTooltip.textComponentsList;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }
                        if (this.drawTooltipText)
                            break;
                    } else if (pageOn == PageOn.RIGHT_PAGE) {
                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(-0.05f + -bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                        BlockPos blockPos = tileEntityIn.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                        Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            List<Component> components = new ArrayList<>();
                            this.tooltipText = bookNonItemTooltip.textComponentsList;
                            this.tooltipStack = ItemStack.EMPTY;
//                        if (Minecraft.getInstance().mouseHandler.isRightPressed() && !this.isRightPressedOld) {
//                            System.out.println("clicked: " + this.tooltipText.get(0).getString());
//                        }
                            this.drawTooltipText = true;
                        }
                        if (this.drawTooltipText)
                            break;
                    }
                }


            }
            for (int i = 0; i < page.imageList.size(); i++) {
                BookImage bookImage = ((BookImage) (page.imageList.toArray()[i]));
                drawImage(bookImage, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, pageOn, isItem);

                if (bookImage.extra_tooltips == null || bookImage.extra_tooltips.size() < 1)
                    continue;

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        this.tooltipText = bookImage.extra_tooltips;
                        this.tooltipStack = ItemStack.EMPTY;
                        this.drawTooltipText = true;
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        this.tooltipText = bookImage.extra_tooltips;
                        this.tooltipStack = ItemStack.EMPTY;
                        this.drawTooltipText = true;
                    }
                }


            }


            //drawing slot overlay
            if (this.drawSlotOverlay)
                drawImage(this.slotOverlay, tileEntityIn, matrixStackIn, bufferIn, 0, combinedLightIn, combinedOverlayIn, this.slotOverlayPageOn, isItem);

            if (page.showTitle.equals("Hexerei"))
                drawTitle(tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, pageOn, isItem);

            for (int i = 0; i < page.entityList.size(); i++) {
                BookEntity bookEntity = ((BookEntity) (page.entityList.toArray()[i]));
                if (bookEntity.entity instanceof LivingEntity livingEntity) {
                    if (bookEntity.entityTagsList.size() > 0 && tileEntityIn.tickCount > bookEntity.entityTagsLastChange + 40) {
                        bookEntity.entityTagsLastChange = (int) tileEntityIn.tickCount;
                        bookEntity.entityTagsListOn++;
                        if (bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                            bookEntity.entityTagsListOn = 0;
                        int on = bookEntity.entityTagsListOn;

                        if (bookEntity.entityTagsListOnSet != bookEntity.entityTagsListOn && !bookEntity.entityTagsList.get(on).equals("")) {

                            CompoundTag tag = TagParser.parseTag(bookEntity.entityTagsList.get(on));

                            String tag2 = tag.getString("CustomName");

                            CompoundTag stringTag = TagParser.parseTag(tag2);

                            livingEntity.setCustomName(Component.translatable(stringTag.getString("text")));

                            livingEntity.load(tag);

                            if (livingEntity instanceof TamableAnimal)
                                ((TamableAnimal) livingEntity).setTame(true, false);

                            bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;

                            if (livingEntity instanceof TamableAnimal tamableAnimal) {
                                tamableAnimal.setInSittingPose(true);
                                tamableAnimal.setOrderedToSit(true);
                                tamableAnimal.setOnGround(true);
                                tamableAnimal.tick();

                                if (tamableAnimal instanceof CrowEntity crowEntity) {
                                    crowEntity.setCommandSit();
                                    crowEntity.tick();
                                }
                                bookEntity.entity = tamableAnimal;
                            }
                        }
                    }
                    drawLivingEntity(tileEntityIn, matrixStackIn, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.rot, 20, (float) (107), (float) (88 - 30), livingEntity, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                } else if (bookEntity.entity != null) {
                    if (bookEntity.entityTagsList.size() > 0 && tileEntityIn.tickCount > bookEntity.entityTagsLastChange + 40) {
                        bookEntity.entityTagsLastChange = (int) tileEntityIn.tickCount;
                        bookEntity.entityTagsListOn++;
                        if (bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                            bookEntity.entityTagsListOn = 0;
                        int on = bookEntity.entityTagsListOn;
                        if (bookEntity.entityTagsListOnSet != on && !bookEntity.entityTagsList.get(on).equals("")) {

                            CompoundTag tag = TagParser.parseTag(bookEntity.entityTagsList.get(on));

                            String tag2 = tag.getString("CustomName");

                            CompoundTag stringTag = TagParser.parseTag(tag2);

                            bookEntity.entity.setCustomName(Component.translatable(stringTag.getString("text")));

                            bookEntity.entity.load(tag);

                            bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;

                        }
                    }
                    drawEntity(tileEntityIn, matrixStackIn, bufferIn, bookEntity.scale, bookEntity.x, bookEntity.y, bookEntity.rot, 20, (float) (107), (float) (88 - 30), bookEntity.entity, combinedLightIn, combinedOverlayIn, pageOn, isItem);
                } else {
                    Optional<EntityType<?>> optionalEntityType = EntityType.byString(bookEntity.entityType);
                    if (optionalEntityType.isPresent()) {
                        Entity entity = optionalEntityType.get().create(Hexerei.proxy.getLevel());

                        if (entity instanceof LivingEntity livingEntity) {
                            bookEntity.entity = entity;


                            if (!bookEntity.entityTags.equals("") && entity != null) {

                                CompoundTag tag = TagParser.parseTag(bookEntity.entityTags);

                                String tag2 = tag.getString("CustomName");

                                CompoundTag stringTag = TagParser.parseTag(tag2);

                                livingEntity.setCustomName(Component.translatable(stringTag.getString("text")));

                                livingEntity.readAdditionalSaveData(tag);

                                if (livingEntity instanceof TamableAnimal)
                                    ((TamableAnimal) livingEntity).setTame(true, false);

                            }

                            if (livingEntity instanceof TamableAnimal tamableAnimal) {
                                tamableAnimal.setInSittingPose(true);
                                tamableAnimal.setOrderedToSit(true);
                                tamableAnimal.setOnGround(true);
                                tamableAnimal.tick();

                                if (tamableAnimal instanceof CrowEntity crowEntity) {
                                    crowEntity.setCommandSit();
                                    crowEntity.tick();
                                }
                                bookEntity.entity = tamableAnimal;
                            }
                        } else {
                            bookEntity.entity = entity;


                            if (!bookEntity.entityTags.equals("") && entity != null) {

                                CompoundTag tag = TagParser.parseTag(bookEntity.entityTags);

                                String tag2 = tag.getString("CustomName");

                                CompoundTag stringTag = TagParser.parseTag(tag2);

                                entity.setCustomName(Component.translatable(stringTag.getString("text")));

                                entity.load(tag);

                            }
                        }
                    }
                }
            }
        }


    }

    public void drawLivingEntity(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, LivingEntity livingEntity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem) {
        matrixStackIn.pushPose();

        if (livingEntity instanceof TamableAnimal tamableAnimal && !tamableAnimal.isInSittingPose()) {
            tamableAnimal.setInSittingPose(true);
            tamableAnimal.setOnGround(true);
        }

//        livingEntity.tickCount += 1;

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);

        matrixStackIn.translate(-1f / 512f, 0, 0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        matrixStackIn.scale(0.049f * scale, 0.049f * scale, 0.003f);
        matrixStackIn.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90));

        float $$6 = (float) Math.atan(p_98854_ / 40.0F);
        float $$7 = (float) Math.atan(p_98855_ / 40.0F);
        Quaternionf $$10 = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf $$11 = Axis.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        float $$12 = livingEntity.yBodyRot;
        float $$13 = livingEntity.getYRot();
        float $$15 = livingEntity.yHeadRotO;
        float $$16 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = rot * 0.60F + livingEntity.getId();
        livingEntity.setYRot(rot * 0.60F + livingEntity.getId());
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conjugate();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStackIn, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        livingEntity.yBodyRot = $$12;
        livingEntity.setYRot($$13);
        livingEntity.yHeadRotO = $$15;
        livingEntity.yHeadRot = $$16;
        matrixStackIn.popPose();
    }

    public void drawEntity(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, float scale, float xIn, float yIn, float rot, int p_98853_, float p_98854_, float p_98855_, Entity entity, int combinedLightIn, int combinedOverlayIn, PageOn pageOn, boolean isItem) {
        matrixStackIn.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStackIn, isItem, ItemDisplayContext.NONE);

        matrixStackIn.translate(-1f / 512f, 0, 0);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        matrixStackIn.translate(-8f / 16f, 5.5f / 16f, -0.04f / 16f);
        matrixStackIn.scale(0.049f * scale, 0.049f * scale, 0.003f);
        matrixStackIn.translate(yIn * 1.25f / scale, -xIn * 1.25f / scale, 0);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90));

        float $$7 = (float) Math.atan(p_98855_ / 40.0F);
        Quaternionf $$10 = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf $$11 = Axis.XP.rotationDegrees($$7 * 20.0F);
        $$10.mul($$11);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-(rot * 0.60F + entity.getId())));
        EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
        $$11.conjugate();
        $$17.overrideCameraOrientation($$11);
        $$17.setRenderShadow(false);
        MultiBufferSource.BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            $$17.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStackIn, $$18, combinedLightIn);
        });
        $$18.endBatch();
        $$17.setRenderShadow(true);
        matrixStackIn.popPose();
    }

    public void drawPages(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, float partialTicks) throws CommandSyntaxException {
        drawPages(tileEntityIn, matrixStack, bufferSource, light, overlay, false, ItemDisplayContext.NONE, partialTicks);
    }

    public void drawTooltips(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, float partialTicks) throws CommandSyntaxException {
        this.drawTooltip = tileEntityIn.turnPage == 0;

        this.drawTooltipScale = Mth.lerp(partialTicks, tileEntityIn.tooltipScaleOld, tileEntityIn.tooltipScale);
        this.drawTooltipScaleOld = this.drawTooltipScale;
        if (this.drawTooltipStack && tileEntityIn.turnPage == 0) {
            tileEntityIn.drawTooltip = true;
            this.drawTooltipStackFlag = true;
            this.drawTooltipTextFlag = false;
        } else if (this.drawTooltipText && tileEntityIn.turnPage == 0) {
            tileEntityIn.drawTooltip = true;
            this.drawTooltipTextFlag = true;
            this.drawTooltipStackFlag = false;
        } else {
            tileEntityIn.drawTooltip = false;
            if (this.drawTooltipScale == 0) {
                this.drawTooltipStackFlag = false;
                this.drawTooltipTextFlag = false;
            }
        }

        if (this.drawTooltipScale > 0) {
            if (this.drawTooltipStackFlag)
                drawTooltipImage(this.tooltipStack, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, false);
            else
                drawTooltipText(tileEntityIn, matrixStack, bufferSource, 0, light, overlay, false);
        }
    }

    public void drawPages(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, boolean isItem, ItemDisplayContext transformType, float partialTicks) throws CommandSyntaxException {
        this.tick++;

        if (ClientProxy.keys == null)
            ClientProxy.keys = Minecraft.getInstance().options.keyMappings;

        this.drawSlotOverlay = false;
        this.drawTooltipStack = false;
        this.drawTooltipText = false;
        BookEntries bookEntries = BookManager.getBookEntries();

        if (bookEntries == null)
            return;

        ItemStack stack = tileEntityIn.itemHandler.getStackInSlot(0);

        BookData bookData = stack.get(ModDataComponents.BOOK);

        tileEntityIn.pageOneRotationRender = Mth.lerp(partialTicks, tileEntityIn.pageOneRotationLast, tileEntityIn.pageOneRotation);
        tileEntityIn.pageTwoRotationRender = Mth.lerp(partialTicks, tileEntityIn.pageTwoRotationLast, tileEntityIn.pageTwoRotation);

        String location1 = "";
        String location2 = "";
        String location1_back = "";
        String location2_back = "";
        String location1_next = "";
        String location2_next = "";
        int location1_p = 0;
        int location2_p = 0;
        int location1_back_p = 0;
        int location2_back_p = 0;
        int location1_next_p = 0;
        int location2_next_p = 0;
        int chapter = 0;
        int page = 0;
        if (bookData != null) {
            chapter = bookData.getChapter();
            page = bookData.getPage();
            if (page % 2 == 1)
                page--;

            if (page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0) {
                BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page);
                location1 = pageEntry.location;
                location1_p = pageEntry.pageNum;
            }
            if (bookEntries.chapterList.get(chapter).pages.size() > page + 1) {
                BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page + 1);
                location2 = pageEntry.location;
                location2_p = pageEntry.pageNum;
            }


            int next_page_chapter = chapter;
            int next_page_page = page;
            int back_page_chapter = chapter;
            int back_page_page = page;
            if (next_page_page < BookManager.getBookEntries().chapterList.get(chapter).pages.size() - 2)
                next_page_page += 2;
            else if (chapter < BookManager.getBookEntries().chapterList.size() - 1) {
                next_page_chapter++;
                next_page_page = 0;
            } else
                next_page_chapter = -1;

            if (next_page_chapter != -1 && next_page_chapter < bookEntries.chapterList.size() && next_page_page < bookEntries.chapterList.get(next_page_chapter).pages.size()) {

                BookPageEntry pageEntry = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page);
                location1_next = pageEntry.location;
                location1_next_p = pageEntry.pageNum;
                if (bookEntries.chapterList.get(next_page_chapter).pages.size() > next_page_page + 1) {
                    BookPageEntry pageEntry2 = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page + 1);
                    location2_next = pageEntry2.location;
                    location2_next_p = pageEntry2.pageNum;
                }
            }


            if (back_page_page - 2 >= 0)
                back_page_page -= 2;
            else if (back_page_chapter > 0) {
                back_page_chapter--;
                back_page_page = BookManager.getBookEntries().chapterList.get(back_page_chapter).pages.size() - 1;
                if (back_page_page % 2 == 1)
                    back_page_page--;
            } else
                back_page_chapter = -1;

            if (back_page_chapter != -1 && back_page_chapter < bookEntries.chapterList.size() && back_page_page < bookEntries.chapterList.get(back_page_chapter).pages.size()) {

                BookPageEntry pageEntry = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page);
                location1_back = pageEntry.location;
                location1_back_p = pageEntry.pageNum;
                if (bookEntries.chapterList.get(back_page_chapter).pages.size() > back_page_page + 1) {
                    BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page + 1);
                    location2_back = pageEntry2.location;
                    location2_back_p = pageEntry2.pageNum;
                }
            }

        }
//
        if (transformType != ItemDisplayContext.GUI) {

            BookPage page1 = BookManager.getBookPages(ResourceLocation.parse(location1));
            BookPage page2 = BookManager.getBookPages(ResourceLocation.parse(location2));
            drawPage(page1, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, isItem, transformType, location1_p);
            drawPage(page2, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, isItem, transformType, location2_p);
            BookPage page1_under = BookManager.getBookPages(ResourceLocation.parse(location2_back));
            BookPage page1_prev = BookManager.getBookPages(ResourceLocation.parse(location1_back));
            BookPage page2_under = BookManager.getBookPages(ResourceLocation.parse(location1_next));
            BookPage page2_prev = BookManager.getBookPages(ResourceLocation.parse(location2_next));
            drawPage(page1_under, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_UNDER, isItem, transformType, location2_back_p);
            drawPage(page2_under, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_UNDER, isItem, transformType, location1_next_p);
            drawPage(page1_prev, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE_PREV, isItem, transformType, location1_back_p);
            drawPage(page2_prev, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE_PREV, isItem, transformType, location2_next_p);
        } else {

            BookPage page1 = BookManager.getBookPages(ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
            BookPage page2 = BookManager.getBookPages(ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
            drawPage(page1, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.LEFT_PAGE, isItem, transformType, location1_p);
            drawPage(page2, tileEntityIn, matrixStack, bufferSource, light, overlay, PageOn.RIGHT_PAGE, isItem, transformType, location2_p);
        }

        drawBaseButtons(tileEntityIn, matrixStack, bufferSource, light, overlay, !location1_next.equals(""), !location1_back.equals(""), chapter, page, isItem);

        MouseHandler handler = Minecraft.getInstance().mouseHandler;




        this.mouseXOld = handler.xpos();
        this.mouseYOld = handler.ypos();

        this.isRightPressedOld = handler.isRightPressed();
        this.isLeftPressedOld = handler.isLeftPressed();


    }

    public void drawBaseButtons(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, boolean isItem) {
        drawBaseButtons(tileEntityIn, matrixStack, bufferSource, light, overlay, drawNext, drawBack, chapter, page, isItem, ItemDisplayContext.NONE, false);
    }

    public void drawBaseButtons(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, boolean drawNext, boolean drawBack, int chapter, int page, boolean isItem, ItemDisplayContext transformType, boolean fullyExtended) {

        Player playerIn = null;
        if (tileEntityIn.getLevel() != null && tileEntityIn.getLevel().isClientSide)
            playerIn = Hexerei.proxy.getPlayer();
        if (playerIn != null) {

            boolean drawBookmarkButton = chapter != 0;

            double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
            Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);
            ItemStack stack = tileEntityIn.itemHandler.getStackInSlot(0);
            BookData bookData = stack.get(ModDataComponents.BOOK);

            if (drawBookmarkButton && !isItem) {
                Vector3f vector3f = new Vector3f(0, 0, 0);
                Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.064f, 0.5f - -1f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
                vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                BookImageEffect bookImageEffect_hover_overlay = new BookImageEffect("hover_overlay", 35, 10f, new BookImage(-0.5f, -1f, -1, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, "hexerei:textures/book/bookmark_button_hover.png", effects));


                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;

                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    effects.add(bookImageEffect_hover_overlay);
                }


                if (bookData != null) {

                    DyeColor bookmark_color = DyeColor.WHITE;
                    int bookmark_chapter = 0;
                    int bookmark_page = 0;
                    String bookmark_id = "";
                    boolean flag2 = false;

                    for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                            boolean flag3 = false;
                            if (!slot.getId().isEmpty()) {
                                bookmark_color = slot.getColor();
                                bookmark_id = slot.getId();
                                for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                    for (BookPageEntry pageEntry : chapterEntry.pages) {
                                        if (pageEntry.location.equals(bookmark_id)) {
                                            bookmark_chapter = pageEntry.chapterNum;
                                            bookmark_page = pageEntry.chapterPageNum;
                                            flag3 = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (flag3) {
                                if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
                                    flag2 = true;
                                    break;
                                }
                            }

                    }
                    // draw bookmark button
                    if (flag2) {


                        if (flag) {
                            List<Component> list = new ArrayList<>();
                            DyeColor col = bookmark_color;

                            String output = col.getName().substring(0, 1).toUpperCase() + col.getName().substring(1);
                            output = output.replaceAll("_", " ");

                            list.add(Component.translatable("Change Color - %s", Component.translatable("%s", output).withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col)))).withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }


                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_underlay.png", effects);
                        BookImage bookImage_overlay = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button_overlay.png", effects);

                        drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
                        drawImage(bookImage_overlay, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);

                    } else {

                        if (flag) {
                            List<Component> list = new ArrayList<>();

                            list.add(Component.translatable("Bookmark Page").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                            this.tooltipText = list;
                            this.tooltipStack = ItemStack.EMPTY;
                            this.drawTooltipText = true;
                        }

                        BookImage bookImage = new BookImage(-0.5f, -1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2 * 1.15f, "hexerei:textures/book/bookmark_button.png", effects);

                        drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
                    }

                    //draw bookmarks


                }
            }
            if (bookData != null) {

                DyeColor bookmark_color = DyeColor.WHITE;
                int bookmark_chapter = 0;
                int bookmark_page = 0;
                ResourceLocation bookmark_id;
                for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                    boolean flag2 = false;
                    if (!slot.getId().isEmpty()) {


                        bookmark_color = slot.getColor();
//                        bookmark_chapter = slot.getInt("chapter");
//                        bookmark_page = slot.getInt("page");
                        if (!slot.getId().isEmpty())
                            bookmark_id = ResourceLocation.parse(slot.getId());
                        else
                            bookmark_id = null;

                        boolean flag3 = false;
                        if (bookmark_id != null) {
                            for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                for (BookPageEntry pageEntry : chapterEntry.pages) {
                                    if (ResourceLocation.parse(pageEntry.location).equals(bookmark_id)) {
                                        bookmark_chapter = pageEntry.chapterNum;
                                        bookmark_page = pageEntry.chapterPageNum;
                                        flag3 = true;
                                        break;
                                    }
                                }
                            }
                        }


                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();

                        if (slot.getIndex() < 5) {

                            float xIn = -0.4f - tileEntityIn.buttonScaleRender - 0.15f;
                            float yIn = slot.getIndex() * 1.5f;
                            if (fullyExtended) {
                                xIn = -1.55f + 0.5f;
                                yIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 1, 0.1f);
                                flag2 = true;

                            }

                            float bookX = xIn + 0.4f - tileEntityIn.bookmarkHoverAmount[slot.getIndex()] / 3 * tileEntityIn.buttonScaleRender;
                            if (fullyExtended)
                                bookX = xIn + 0.4f - 0.33f;

                            if (flag2) {
                                List<Component> list = new ArrayList<>();
                                DyeColor col = bookmark_color;

                                BookEntries bookEntries = BookManager.getBookEntries();


                                if (flag3) {
                                    list.add(Component.translatable("%s%s - Page %s%s",
                                                    Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                                    Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("%s", bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 90, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                        }
                        if (slot.getIndex() >= 5 && slot.getIndex() < 10) {


                            float yIn = -0.95f - tileEntityIn.buttonScaleRender - 0.25f;
                            float xIn = -5.5f + slot.getIndex() * 1.15f;
                            if (fullyExtended) {
                                yIn = -2.15f + 0.65f;
                                xIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 1, 0.1f);
                                flag2 = true;

                            }


                            float bookY = yIn + 0.5f - tileEntityIn.bookmarkHoverAmount[slot.getIndex()] / 3 * tileEntityIn.buttonScaleRender;
                            if (fullyExtended)
                                bookY = yIn + 0.5f - 0.33f;

                            if (flag2) {
                                List<Component> list = new ArrayList<>();
                                DyeColor col = bookmark_color;

                                BookEntries bookEntries = BookManager.getBookEntries();

                                if (bookEntries != null) {
                                    list.add(Component.translatable("%s%s - Page %s%s",
                                                    Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.LEFT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                        }
                        if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                            float yIn = -0.95f - tileEntityIn.buttonScaleRender - 0.25f;
                            float xIn = -11.25f + slot.getIndex() * 1.15f;
                            if (fullyExtended) {
                                yIn = -2.15f + 0.65f;
                                xIn += 0.25f;
                            }
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 1, 0.1f);
                                flag2 = true;

                            }


                            if (flag2) {
                                List<Component> list = new ArrayList<>();
                                DyeColor col = bookmark_color;

                                BookEntries bookEntries = BookManager.getBookEntries();

                                if (bookEntries != null) {
                                    list.add(Component.translatable("%s%s - Page %s%s",
                                                    Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }


                            float bookY = yIn + 0.5f - tileEntityIn.bookmarkHoverAmount[slot.getIndex()] / 3 * tileEntityIn.buttonScaleRender;
                            if (fullyExtended)
                                bookY = yIn + 0.5f - 0.33f;

                            BookImage bookImageUnderlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(xIn, bookY, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, 0, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                        }
                        if (slot.getIndex() >= 15) {

                            float xIn = 5.5f + tileEntityIn.buttonScaleRender;
                            float yIn = (slot.getIndex() - 15) * 1.5f;
                            if (fullyExtended) {
                                xIn = 6.65f;
                                yIn -= 0.25f;
                            }
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - tileEntityIn.buttonScaleRender/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 1, 0.1f);
                                flag2 = true;

                            }

                            float bookX = xIn - 0.4f + tileEntityIn.bookmarkHoverAmount[slot.getIndex()] / 3 * tileEntityIn.buttonScaleRender;
                            if (fullyExtended)
                                bookX = xIn - 0.4f - 0.33f;

                            if (flag2) {
                                List<Component> list = new ArrayList<>();
                                DyeColor col = bookmark_color;

                                BookEntries bookEntries = BookManager.getBookEntries();


                                if (bookEntries != null) {
                                    list.add(Component.translatable("%s%s - Page %s%s",
                                                    Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).name).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("%s", bookEntries.chapterList.get(bookmark_chapter).pages.get(bookmark_page).pageNum).withStyle(Style.EMPTY.withColor(10329495)),
                                                    Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(col))))
                                            .withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                                    this.tooltipText = list;
                                    this.tooltipStack = ItemStack.EMPTY;
                                    this.drawTooltipText = true;
                                }
                            }

                            BookImage bookImageUnderlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark);
                            BookImage bookImageOverlay = new BookImage(bookX, yIn, 0, 0, 0, 64, 48, 64, 48, 0.5f, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark);

                            drawBookmark(bookImageUnderlay, tileEntityIn, matrixStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                            drawBookmark(bookImageOverlay, tileEntityIn, matrixStack, bufferSource, -10, -90, light, overlay, PageOn.RIGHT_PAGE, HexereiUtil.getColorValue(bookmark_color), isItem, transformType);
                        }


                        if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
                            tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 1, 0.1f);
                        }


                    }

                    if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
//                        tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                    } else if (tileEntityIn.bookmarkHoverAmount[slot.getIndex()] > 0 && !flag2)
                        tileEntityIn.bookmarkHoverAmount[slot.getIndex()] = moveTo(tileEntityIn.bookmarkHoverAmount[slot.getIndex()], 0, 0.05f);
                }


                //send to server to update the slotClicked

                if (tileEntityIn.slotClicked != -1) {
                    for (int i = 0; i < 20; i++) {

                        if (i == tileEntityIn.slotClicked)
                            continue;

                        boolean flag2 = false;

                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                        if (i < 5) {

                            float xIn = -1.4f;
                            float yIn = i * 1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 90, light, overlay, PageOn.LEFT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 5 && i < 10) {


                            float xIn = -5.5f + i * 1.15f;
                            float yIn = -1.95f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 0, light, overlay, PageOn.LEFT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 10 && i < 15) {

                            float xIn = -11.25f + i * 1.15f;
                            float yIn = -1.95f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, 0, light, overlay, PageOn.RIGHT_PAGE, -1, isItem, transformType);
                        }
                        if (i >= 15) {

                            float xIn = 6.5f;
                            float yIn = (i - 15) * 1.5f;
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - tileEntityIn.buttonScaleRender/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = tileEntityIn.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.04, -0.04, -0.04), vec.add(0.04, 0.04, 0.04));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                                tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                                flag2 = true;
                                effectsBookmark.add(new BookImageEffect("scale", 50, 1.15f));
                                effectsBookmark.add(new BookImageEffect("tilt", 35, 10f));
                            }

                            BookImage bookSelector = new BookImage(xIn, yIn, 0, 0, 0, 64, 64, 64, 64, 0.5f * tileEntityIn.bookmarkSelectorScale, "hexerei:textures/book/bookmark_selector.png", effectsBookmark);

                            drawBookmark(bookSelector, tileEntityIn, matrixStack, bufferSource, 1, -90, light, overlay, PageOn.RIGHT_PAGE, -1, isItem, transformType);
                        }


                        if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 1, 0.1f);
                        } else if (tileEntityIn.bookmarkHoverAmount[i] > 0 && !flag2) {
//                            tileEntityIn.bookmarkHoverAmount[i] = moveTo(tileEntityIn.bookmarkHoverAmount[i], 0, 0.05f);
                        }

                    }
                }
            }


            if (!isItem) {
                Vector3f vector3f = new Vector3f(0, 0, 0);
                Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.064f, 0.5f - 7.25f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
                vector3f_1.rotate(Axis.YP.rotationDegrees((10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);

                String loc = "hexerei:textures/book/font_button.png";
                if (drawBack)
                    loc = "hexerei:textures/book/back_page.png";

                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.LEFT_PAGE);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    List<Component> list = new ArrayList<>();
                    if (drawBack) {
                        list.add(Component.translatable("Back").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));

                        loc = "hexerei:textures/book/back_page_hover.png";
                    } else {
                        list.add(Component.translatable("Change Font").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));

                        loc = "hexerei:textures/book/font_button_hover.png";
                    }

                    this.tooltipText = list;
                    this.tooltipStack = ItemStack.EMPTY;
                    this.drawTooltipText = true;
                }

                BookImage bookImage = new BookImage(-0.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2, loc, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.LEFT_PAGE, isItem);
            }


            if (!isItem) {
                Vector3f vector3f = new Vector3f(0, 0, 0);
                //back position
                Vector3f vector3f_1 = new Vector3f(0, 0.5f - 7f * 0.061f, -0.03f);

                BlockPos blockPos = tileEntityIn.getBlockPos();
//                vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                ArrayList<BookImageEffect> effects = new ArrayList<>();
                BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc_close = "hexerei:textures/book/close.png";
                String loc_del = "hexerei:textures/book/delete.png";

                boolean flag = false;

                Vec3 intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.MIDDLE_BUTTON);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);

                    if (tileEntityIn.slotClicked != -1 && tileEntityIn.slotClickedTick > 5) {
                        loc_del = "hexerei:textures/book/delete_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Delete Bookmark").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    } else {
                        loc_close = "hexerei:textures/book/close_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Close Book").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }
                }
                BookImage bookImage;
                if (tileEntityIn.slotClicked != -1 && tileEntityIn.slotClickedTick > 5)
                    bookImage = new BookImage(0, 0, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.bookmarkSelectorScale / 1.5f, loc_del, effects);
                else
                    bookImage = new BookImage(0, 0, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, loc_close, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, isItem);


                vector3f = new Vector3f(0, 0, 0);
                vector3f_1 = new Vector3f(0, 0.5f - -1f * 0.061f, -0.03f);

                blockPos = tileEntityIn.getBlockPos();
                vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));
                vector3f.add(vector3f_1);
                vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                        vector3f.y() + blockPos.getY() + 18 / 16f,
                        vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                effects = new ArrayList<>();
                bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                String loc = "hexerei:textures/book/home.png";

                flag = false;
                intersectionVec = intersectPoint(0, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, PageOn.MIDDLE_BUTTON);
                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                    flag = true;
                }
                if (flag) {
                    effects.add(bookImageEffect_scale);
                    effects.add(bookImageEffect_tilt);
                    loc = "hexerei:textures/book/home_hover.png";
                    List<Component> list = new ArrayList<>();
                    list.add(Component.translatable("Home").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                    this.tooltipText = list;
                    this.drawTooltipText = true;
                    this.tooltipStack = ItemStack.EMPTY;
                }

                bookImage = new BookImage(0, -8.1f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2f, loc, effects);

                drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.MIDDLE_BUTTON, isItem);


                if (drawNext) {
                    vector3f = new Vector3f(0, 0, 0);
                    vector3f_1 = new Vector3f(-0.05f + -5.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);

                    blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                    effects = new ArrayList<>();
                    bookImageEffect_scale = new BookImageEffect("scale", 50, 1.15f);
                    bookImageEffect_tilt = new BookImageEffect("tilt", 35, 10f);
                    loc = "hexerei:textures/book/next_page.png";


                    flag = false;
                    intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, PageOn.RIGHT_PAGE);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag = true;
                    }
                    if (flag) {
                        effects.add(bookImageEffect_scale);
                        effects.add(bookImageEffect_tilt);
                        loc = "hexerei:textures/book/next_page_hover.png";
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("Next").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                        this.tooltipText = list;
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                    }


                    bookImage = new BookImage(5.5f, 7.25f, 0, 0, 0, 32, 32, 32, 32, tileEntityIn.buttonScaleRender / 2, loc, effects);

                    drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, 0, light, overlay, PageOn.RIGHT_PAGE, isItem);
                }
            }
        }
    }

    private float moveTo(float input, float moveTo, float speed) {
        float distance = moveTo - input;

        if (Math.abs(distance) <= speed) {
            return moveTo;
        }

        if (distance > 0) {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }

    public float getAngle(Vec3 pos, BlockEntity blockEntity) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - blockEntity.getBlockPos().getZ() - 0.5f, pos.x() - blockEntity.getBlockPos().getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public float moveToAngle(float input, float movedTo, float speed) {
        float distance = movedTo - input;

        if (Math.abs(distance) <= speed) {
            return movedTo;
        }

        if (distance > 0) {
            if (Math.abs(distance) < 180)
                input += speed;
            else
                input -= speed;
        } else {
            if (Math.abs(distance) < 180)
                input -= speed;
            else
                input += speed;
        }

        if (input < -90) {
            input += 360;
        }
        if (input > 270)
            input -= 360;

        return input;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawItemInSlot(BookOfShadowsAltarTile tileEntityIn, BookItemsAndFluids bookItemStackInSlot, PoseStack matrixStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {
        if (bookItemStackInSlot.type.equals("item") || bookItemStackInSlot.type.equals("tag")) {
            if (bookItemStackInSlot.show_slot)
                drawSlot(tileEntityIn, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
            renderItem(tileEntityIn, bookItemStackInSlot, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
        } else if (bookItemStackInSlot.type.equals("fluid")) {
            drawFluidInSlot(tileEntityIn, bookItemStackInSlot, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, isItem);
        }

    }

    public void drawBlock(BookOfShadowsAltarTile tileEntityIn, BookBlocks bookItemStackInSlot, PoseStack matrixStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn) {
        if (bookItemStackInSlot.type.equals("block") || bookItemStackInSlot.type.equals("tag")) {
            if (bookItemStackInSlot.show_slot)
                drawSlot(tileEntityIn, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn, false);
            renderBlock(tileEntityIn, bookItemStackInSlot, matrixStack, bufferSource, xIn, yIn, 0, light, overlay, pageOn);
        }

    }

    public AABB getpositionAABBNext(BookOfShadowsAltarTile altarTile) {

        BlockPos blockPos = altarTile.getBlockPos();
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(-0.05f + -5.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);
        vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

        return new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
    }

    public AABB getpositionAABBBack(BookOfShadowsAltarTile altarTile) {
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.06f, 0.5f - 7.25f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();

        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

        vector3f.add(vector3f_1);

        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        return new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
    }

    public AABB getpositionAABBLeft(BookOfShadowsAltarTile altarTile, float xIn, float yIn) {
        Vector3f vector3f = new Vector3f(0, 0, 0);
        Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();

        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

        vector3f.add(vector3f_1);

        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        return new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
    }

    public AABB getpositionAABBClose(BookOfShadowsAltarTile altarTile) {
        Vector3f vector3f = new Vector3f(0, 0, 0);
        //back position
        Vector3f vector3f_1 = new Vector3f(0, 0.5f - 7f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();
//                vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        return new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
    }

    public AABB getpositionAABBHome(BookOfShadowsAltarTile altarTile) {
        Vector3f vector3f = new Vector3f(0, 0, 0);
        //back position
        Vector3f vector3f_1 = new Vector3f(0, 0.5f - -1f * 0.061f, -0.03f);

        BlockPos blockPos = altarTile.getBlockPos();
//                vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
        vector3f.add(vector3f_1);
        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                vector3f.y() + blockPos.getY() + 18 / 16f,
                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


        return new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));
    }


    protected static BlockHitResult getPlayerPOVHitResult(int i, Level level, Player player, ClipContext.Fluid p_41438_) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();

        if (i == 1) {
            vec3 = vec3.subtract(0, 1, 0);
        }
        if (i == 2) {
            vec3 = vec3.subtract(0.25, 1, 0);
        }
        if (i == 3) {
            vec3 = vec3.subtract(-0.25, 1, 0);
        }
        if (i == 4) {
            vec3 = vec3.subtract(0, 1, 0.25);
        }
        if (i == 5) {
            vec3 = vec3.subtract(0, 1, -0.25);
        }

        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, (Entity)null));
    }


    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        this.drawTooltipScaleOld = this.drawTooltipScale;
        if (this.drawTooltipStack && this.drawTooltip) {
            this.drawTooltipStackFlag = true;
            this.drawTooltipTextFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1f, 0.1f);
        } else if (this.drawTooltipText && this.drawTooltip) {
            this.drawTooltipTextFlag = true;
            this.drawTooltipStackFlag = false;
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 1f, 0.1f);
        } else {
            this.drawTooltipScale = moveTo(this.drawTooltipScale, 0, 0.2f);
            if (this.drawTooltipScale == 0) {
                this.drawTooltipStackFlag = false;
                this.drawTooltipTextFlag = false;
            }
        }
    }


    @SubscribeEvent
//    @OnlyIn(Dist.CLIENT)
    public void onClickEvent(InputEvent.MouseButton.Pre event) {

        Player playerIn = Hexerei.proxy.getPlayer();
        if (event.getButton() == 1 && playerIn != null) {
            this.isRightPressedOld = false;
            Hexerei.entityClicked = false;

            if (Minecraft.getInstance().screen != null)
                return;

            double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();

            List<BlockPos> altars = getAltars(playerIn);

            for (BlockPos pos : altars) {

                BlockEntity blockEntity = playerIn.level().getBlockEntity(pos);

                if (blockEntity instanceof BookOfShadowsAltarTile altarTile){
                    if (altarTile.turnPage == 0 && event.getAction() == 1) {

                        if (altarTile.slotClicked != -1) {
                            if (++altarTile.slotClickedTick > 0) {
                                playerIn.swinging = false;
                                event.setCanceled(true);
                            }
                        }

                        BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);

                        if (bookData != null && bookData.isOpened()) {
                            int clicked = checkClick(playerIn, altarTile);
//                            System.out.println(clicked);
                            if (clicked == 1) {
                                if (altarTile.slotClicked == -1 && clickedNext(altarTile)) {
                                    altarTile.setTurnPage(clicked);

                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    event.setCanceled(true);
                                    break;
                                }
                            }
                            if (clicked == 2) {
                                if (altarTile.slotClicked == -1 && clickedBack(altarTile)) {
                                    altarTile.setTurnPage(clicked);

                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    event.setCanceled(true);
                                    break;
                                } else if (altarTile.slotClicked == -1) {

                                    ClientProxy.fontIndex++;
                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    event.setCanceled(true);
                                    break;
                                }
                            }
                            if (clicked == -2) {
                                //close
                                altarTile.setTurnPage(clicked);

                                playerIn.swing(InteractionHand.MAIN_HAND);
                                event.setCanceled(true);
                                break;
                            }
                            if (clicked == -1) {

                                playerIn.swing(InteractionHand.MAIN_HAND);
                                event.setCanceled(true);
                                break;
                            }
                            if (clicked == -3) {
                                //close

                                playerIn.swing(InteractionHand.MAIN_HAND);
                                event.setCanceled(true);
                                break;
                            }
                            if (clicked == 3) {
                                // clicked bookmark
                                if (bookData.getChapter() != 0) {
                                    altarTile.clickPageBookmark(bookData.getChapter(), bookData.getPage());

                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    event.setCanceled(true);
                                    break;
                                }
                            }
                            if (clicked == -5) {
                                playerIn.swinging = false;
                                event.setCanceled(true);
                                break;
                            }
                        }
                    }

                    if (altarTile.turnPage == 0 && altarTile.slotClicked != -1 && event.getAction() == 0) {


                        Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
                        Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);

                        BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);


                        if (bookData != null) {

                            int bookmark_chapter = 0;
                            int bookmark_page = 0;
                            ResourceLocation bookmark_id = null;
                            boolean flag = false;
                            int int_slot = 0;

                            BookData.Bookmarks bookmarks = bookData.getBookmarks();

                            if (altarTile.slotClicked != -1) {
                                Vec3 intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
                                AABB aabb = getpositionAABBClose(altarTile);
                                if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                    //send signal to server that you deleted your bookmark.
                                    altarTile.deleteBookmark(altarTile.slotClicked);
                                }
                            }


                            for (BookData.Bookmarks.Slot slot : bookmarks.getSlots()) {
                                boolean flag2 = false;
                                if (!slot.getId().isEmpty()) {

//                                    if (slot.contains("chapter"))
//                                        bookmark_chapter = slot.getInt("chapter");
//                                    if (slot.contains("page"))
//                                        bookmark_page = slot.getInt("page");
                                    bookmark_id = ResourceLocation.parse(slot.getId());


                                }

                                ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                                if (slot.getIndex() < 5) {

                                    float xIn = -0.4f - altarTile.buttonScale - 0.15f;
                                    float yIn = slot.getIndex() * 1.5f;
                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();
                                    vector3f_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                    vector3f.add(vector3f_1);
                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                    Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                        flag2 = true;
                                    }
                                }
                                if (slot.getIndex() >= 5 && slot.getIndex() < 10) {

                                    float xIn = -5.5f + slot.getIndex() * 1.15f;
                                    float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();
                                    vector3f_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                    vector3f.add(vector3f_1);
                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                    Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                        flag2 = true;
                                    }
                                }
                                if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                                    float xIn = -11.25f + slot.getIndex() * 1.15f;
                                    float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();
                                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                    vector3f.add(vector3f_1);
                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                    Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                        flag2 = true;
                                    }
                                }
                                if (slot.getIndex() >= 15) {

                                    float xIn = 5.5f + altarTile.buttonScale + 0.15f;
                                    float yIn = (slot.getIndex() - 15) * 1.5f;
                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();
                                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                                    vector3f.add(vector3f_1);
                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                                    Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                        flag2 = true;
                                    }
                                }
                                if (flag2) {
                                    if (altarTile.slotClicked == slot.getIndex()) {
                                        if (altarTile.slotClickedTick < 20) {
                                            //click the same bookmark
                                            boolean flag3 = false;
                                            if (bookmark_id != null) {
                                                for (BookChapter chapter : BookManager.getBookEntries().chapterList) {
                                                    for (BookPageEntry pageEntry : chapter.pages) {
                                                        if (ResourceLocation.parse(pageEntry.location).equals(bookmark_id)) {
                                                            flag3 = true;
                                                            altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                                            break;
                                                        }
                                                    }
                                                    if (flag3)
                                                        break;
                                                }
                                            }
                                            if (!flag3)
                                                altarTile.setTurnPage(-1, bookmark_chapter, bookmark_page);
                                        }
                                    } else {
                                        //drag the bookmark to another slot
                                        altarTile.swapBookmarks(altarTile.slotClicked, slot.getIndex());
                                        altarTile.bookmarkHoverAmount[slot.getIndex()] = 0;
                                        altarTile.bookmarkHoverAmount[altarTile.slotClicked] = 0;
                                    }

                                    int_slot = slot.getIndex();
                                    break;
                                }

                            }
                            if (int_slot != altarTile.slotClicked || altarTile.slotClickedTick > 5)
                                playerIn.swing(InteractionHand.MAIN_HAND);
                            altarTile.slotClicked = -1;
                            altarTile.slotClickedTick = 0;
                            break;
                        }

                    }
                }

            }
        } else if (event.getButton() == 0) {
            this.isLeftPressedOld = event.getAction() == 1;
        }

        if (playerIn != null && event.getButton() == 1) {

//            for (int i = 0; i < 6; i++) {
//                BlockHitResult raytrace = getPlayerPOVHitResult(i, playerIn.level(), playerIn, ClipContext.Fluid.NONE);
//                if (raytrace.getType() != HitResult.Type.MISS) {
//                    BlockPos pos = raytrace.getBlockPos();
//
//
//                    BlockEntity blockEntity = playerIn.level().getBlockEntity(pos);
//                }
//            }

            this.isRightPressedOld = true;
        }

    }


    public String getModNameForModId(String modId) {
        return HexereiUtil.getModNameForModId(modId);
    }

    public List<BlockPos> getAltars(Player playerIn) {
        double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();

        List<BlockPos> altars = new ArrayList<>();

        float f = playerIn.getXRot();
        float f1 = playerIn.getYRot();
        Vec3 vec3 = playerIn.getEyePosition();
        Vec3 vec31 = new Vec3(0f, 0f, 0.25f);
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;

        float section = 0;
        while (section <= reach) {
            BlockPos pos = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section));
            BlockPos pos2 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90))));
            BlockPos pos3 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90))));
            if (!altars.contains(pos))
                altars.add(pos);
            if (!altars.contains(pos2))
                altars.add(pos2);
            if (!altars.contains(pos3))
                altars.add(pos3);
            if (section > reach) {
                section = (float) reach;
                pos = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section));
                pos2 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90))));
                pos3 = BlockPos.containing(vec3.add((double) f6 * section, (double) f5 * section - 1, (double) f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90))));
                if (!altars.contains(pos))
                    altars.add(pos);
                if (!altars.contains(pos2))
                    altars.add(pos2);
                if (!altars.contains(pos3))
                    altars.add(pos3);
                break;
            }
            else
                section += 0.25;
        }
        return altars;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyEvent(InputEvent.Key event) {
//        System.out.println(event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue());

        if (!HexereiJeiCompat.LOADED)
            return;

        if (Minecraft.getInstance().screen != null)
            return;

        Player playerIn = Hexerei.proxy.getPlayer();

        //released
        if (playerIn != null && event.getAction() == 0) {

        }

        //pressed
        if (playerIn != null && event.getAction() == 1) {
            if (event.getKey() != ModKeyBindings.bookJEIShowUses.getKey().getValue() && event.getKey() != ModKeyBindings.bookJEIShowRecipe.getKey().getValue())
                return;
            if ((Minecraft.getInstance().screen instanceof IRecipesGui))
                return;

            double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();

            List<BlockPos> altars = getAltars(playerIn);

            for (BlockPos pos : altars) {

                BlockEntity blockEntity = playerIn.level().getBlockEntity(pos);

                if (blockEntity instanceof BookOfShadowsAltarTile altarTile && altarTile.turnPage == 0) {

                    BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);

                    if (bookData != null && bookData.isOpened()) {
                        Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
                        Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);

                        String location1 = "";
                        String location2 = "";
                        BookEntries bookEntries = BookManager.getBookEntries();
                        if (bookEntries != null) {
                            int chapter = bookData.getChapter();
                            int page = bookData.getPage();
                            if (page % 2 == 1)
                                page--;

                            int start = bookEntries.chapterList.get(chapter).startPage;
                            int end = bookEntries.chapterList.get(chapter).endPage;

                            if (page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0)
                                location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
                            if (end - start > page + 1)
                                location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

                            BookPage page1 = BookManager.getBookPages(ResourceLocation.parse(location1));
                            BookPage page2 = BookManager.getBookPages(ResourceLocation.parse(location2));


                            if (page1 != null) {
                                for (int i = 0; i < page1.itemList.size(); i++) {

                                    BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page1.itemList.toArray()[i]));

                                    if (bookItemStackInSlot.item != null && bookItemStackInSlot.item.isEmpty())
                                        continue;

                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();

                                    vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                                    vector3f.add(vector3f_1);

                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    float size = 0.03f;
                                    AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                                    Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                        if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showUses(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showUses(bookItemStackInSlot.fluid);
                                            }
                                        }
                                        if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showRecipe(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                            if (page2 != null) {

                                for (int i = 0; i < page2.itemList.size(); i++) {

                                    BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page2.itemList.toArray()[i]));

                                    if (bookItemStackInSlot.item == null || bookItemStackInSlot.item.isEmpty())
                                        continue;

                                    Vector3f vector3f = new Vector3f(0, 0, 0);
                                    Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                                    BlockPos blockPos = altarTile.getBlockPos();

                                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                                    vector3f.add(vector3f_1);

                                    vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                            vector3f.y() + blockPos.getY() + 18 / 16f,
                                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                                    float size = 0.03f;
                                    AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                                    Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                        if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showUses(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showUses(bookItemStackInSlot.fluid);
                                            }
                                        }
                                        if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showRecipe(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }
    }


    @OnlyIn(Dist.CLIENT)
    private static Vec3 intersectPoint(Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, Vec3 planePoint) {
        Vec3 diff = rayPoint.subtract(planePoint);
        double prod1 = diff.dot(planeNormal);
        double prod2 = rayVector.dot(planeNormal);
        double prod3 = prod1 / prod2;
        return rayPoint.subtract(rayVector.scale(prod3));
    }

    @OnlyIn(Dist.CLIENT)
    private static Vec3 intersectPoint(float xIn, float yIn, Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        if (pageOn == PageOn.RIGHT_PAGE) {
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        } else if (pageOn == PageOn.MIDDLE_BUTTON) {
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(0f, 0.5f - yIn * 0.061f, -0.03f);

            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        } else {
            BlockPos blockPos = altarTile.getBlockPos();
            Vector3f vector3f = new Vector3f(0, 0, 0);
            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

            vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
            vector3f.add(vector3f_1);
            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));
            Vec3 planePoint = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vec3 diff = rayPoint.subtract(planePoint);
            double prod1 = diff.dot(planeNormal);
            double prod2 = rayVector.dot(planeNormal);
            double prod3 = prod1 / prod2;
            return rayPoint.subtract(rayVector.scale(prod3));
        }
    }


    @OnlyIn(Dist.CLIENT)
    public Vec3 planeNormal(BookOfShadowsAltarTile altarTile, PageOn pageOn) {
        if (pageOn == PageOn.RIGHT_PAGE) {
            Vector3f vector3f = new Vector3f(0, 0, 0);

            Vector3f vector3f_1 = new Vector3f(-0.05f - -0.5f * 0.06f, 0.5f - 7.05f * 0.061f, -0.03f);

            BlockPos blockPos = altarTile.getBlockPos();

            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f.add(vector3f_1);

            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));


            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_2 = new Vector3f(0, 0, 0);
            Vector3f vector3f_2_1 = new Vector3f(-0.05f - 0 * 0.06f, 0.5f - 0 * 0.061f, -0.03f);

            vector3f_2_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_2_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_2.add(vector3f_2_1);

            vector3f_2.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_2 = new Vec3(vector3f_2.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_2.y() + blockPos.getY() + 18 / 16f,
                    vector3f_2.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_3 = new Vector3f(0, 0, 0);
            Vector3f vector3f_3_1 = new Vector3f(-0.05f - 10 * 0.06f, 0.5f - 10 * 0.061f, -0.03f);

            vector3f_3_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
            vector3f_3_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_3.add(vector3f_3_1);

            vector3f_3.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_3 = new Vec3(vector3f_3.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_3.y() + blockPos.getY() + 18 / 16f,
                    vector3f_3.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


            Vec3 vec3_pr = vec_2.subtract(vec);
            Vec3 vec3_pq = vec_3.subtract(vec);

            return vec3_pr.cross(vec3_pq);
        } else {
            Vector3f vector3f = new Vector3f(0, 0, 0);

            Vector3f vector3f_1 = new Vector3f(0.35f - -0.5f * 0.06f, 0.5f - 7.05f * 0.061f, -0.03f);

            BlockPos blockPos = altarTile.getBlockPos();

            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f.add(vector3f_1);

            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));


            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f.y() + blockPos.getY() + 18 / 16f,
                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_2 = new Vector3f(0, 0, 0);
            Vector3f vector3f_2_1 = new Vector3f(0.35f - 0 * 0.06f, 0.5f - 0 * 0.061f, -0.03f);

            vector3f_2_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_2_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_2.add(vector3f_2_1);

            vector3f_2.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_2 = new Vec3(vector3f_2.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_2.y() + blockPos.getY() + 18 / 16f,
                    vector3f_2.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

            Vector3f vector3f_3 = new Vector3f(0, 0, 0);
            Vector3f vector3f_3_1 = new Vector3f(0.35f - 10 * 0.06f, 0.5f - 10 * 0.061f, -0.03f);

            vector3f_3_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
            vector3f_3_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

            vector3f_3.add(vector3f_3_1);

            vector3f_3.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

            Vec3 vec_3 = new Vec3(vector3f_3.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                    vector3f_3.y() + blockPos.getY() + 18 / 16f,
                    vector3f_3.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


            Vec3 vec3_pr = vec_2.subtract(vec);
            Vec3 vec3_pq = vec_3.subtract(vec);

            return vec3_pr.cross(vec3_pq);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int checkClick(Player playerIn, BookOfShadowsAltarTile altarTile) {
        int clicked = 0;

        double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
        Vec3 planeNormalRight = planeNormal(altarTile, PageOn.RIGHT_PAGE);
        Vec3 planeNormalLeft = planeNormal(altarTile, PageOn.LEFT_PAGE);
        if (!this.isRightPressedOld) {


            Vec3 intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
            AABB aabb = getpositionAABBNext(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: next1");
                clicked = 1;
                return clicked;
            }

            intersectionVec = intersectPoint(-0.5f, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
            aabb = getpositionAABBBack(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: back2");
                clicked = 2;
                return clicked;
            }
            intersectionVec = intersectPoint(0, 7.05f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
            aabb = getpositionAABBClose(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: close");
                clicked = -2;
                return clicked;
            }
            intersectionVec = intersectPoint(0, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.MIDDLE_BUTTON);
            aabb = getpositionAABBHome(altarTile);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: home");
                clicked = -1;
                altarTile.setTurnPage(clicked, 0, 0);
                return clicked;
            }
        }

        if (!this.isRightPressedOld) {
            Vec3 intersectionVec = intersectPoint(-0.5f, -1f, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
            AABB aabb = getpositionAABBLeft(altarTile, -0.5f, -1f);
            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
//                System.out.println("clicked: bookmark");
                clicked = 3;


                return clicked;
            }
        }

        if (!this.isRightPressedOld) {

            BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);

            if (bookData != null) {

                for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                    if (!slot.getId().isEmpty()) {

                        boolean flag2 = false;

                        ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                        if (slot.getIndex() < 5) {


                            float xIn = -0.4f - altarTile.buttonScale - 0.15f;
                            float yIn = slot.getIndex() * 1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));


                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn - altarTile.bookmarkHoverAmount[slot.getIndex()] / 3, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (slot.getIndex() >= 5 && slot.getIndex() < 10) {


                            float xIn = -5.5f + slot.getIndex() * 1.15f;
                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees((10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (slot.getIndex() >= 10 && slot.getIndex() < 15) {

                            float xIn = -11.25f + slot.getIndex() * 1.15f;
                            float yIn = -0.95f - altarTile.buttonScale - 0.25f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (slot.getIndex() >= 15) {

                            float xIn = 5.5f + altarTile.buttonScale + 0.15f;
                            float yIn = (slot.getIndex() - 15) * 1.5f;
//                            float xIn = -11.25f + i * 1.15f;
//                            float yIn = -0.95f - altarTile.buttonScale/1.5f;
                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();
                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));
                            vector3f.add(vector3f_1);
                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03, -0.03, -0.03), vec.add(0.03, 0.03, 0.03));

                            Vec3 intersectionVec = intersectPoint(xIn, yIn, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                flag2 = true;
                            }
                        }
                        if (flag2) {
                            altarTile.slotClicked = slot.getIndex();
                            clicked = -1;
//                            altarTile.setTurnPage(clicked, bookmark_chapter, bookmark_page);
//                            return clicked;
                        }
                    }
                }
            }
        }


        ItemStack stack = altarTile.itemHandler.getStackInSlot(0);
        BookData bookData = stack.get(ModDataComponents.BOOK);
        if (bookData != null) {

            String location1 = "";
            String location2 = "";
            BookEntries bookEntries = BookManager.getBookEntries();

            int chapter = bookData.getChapter();
            int page = bookData.getPage();
            if (page % 2 == 1)
                page--;

            if (bookEntries != null) {

                int start = bookEntries.chapterList.get(chapter).startPage;
                int end = bookEntries.chapterList.get(chapter).endPage;

                if (page < bookEntries.chapterList.get(chapter).pages.size() && page >= 0)
                    location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
                if (end - start > page + 1)
                    location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

                BookPage page1 = BookManager.getBookPages(ResourceLocation.parse(location1));
                BookPage page2 = BookManager.getBookPages(ResourceLocation.parse(location2));

                if (page1 != null) {

                    if (!this.isRightPressedOld)

                        for (int i = 0; i < page1.nonItemTooltipList.size(); i++) {

                            BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page1.nonItemTooltipList.toArray()[i]));


                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                            Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                            if (bookNonItemTooltip.hyperlink_id.isEmpty() && bookNonItemTooltip.hyperlink_url.equals(""))
                                continue;
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;
                                if (!bookNonItemTooltip.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookNonItemTooltip.hyperlink_url);
                                if (!bookNonItemTooltip.hyperlink_id.isEmpty()) {
                                    for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                        for (BookPageEntry pageEntry : chapterEntry.pages) {
                                            if (pageEntry.location.equals(bookNonItemTooltip.hyperlink_id)) {
                                                altarTile.setTurnPage(clicked, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    for (int i = 0; i < page1.itemList.size(); i++) {

                        BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page1.itemList.toArray()[i]));


                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                        BlockPos blockPos = altarTile.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpened / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                        float size = 0.03f;
                        AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                        Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);

                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                            clicked = -1;
                            String itemRegistryName;

                            if (bookItemStackInSlot.item != null)
                                itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.item.getItem()).toString();
                            else
                                itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.fluid.getFluid()).toString();

                            boolean flag = false;
                            if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
                                BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                                if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                    altarTile.setTurnPage(clicked, hyperlink.chapter, hyperlink.page);
                                flag = true;
                            }
                            if (!flag) {
                                for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                    for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                        String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                        BookPage page_check = BookManager.getBookPages(ResourceLocation.parse(location3));
                                        if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                            if (!(chapter == j && (page == k || page == k - 1)))
                                                altarTile.setTurnPage(clicked, j, k);
                                            BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                            flag = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!flag) {
                            }
                            break;
                        }
                    }

                    for (int i = 0; i < page1.imageList.size(); i++) {
                        BookImage bookImage = ((BookImage) (page1.imageList.toArray()[i]));

                        Vector3f vector3f = new Vector3f(0, 0, 0);
                        Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                        BlockPos blockPos = altarTile.getBlockPos();

                        vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpenedRender / 1.12f));
                        vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                        vector3f.add(vector3f_1);

                        vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));

                        Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                vector3f.y() + blockPos.getY() + 18 / 16f,
                                vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                        AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                        Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                        if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                            //add hyperlink stuff here
//                            loc.set(bookImageEffect.hoverImage.imageLoc);
                            clicked = -1;

                            if (!bookImage.hyperlink_url.equals(""))
                                showLinkScreenClient(bookImage.hyperlink_url);
                            if (!bookImage.hyperlink_id.isEmpty()) {

                                for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                    for (BookPageEntry pageEntry : chapterEntry.pages) {
                                        if (pageEntry.location.equals(bookImage.hyperlink_id)) {
                                            altarTile.setTurnPage(clicked, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }


                    if (altarTile.slotClicked == -1) {
                        for (int i = 0; i < page1.entityList.size(); i++) {
                            BookEntity bookEntity = ((BookEntity) (page1.entityList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(0.35f - (bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(10 + altarTile.degreesOpenedRender / 1.12f));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                            Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, altarTile, PageOn.LEFT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                if (!this.isRightPressedOld) {
                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    Hexerei.entityClicked = true;
                                }
                                return -5;
                            }
                        }
                    }

                }
                if (page2 != null) {

                    if (!this.isRightPressedOld) {
                        for (int i = 0; i < page2.nonItemTooltipList.size(); i++) {

                            BookNonItemTooltip bookNonItemTooltip = ((BookNonItemTooltip) (page2.nonItemTooltipList.toArray()[i]));

                            if (bookNonItemTooltip.hyperlink_id.isEmpty() && bookNonItemTooltip.hyperlink_url.equals(""))
                                continue;

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - bookNonItemTooltip.x * 0.06f, 0.5f - bookNonItemTooltip.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookNonItemTooltip.width, -bookNonItemTooltip.height, -bookNonItemTooltip.width), vec.add(bookNonItemTooltip.width, bookNonItemTooltip.height, bookNonItemTooltip.width));

                            Vec3 intersectionVec = intersectPoint(bookNonItemTooltip.x, bookNonItemTooltip.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;

                                if (!bookNonItemTooltip.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookNonItemTooltip.hyperlink_url);
                                if (!bookNonItemTooltip.hyperlink_id.isEmpty()) {

                                    for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                        for (BookPageEntry pageEntry : chapterEntry.pages) {
                                            if (pageEntry.location.equals(bookNonItemTooltip.hyperlink_id)) {
                                                altarTile.setTurnPage(clicked, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        for (int i = 0; i < page2.itemList.size(); i++) {

                            BookItemsAndFluids bookItemStackInSlot = ((BookItemsAndFluids) (page2.itemList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f - bookItemStackInSlot.x * 0.06f, 0.5f - bookItemStackInSlot.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpened / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpened / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpun));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpun) / 57.1f) / 32f * (altarTile.degreesOpened / 5f - 12f));

                            float size = 0.03f;
                            AABB aabb = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));

                            Vec3 intersectionVec = intersectPoint(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);

                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                clicked = -1;

                                String itemRegistryName;

                                if (bookItemStackInSlot.item != null)
                                    itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.item.getItem()).toString();
                                else
                                    itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.fluid.getFluid()).toString();

                                boolean flag = false;
                                if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
//                                System.out.println("Found previous hyperlink");
                                    BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                                    if (!(chapter == hyperlink.chapter && (page == hyperlink.page || page == hyperlink.page - 1)))
                                        altarTile.setTurnPage(clicked, hyperlink.chapter, hyperlink.page);
                                    flag = true;
                                }
                                if (!flag) {
                                    for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                                        for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                                            String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                                            BookPage page_check = BookManager.getBookPages(ResourceLocation.parse(location3));
                                            if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                                                if (!(chapter == j && (page == k || page == k - 1)))
                                                    altarTile.setTurnPage(clicked, j, k);
                                                BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                                                flag = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!flag) {
//                                System.out.println("No hyperlink found");
                                }
                                break;
                            }
                        }

                        for (int i = 0; i < page2.imageList.size(); i++) {

                            BookImage bookImage = ((BookImage) (page2.imageList.toArray()[i]));

                            if (bookImage.hyperlink_id.isEmpty() && bookImage.hyperlink_url.equals(""))
                                continue;

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                            Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                                clicked = -1;

                                if (!bookImage.hyperlink_url.equals(""))
                                    showLinkScreenClient(bookImage.hyperlink_url);
                                if (!bookImage.hyperlink_id.isEmpty()) {

                                    for (BookChapter chapterEntry : BookManager.getBookEntries().chapterList) {
                                        for (BookPageEntry pageEntry : chapterEntry.pages) {
                                            if (pageEntry.location.equals(bookImage.hyperlink_id)) {
                                                altarTile.setTurnPage(clicked, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;

                                //add hyperlink stuff here
//                            loc.set(bookImageEffect.hoverImage.imageLoc);
                            }
                        }
                    }


                    if (altarTile.slotClicked == -1) {
                        for (int i = 0; i < page2.entityList.size(); i++) {
                            BookEntity bookEntity = ((BookEntity) (page2.entityList.toArray()[i]));

                            Vector3f vector3f = new Vector3f(0, 0, 0);
                            Vector3f vector3f_1 = new Vector3f(-0.05f + -(bookEntity.x + bookEntity.offset.x) * 0.06f, 0.5f - (bookEntity.y + bookEntity.offset.y) * 0.061f, -0.03f);

                            BlockPos blockPos = altarTile.getBlockPos();

                            vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + altarTile.degreesOpenedRender / 1.12f)));
                            vector3f_1.rotate(Axis.XP.rotationDegrees(45 - altarTile.degreesOpenedRender / 2f));

                            vector3f.add(vector3f_1);

                            vector3f.rotate(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));

                            Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f),
                                    vector3f.y() + blockPos.getY() + 18 / 16f,
                                    vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((altarTile.degreesSpunRender) / 57.1f) / 32f * (altarTile.degreesOpenedRender / 5f - 12f));

                            AABB aabb = new AABB(vec.add(-0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale, -0.03 * bookEntity.scale * bookEntity.offset.scale), vec.add(0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale, 0.03 * bookEntity.scale * bookEntity.offset.scale));

                            Vec3 intersectionVec = intersectPoint(bookEntity.x, bookEntity.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, altarTile, PageOn.RIGHT_PAGE);
                            if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                                if (!this.isRightPressedOld) {
                                    playerIn.swing(InteractionHand.MAIN_HAND);
                                    Hexerei.entityClicked = true;
                                }
                                return -5;
                            }
                        }
                    }

                }
            }


        }

        return clicked;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean clickedBack(BookOfShadowsAltarTile altarTile) {

        BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
        int currentPage = bookData.getPage();
        int currentChapter = bookData.getChapter();
        return currentChapter > 0 || currentPage > 1;

    }

    @OnlyIn(Dist.CLIENT)
    public boolean clickedNext(BookOfShadowsAltarTile altarTile) {

        BookData bookData = altarTile.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
        int currentPage = bookData.getPage();
        int currentChapter = bookData.getChapter();
        return currentChapter < BookManager.getBookEntries().chapterList.size() - 1 || currentPage < BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 2;

    }

    @OnlyIn(Dist.CLIENT)
    public static void showLinkScreenClient(String link) {
        ConfirmLinkScreen screen = new ConfirmLinkScreen((p_169232_) -> {
            if (p_169232_) {
                Util.getPlatform().openUri(link);
            }
            Minecraft.getInstance().setScreen(null);
        }, link, true);

        Minecraft.getInstance().setScreen(screen);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawSlot(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        matrixStack.translate(xIn / 8.1f, yIn / 8.1f, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/slot.png")));

//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = matrixStack.last();
        int u = 0;
        int v = 0;
        int imageWidth = 32;
        int imageHeight = 32;
        int width = 18;
        int height = 18;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        if (bufferSource instanceof MultiBufferSource.BufferSource source)
            source.endBatch();

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawFluidInSlot(BookOfShadowsAltarTile tileEntityIn, @NotNull BookItemsAndFluids bookItemsAndFluids, PoseStack matrixStack, MultiBufferSource bufferSource, float xIn, float yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();
        FluidStack stack = bookItemsAndFluids.fluid;
        int capacity = bookItemsAndFluids.capacity;
        boolean showSlot = bookItemsAndFluids.show_slot;
        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        matrixStack.translate(xIn / 8.1f, yIn / 8.1f, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        Matrix4f matrix = matrixStack.last().pose();
        if (showSlot) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/slot.png")));

//            matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
            PoseStack.Pose normal = matrixStack.last();
            int u = 0;
            int v = 0;
            int imageWidth = 18;
            int imageHeight = 18;
            int width = 18;
            int height = 18;
            float u1 = (u + 0.0F) / (float) imageWidth;
            float u2 = (u + (float) width) / (float) imageWidth;
            float v1 = (v + 0.0F) / (float) imageHeight;
            float v2 = (v + (float) height) / (float) imageHeight;

            buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        }
        drawFluid(matrixStack, bufferSource, (int) bookItemsAndFluids.fluid_width, (int) bookItemsAndFluids.fluid_height, stack, capacity, light, overlay, bookItemsAndFluids.fluid_offset_x, bookItemsAndFluids.fluid_offset_y, bookItemsAndFluids.fluid_width, bookItemsAndFluids.fluid_height);

        matrixStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    private void drawFluid(PoseStack poseStack, MultiBufferSource bufferSource, final int tiledWidth, final int tiledHeight, FluidStack fluidStack, int capacity, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

//        FluidType attributes = fluid.getFluidType();
//        int fluidColor = attributes.getColor(fluidStack);
        int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(fluidStack);

        int amount = fluidStack.getAmount();
//        int amount = (int)Math.abs((Math.sin(Hexerei.getClientTicks() / 100) * 2000));
        if (amount == 0)
            amount = capacity > 0 ? capacity : 1000;
        int scaledAmount = (amount * tiledHeight) / (capacity != 0 ? capacity : 1000);
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > tiledHeight) {
            scaledAmount = tiledHeight;
        }
        if (capacity == 0)
            scaledAmount = tiledHeight;

        drawTiledSprite(poseStack, bufferSource, tiledWidth, tiledHeight, fluidColor, scaledAmount, fluidStillSprite, capacity, amount, light, overlay, x_offset, y_offset, width, height);

    }

    @OnlyIn(Dist.CLIENT)
    private static void drawTiledSprite(PoseStack poseStack, MultiBufferSource bufferSource, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int capacity, int amount, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final int yTileCount = scaledAmount / TEXTURE_SIZE;
        final int yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width2 = (xTile == xTileCount) ? xRemainder : (int) width;
                int height2 = (yTile == yTileCount) ? yRemainder : (int) height;
//                if(capacity > 0 && capacity >= amount)
//                    height2 *= ((float)amount / (float)capacity);
                int x_tile = (xTile * TEXTURE_SIZE);
                int y_tile = tiledHeight - ((yTile + 1) * (int) height);
                if (width2 > 0 && height2 > 0) {
                    int maskTop = (int) height - height2;
                    int maskRight = (int) width - width2;

                    drawTextureWithMasking(poseStack, bufferSource, capacity, amount, x_tile, y_tile, sprite, color, maskTop, maskRight, 1, light, overlay, x_offset, y_offset, width, height);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        FluidType type = fluid.getFluidType();
        ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack);
        return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }

    @OnlyIn(Dist.CLIENT)
    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawTextureWithMasking(PoseStack poseStack, MultiBufferSource bufferSource, int capacity, int amount, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int color, int maskTop, int maskRight, float zLevel, int light, int overlay, float x_offset, float y_offset, float width, float height) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        uMax = uMax - (maskRight / width * (uMax - uMin));
        vMax = vMax - (maskTop / height * (vMax - vMin));

        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        poseStack.pushPose();
        poseStack.translate(0.001f, 0.0485f + (y_offset * 0.005975f), (x_offset * 0.005975f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose normal = poseStack.last();
        poseStack.popPose();


        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        buffer.addVertex(matrix, 0, -0.055f / 18 * (width), 0).setColor(red, green, blue, alpha).setUv(uMin, vMax).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * (width), 0).setColor(red, green, blue, alpha).setUv(uMax, vMax).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * (width), 0.055f / 9 * (height - maskTop)).setColor(red, green, blue, alpha).setUv(uMax, vMin).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * (width), 0.055f / 9 * (height - maskTop)).setColor(red, green, blue, alpha).setUv(uMin, vMin).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

    }

    private float easeInOutElastic(double x) {

        double c5 = (2 * Math.PI) / 4.5;

        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? 4 * x * x * x
                : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1);


//        double c1 = 1.70158;
//        double c2 = c1 * 1.525;
//
//        return x == 0
//                ? 0
//                : x == 1
//                ? 1
//                : x < 0.5
//                ? (float)((1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2)
//                : (float)((Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTooltipImage(ItemStack stack, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, boolean isItem) {

        matrixStack.pushPose();

        matrixStack.translate(8f / 16f, 18f / 16f, 8f / 16f);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(270));
        matrixStack.translate(0.25f, -(1 - (this.drawTooltipScale < 0.5f ? this.drawTooltipScale * 2f : 1)) / 12f, 0);
        float scale = easeInOutElastic(this.drawTooltipScale);
        matrixStack.scale(scale, scale, scale);

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        this.tooltipStack = stack;
        if (!this.tooltipStack.isEmpty()) {
            List<Component> tooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);

            if (tooltip.size() > 0)
                tooltip.addAll(this.tooltipText);

            String modId = HexereiUtil.getRegistryName(this.tooltipStack.getItem()).getNamespace();
            String modName = getModNameForModId(modId);
            MutableComponent modNameComponent = Component.translatable(modName);
            modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
            if (!HexereiModNameTooltipCompat.LOADED)
                tooltip.add(modNameComponent);

            this.renderTooltip(this.tooltipStack, bufferSource, matrixStack, tooltip, stack.getTooltipImage(), 0, 0, overlay, light);
        }

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getFluidTooltip(BookItemsAndFluids bookItemStackInSlot) {
        FluidStack fluidStack = bookItemStackInSlot.fluid;
        int capacity = bookItemStackInSlot.capacity;
        int amount = bookItemStackInSlot.amount;
        List<Component> tooltip = new ArrayList<>();
        Fluid fluidType = fluidStack.getFluid();

        MutableComponent displayName = (MutableComponent) fluidStack.getHoverName();
        displayName.withStyle(ChatFormatting.WHITE);
        tooltip.add(displayName);
        if (capacity != 0) {
            MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount.with.capacity", nf.format(amount), nf.format(capacity));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        } else if (amount != 0) {
            MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount", nf.format(amount));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        }

        if (!bookItemStackInSlot.extra_tooltips.isEmpty())
            tooltip.addAll(bookItemStackInSlot.extra_tooltips);


        String modId = HexereiUtil.getRegistryName(fluidStack.getFluid()).getNamespace();
        String modName = getModNameForModId(modId);
        MutableComponent modNameComponent = Component.translatable(modName);
        modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
        tooltip.add(modNameComponent);

        return tooltip;
    }


    @OnlyIn(Dist.CLIENT)
    public void drawTooltipText(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, boolean isItem) {

        matrixStack.pushPose();

        matrixStack.translate(8f / 16f, 18f / 16f, 8f / 16f);
        matrixStack.translate((float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f), 0f / 16f, (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));
        matrixStack.translate(0, -((tileEntityIn.degreesFloppedRender / 90)) / 16f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));
        matrixStack.mulPose(Axis.XP.rotationDegrees(-(tileEntityIn.degreesOpenedRender / 2 + 45)));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-tileEntityIn.degreesFloppedRender));
        matrixStack.translate(0, 0, -(tileEntityIn.degreesFloppedRender / 10f) / 32);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(270));
        float scale = Math.min(this.drawTooltipScale, 1);
        matrixStack.translate(0.25f, -(1 - (scale < 0.5f ? scale * 2f : 1)) / 12f, 0);
        scale = easeInOutElastic(this.drawTooltipScale);
        if (scale < 0) scale = 0;
        matrixStack.scale(scale, scale, scale);


//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);

        this.renderTooltip(this.tooltipStack, bufferSource, matrixStack, this.tooltipText, Optional.empty(), 0, 0, overlay, light);

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void renderTooltip(ItemStack stack, MultiBufferSource buffer, PoseStack p_169389_, List<Component> components, Optional<TooltipComponent> p_169391_, int p_169392_, int p_169393_, int overlay, int light) {
        List<ClientTooltipComponent> list = ClientHooks.gatherTooltipComponents(stack, components, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
        List<Component> newComponentList = new ArrayList<>();
        for (Component component : components) {
            newComponentList.add(Component.translatable(component.getString()).withStyle(component.getStyle().withColor(0x292929)));
        }
        List<ClientTooltipComponent> list2 = ClientHooks.gatherTooltipComponents(stack, newComponentList, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
        this.renderTooltipInternal(buffer, p_169389_, list, list2, p_169392_, p_169393_, overlay, light);
    }


    @OnlyIn(Dist.CLIENT)
    private void renderTooltipInternal(MultiBufferSource bufferSource, PoseStack matrixStack, List<ClientTooltipComponent> clientTooltipComponentList, List<ClientTooltipComponent> clientTooltipComponentList2, int p_169386_, int p_169387_, int overlay, int light) {
        if (!clientTooltipComponentList.isEmpty()) {

            RenderTooltipEvent.Pre preEvent = ClientHooks.onRenderTooltipPre(this.tooltipStack, new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource), p_169386_, p_169387_, 750, 750, clientTooltipComponentList, Minecraft.getInstance().font, DefaultTooltipPositioner.INSTANCE);
            if (preEvent.isCanceled()) {
                return;
            }

            int i = 0;
            int j = clientTooltipComponentList.size() == 1 ? -2 : 0;

            ClientTooltipComponent clientTooltipComponent;
            int l;
            for (Iterator<ClientTooltipComponent> var8 = clientTooltipComponentList.iterator(); var8.hasNext(); j += clientTooltipComponent.getHeight()) {
                clientTooltipComponent = var8.next();
                l = clientTooltipComponent.getWidth(preEvent.getFont());
                if (l > i) {
                    i = l;
                }
            }

            int j2 = preEvent.getX() + 12;
            int k2 = preEvent.getY() - 12;
            if (j2 + i > 750) {
                j2 -= 28 + i;
            }

            if (k2 + j + 6 > 750) {
                k2 = 750 - j - 6;
            }

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));

            matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
            matrixStack.scale(0.003f, 0.003f, 0.003f);
            matrixStack.translate(-(i + 15) / 2f, -(j + 15) / 2f, -10);


            RenderTooltipEvent.Color colorEvent = ClientHooks.onRenderTooltipColor(this.tooltipStack, new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource), j2, k2, preEvent.getFont(), clientTooltipComponentList);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 0.2f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 4, j2 + i + 3, k2 - 2, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 4, 0.1f, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 4, k2 - 3, j2 - 2, k2 + j + 3, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 + i + 2, k2 - 3, j2 + i + 4, k2 + j + 3, 0.1f, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light);
            ((MultiBufferSource.BufferSource) bufferSource).endBatch();
            buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 0, colorEvent.getBorderStart(), colorEvent.getBorderStart(), overlay, light);
            fillGradient(matrixStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 0, colorEvent.getBorderEnd(), colorEvent.getBorderEnd(), overlay, light);
            RenderSystem.enableDepthTest();

            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            matrixStack.translate(0.0D, 0.0D, 0.01D);

            matrixStack.scale(1, 1, 0.00001f);
            int l1 = k2;
//
            Matrix4f matrix4f = matrixStack.last().pose();
            int l2;
            ClientTooltipComponent clientTooltipComponent2;
            for (l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = clientTooltipComponentList.get(l2);
                if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip) {
                    hexereiBookTooltip.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource, overlay, light);
                }
                else if (clientTooltipComponent2 instanceof ClientTextTooltip clientTextTooltip) {
                    int r = (int) (0.25f * 255.0f);
                    int g = (int) (0.25f * 255.0f);
                    int b = (int) (0.25f * 255.0f);
                    int a = (int) (1 * 255.0F);


                    int col = (a << 24) | (r << 16) | (g << 8) | b;
                    Font font = preEvent.getFont();
                    matrix4f = matrixStack.last().pose();
                    font.drawInBatch(clientTextTooltip.text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, light);
                    matrixStack.pushPose();
                    matrixStack.translate(0.5f, 0.5f, 7500);
                    matrix4f = matrixStack.last().pose();
                    font.drawInBatch(((ClientTextTooltip) clientTooltipComponentList2.get(l2)).text, (float) j2, (float) l1, col, false, matrix4f, multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, light);
                    matrixStack.popPose();
                }
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

            multibuffersource$buffersource.endBatch();
            l1 = k2;

            matrixStack.scale(1, 1, 333.333f);
            for (l2 = 0; l2 < clientTooltipComponentList.size(); ++l2) {
                clientTooltipComponent2 = clientTooltipComponentList.get(l2);
                RenderSystem.enableDepthTest();
                if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip)
                    hexereiBookTooltip.renderImage(preEvent.getFont(), bufferSource, j2, l1, matrixStack, itemRenderer, 0, overlay, light);
//                else
//                    clientTooltipComponent2.renderImage(preEvent.getFont(), j2, l1, matrixStack, this.itemRenderer, 0);
                l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

        }

    }

    private static int adjustColor(int p_92720_) {
        return (p_92720_ & -67108864) == 0 ? p_92720_ | -16777216 : p_92720_;
    }

    protected static void fillGradient(PoseStack poseStack, VertexConsumer buffer, int p_93126_, int p_93127_, int p_93128_, int p_93129_, float p_93130_, int pColorFrom, int pColorTo, int overlay, int light) {

        float fromAlpha = (float) FastColor.ARGB32.alpha(pColorFrom) / 255.0F * 0.9f;
        float f1 = (float)FastColor.ARGB32.red(pColorFrom) / 255.0F;
        float f2 = (float)FastColor.ARGB32.green(pColorFrom) / 255.0F;
        float f3 = (float)FastColor.ARGB32.blue(pColorFrom) / 255.0F;
        float toAlpha = (float)FastColor.ARGB32.alpha(pColorTo) / 255.0F * 0.9f;
        float f5 = (float)FastColor.ARGB32.red(pColorTo) / 255.0F;
        float f6 = (float)FastColor.ARGB32.green(pColorTo) / 255.0F;
        float f7 = (float)FastColor.ARGB32.blue(pColorTo) / 255.0F;

        PoseStack.Pose normal = poseStack.last();
        Matrix4f matrix4f = poseStack.last().pose();


        int u = 0;
        int v = 0;
        int imageWidth = 1;
        int imageHeight = 1;
        int width = 1;
        int height = 1;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix4f, p_93128_, p_93127_, p_93130_).setColor(f1, f2, f3, fromAlpha).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93126_, p_93127_, p_93130_).setColor(f1, f2, f3, fromAlpha).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93126_, p_93129_, p_93130_).setColor(f5, f6, f7, toAlpha).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix4f, p_93128_, p_93129_, p_93130_).setColor(f5, f6, f7, toAlpha).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);


    }


    @OnlyIn(Dist.CLIENT)
    public void drawBookmark(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float zLevel, float rotate, int light, int overlay, PageOn pageOn, int color, boolean isItem, ItemDisplayContext transformType) {

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(tileEntityIn, matrixStack, isItem, transformType);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f * bookImage.scale, 0.5f * bookImage.scale, 0.5f * bookImage.scale);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));

        matrixStack.translate((bookImage.x / 8.1f - 0.03f / 16f) / bookImage.scale, (bookImage.y / 8.1f - 0.053f / 16f) / bookImage.scale, -zLevel / 1600f / bookImage.scale);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("scale")) {

                float f = bookImageEffect.amount - 1;

                float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
                matrixStack.scale(x, x, x);
            }
        }));

        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("tilt")) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
            }
        }));
        matrixStack.mulPose(Axis.XP.rotationDegrees(rotate));
        if (transformType != ItemDisplayContext.NONE)
            matrixStack.mulPose(Axis.ZP.rotationDegrees(-35));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(bookImage.imageLoc)));

//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = matrixStack.last();
        int u = (int) bookImage.u;
        int v = (int) bookImage.v;
        int imageWidth = (int) bookImage.imageWidth;
        int imageHeight = (int) bookImage.imageHeight;
        int width = (int) bookImage.width;
        int height = (int) bookImage.height;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }


        if (transformType != ItemDisplayContext.NONE) {
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

            buffer.addVertex(matrix, 0, -0.055f / 9 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 9 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        } else {
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        }
        matrixStack.popPose();

    }


    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {
        drawImage(bookImage, tileEntityIn, matrixStack, bufferSource, zLevel, light, overlay, pageOn, -1, isItem, ItemDisplayContext.NONE);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawImage(BookImage bookImage, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, PageOn pageOn, int color, boolean isItem, ItemDisplayContext transformType) {

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, transformType);//
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, transformType);
        else if (pageOn == PageOn.MIDDLE_BUTTON)
            translateToMiddleButton(tileEntityIn, matrixStack, isItem, transformType);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f * bookImage.scale, 0.5f * bookImage.scale, 0.5f * bookImage.scale);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));

        matrixStack.translate((bookImage.x / 8.1f - 0.03f / 16f) / bookImage.scale, (bookImage.y / 8.1f - 0.053f / 16f) / bookImage.scale, -(zLevel + bookImage.z) / 1600f);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("scale")) {

                float f = bookImageEffect.amount - 1;

                float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
                matrixStack.scale(x, x, x);
            }
        }));

        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("tilt")) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookImageEffect.speed)));
            }
        }));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);


        AtomicReference<String> loc = new AtomicReference<>(bookImage.imageLoc);
        AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
        AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);

        AtomicReference<Integer> u = new AtomicReference<>((int) bookImage.u);
        AtomicReference<Integer> v = new AtomicReference<>((int) bookImage.v);
        AtomicReference<Integer> imageWidth = new AtomicReference<>((int) bookImage.imageWidth);
        AtomicReference<Integer> imageHeight = new AtomicReference<>((int) bookImage.imageHeight);
        AtomicReference<Integer> width = new AtomicReference<>((int) bookImage.width);
        AtomicReference<Integer> height = new AtomicReference<>((int) bookImage.height);


        AtomicBoolean flag = new AtomicBoolean(false);

        bookImage.effects.forEach((bookImageEffect -> {
            if (bookImageEffect.type.equals("hover_change_texture")) {

                LocalPlayer playerIn = (LocalPlayer) Hexerei.proxy.getPlayer();

                double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
                Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
                Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag.set(true);
                        loc.set(bookImageEffect.hoverImage.imageLoc);
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {
                        flag.set(true);
                        loc.set(bookImageEffect.hoverImage.imageLoc);
                    }
                }


            }
            if (bookImageEffect.type.equals("hover_overlay")) {

                LocalPlayer playerIn = (LocalPlayer) Hexerei.proxy.getPlayer();

                double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
                Vec3 planeNormalRight = planeNormal(tileEntityIn, PageOn.RIGHT_PAGE);
                Vec3 planeNormalLeft = planeNormal(tileEntityIn, PageOn.LEFT_PAGE);

                if (pageOn == PageOn.LEFT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(0.35f - bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(10 + tileEntityIn.degreesOpenedRender / 1.12f));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalLeft, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                        overlay_image.set(bookImageEffect.hoverImage);
                        overlay_draw.set(true);
                    }
                } else if (pageOn == PageOn.RIGHT_PAGE) {
                    Vector3f vector3f = new Vector3f(0, 0, 0);
                    Vector3f vector3f_1 = new Vector3f(-0.05f + -bookImage.x * 0.06f, 0.5f - bookImage.y * 0.061f, -0.03f);

                    BlockPos blockPos = tileEntityIn.getBlockPos();

                    vector3f_1.rotate(Axis.YP.rotationDegrees(-(10 + tileEntityIn.degreesOpenedRender / 1.12f)));
                    vector3f_1.rotate(Axis.XP.rotationDegrees(45 - tileEntityIn.degreesOpenedRender / 2f));

                    vector3f.add(vector3f_1);

                    vector3f.rotate(Axis.YP.rotationDegrees(tileEntityIn.degreesSpunRender));

                    Vec3 vec = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f),
                            vector3f.y() + blockPos.getY() + 18 / 16f,
                            vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((tileEntityIn.degreesSpunRender) / 57.1f) / 32f * (tileEntityIn.degreesOpenedRender / 5f - 12f));

                    AABB aabb = new AABB(vec.add(-bookImage.width / 850 * bookImage.scale, -bookImage.height / 850 * bookImage.scale, -bookImage.width / 850 * bookImage.scale), vec.add(bookImage.width / 850 * bookImage.scale, bookImage.height / 850 * bookImage.scale, bookImage.width / 850 * bookImage.scale));

                    Vec3 intersectionVec = intersectPoint(bookImage.x, bookImage.y, playerIn.getLookAngle(), playerIn.getEyePosition(), planeNormalRight, tileEntityIn, pageOn);
                    if (aabb.contains(intersectionVec) && intersectionVec.subtract(playerIn.getEyePosition()).length() <= reach) {

                        overlay_image.set(bookImageEffect.hoverImage);
                        overlay_draw.set(true);
                    }
                }

            }


            if (flag.get()) {

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if (bookHoverImageEffect.type.equals("scale")) {

                        float f = bookHoverImageEffect.amount - 1;

                        float x = (f / 2f + 1 + ((f / 2f) * Mth.sin((Hexerei.getClientTicks()) / bookHoverImageEffect.speed)));
                        matrixStack.scale(x, x, x);
                    }
                }));

                bookImageEffect.hoverImage.effects.forEach((bookHoverImageEffect -> {
                    if (bookHoverImageEffect.type.equals("tilt")) {
                        matrixStack.mulPose(Axis.XP.rotationDegrees(-bookHoverImageEffect.amount * Mth.sin((Hexerei.getClientTicks()) / bookHoverImageEffect.speed)));
                    }
                }));

            }


        }));

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));

//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = matrixStack.last();

        float u1 = (u.get() + 0.0F) / (float) imageWidth.get();
        float u2 = (u.get() + (float) width.get()) / (float) imageWidth.get();
        float v1 = (v.get() + 0.0F) / (float) imageHeight.get();
        float v2 = (v.get() + (float) height.get()) / (float) imageHeight.get();

        float a = 1;
        float r = 1;
        float g = 1;
        float b = 1;

        if (color != -1) {
            r = (float) (color >> 16 & 255) / 255.0F;
            g = (float) (color >> 8 & 255) / 255.0F;
            b = (float) (color & 255) / 255.0F;
        }


        buffer.addVertex(matrix, 0, -0.055f / 18 * height.get(), -0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height.get(), -0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height.get(), 0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height.get(), 0.055f / 18 * width.get()).setColor(r, g, b, a).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);


        if (overlay_draw.get()) {
            BookImage ov_img = overlay_image.get();
            VertexConsumer buffer2 = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(ov_img.imageLoc)));

            float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
            float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
            float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
            float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;

            float overlay_a = 1;
            float overlay_r = 1;
            float overlay_g = 1;
            float overlay_b = 1;

            if (color != -1) {
                overlay_r = (float) (color >> 16 & 255) / 255.0F;
                overlay_g = (float) (color >> 8 & 255) / 255.0F;
                overlay_b = (float) (color & 255) / 255.0F;
            }

            matrixStack.pushPose();
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, -0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u1, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, 0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            buffer2.addVertex(matrix, ov_img.z / 2000f, -0.055f / 18 * ov_img.height, 0.055f / 18 * ov_img.width).setColor(overlay_r, overlay_g, overlay_b, overlay_a).setUv(overlay_u2, overlay_v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
            matrixStack.popPose();
        }

        matrixStack.popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public void drawTitle(BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, int light, int overlay, PageOn pageOn, boolean isItem) {

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);


        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8f / 16f, 5.5f / 16f, -0.012f / 16f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
        matrixStack.translate(-0.03f / 16f, -0.053f / 16f, 0);
        matrixStack.translate(4.75f / 16f, 0f / 16f, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/title.png")));

//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = matrixStack.last();
        int u = 0;
        int v = 0;
        int imageWidth = 128;
        int imageHeight = 128;
        int width = 100;
        int height = 26;
        float u1 = (u + 0.0F) / (float) imageWidth;
        float u2 = (u + (float) width) / (float) imageWidth;
        float v1 = (v + 0.0F) / (float) imageHeight;
        float v2 = (v + (float) height) / (float) imageHeight;

        buffer.addVertex(matrix, 0, -0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, -0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, 0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);
        buffer.addVertex(matrix, 0, -0.055f / 18 * height, 0.055f / 18 * width).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1F, 0F, 0F);

        matrixStack.popPose();


    }


    @OnlyIn(Dist.CLIENT)
    public void drawCharacter(char character, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float mouseX, float mouseY, int xIn, int yIn, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {


        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ClientProxy.TEXT.get(character));

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8.35f / 16f, 5.5f / 16f, -0.07f / 16f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(this.lineHeight + yIn * 0.05f, -this.lineWidth - (ClientProxy.TEXT_WIDTH.get(character) / 2) - xIn * 0.042f, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-90));

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());


//        matrixStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
        PoseStack.Pose normal = matrixStack.last();
        buffer.addVertex(matrix, -0.032f, -0.032f, 0.0f).setColor(0.12f, 0.12f, 0.12f, 1.0f).setUv(sprite.getU0(), sprite.getV0()).setOverlay(overlay).setLight(light).setNormal(normal, 1, 0, 0);
        buffer.addVertex(matrix, 0.032f, -0.032f, 0.0f).setColor(0.12f, 0.12f, 0.12f, 1.0f).setUv(sprite.getU0(), sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(normal, 1, 0, 0);
        buffer.addVertex(matrix, 0.032f, 0.032f, 0.0f).setColor(0.12f, 0.12f, 0.12f, 1.0f).setUv(sprite.getU1(), sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(normal, 1, 0, 0);
        buffer.addVertex(matrix, -0.032f, 0.032f, 0.0f).setColor(0.12f, 0.12f, 0.12f, 1.0f).setUv(sprite.getU1(), sprite.getV0()).setOverlay(overlay).setLight(light).setNormal(normal, 1, 0, 0);

        //shadow for special font
//        matrixStack.translate(0.001,0.001,-0.001);
//        normal = matrixStack.last().normal();
//        buffer.addVertex(matrix, -0.032f, -0.032f, 0.0f).setColor(0.03f,0.03f,0.03f, 1.0f).setUv(sprite.getU0(), sprite.getV0()).setOverlay()(overlay).setLight()(light).setNormal()(normal, 1,0,0);
//        buffer.addVertex(matrix, 0.032f, -0.032f, 0.0f) .setColor(0.03f,0.03f,0.03f, 1.0f).setUv(sprite.getU0(), sprite.getV1()).setOverlay()(overlay).setLight()(light).setNormal()(normal, 1,0,0);
//        buffer.addVertex(matrix, 0.032f, 0.032f, 0.0f)  .setColor(0.03f,0.03f,0.03f, 1.0f).setUv(sprite.getU1(), sprite.getV1()).setOverlay()(overlay).setLight()(light).setNormal()(normal, 1,0,0);
//        buffer.addVertex(matrix, -0.032f, 0.032f, 0.0f) .setColor(0.03f,0.03f,0.03f, 1.0f).setUv(sprite.getU1(), sprite.getV0()).setOverlay()(overlay).setLight()(light).setNormal()(normal, 1,0,0);
        matrixStack.popPose();

    }

    public void resetLines() {
        this.lineWidth = 0;
        this.lineHeight = 0;
    }

    @OnlyIn(Dist.CLIENT)
    public BookParagraphElements resetLinesNewBox(BookParagraph bookParagraph, int boxOn) {
        this.lineWidth = 0;
        this.lineHeight = 0;
        if (boxOn + 1 < bookParagraph.paragraphElements.toArray().length)
            return (BookParagraphElements) (bookParagraph.paragraphElements.toArray()[boxOn + 1]);
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawString(BookParagraph bookParagraph, BookOfShadowsAltarTile tileEntityIn, PoseStack matrixStack, MultiBufferSource bufferSource, float mouseX, float mouseY, float zLevel, int light, int overlay, PageOn pageOn, boolean isItem) {

        MutableComponent pageText = bookParagraph.translatablePassage;
        int wordNumber = -1;
        int boxOn = 0;
        BookParagraphElements activeElement = (bookParagraph.paragraphElements.get(0));


        boolean drawSpecialFont = false;// HexConfig.FANCY_FONT_IN_BOOK.get();
//
//
//        if(drawSpecialFont){
//            boolean findNewWord = true;
//            String[] words = pageText.getString().trim().split("\\s+");
//            String pageTextString = pageText.getString();
//            int itor = -1;
//            for(String word : words){
//                itor++;
//                if(word.length() > 2) {
//                    StringBuilder stringBuilder = new StringBuilder();
//                    if (word.charAt(0) == '%' && word.charAt(1) == 'k') {
//                        for(int i = 2; i < word.length(); i++){
//                            stringBuilder.append(word.charAt(i));
//                        }
//                        String temp = stringBuilder.toString();
//
//                        String alt = "key." + temp;
//
//                        for (KeyMapping k : ClientProxy.keys) {
//                            String name = k.getName();
//                            if (name.equals(temp) || name.equals(alt)) {
//                                String keyName = k.getTranslatedKeyMessage().getString();
//                                if(keyName.length() <= 1)
//                                    keyName = keyName.toUpperCase(Locale.ROOT);
//                                words[itor] = keyName;
//                                pageTextString = pageTextString.replaceAll(word, words[itor]);
//                            }
//                        }
//
//                    }
//                }
//            }
//            char[] text = pageTextString.toCharArray();
//
//            int[] wordLength = new int[words.length];
//            float[] wordWidths = new float[words.length];
//            for (int k = 0; k < words.length; k++) {
//                wordLength[k] = words[k].length();
//                char[] wordText = words[k].toCharArray();
//                for (char character : wordText) {
//                    if (ClientProxy.TEXT.containsKey(character))
//                        wordWidths[k] += ClientProxy.TEXT_WIDTH.get(character);
//                    else
//                        wordWidths[k] += ClientProxy.TEXT_WIDTH.get(' ');
//                }
//            }
//
//            boolean breakBool = false;
//            for (int i = 0; i < text.length; i = i) {
//                if (breakBool)
//                    break;
//                if (text[i] == '\n') {
//                    this.lineWidth = 0;
//                    this.lineHeight += 0.05f;
//                    if (this.lineHeight >= activeElement.height * 0.05f) {
//                        activeElement = resetLinesNewBox(bookParagraph, boxOn++);
//                        if (activeElement == null) {
//                            breakBool = true;
//                            break;
//
//                        }
//                    }
//                    i++;
//                } else if (text[i] == ' ') {
//                    findNewWord = true;
//                    drawCharacter(' ', tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
//                    this.lineWidth += ClientProxy.TEXT_WIDTH.get(' ');
//                    if (this.lineWidth > activeElement.width * 0.02) {
//                        this.lineWidth = 0;
//                        this.lineHeight += 0.05f;
//                        if (this.lineHeight >= activeElement.height * 0.05f) {
//                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
//                            if (activeElement == null) {
//                                breakBool = true;
//                                break;
//
//                            }
//                        }
//                    }
//                    i++;
//                } else if (findNewWord) {
//                    wordNumber++;
//
//                    char[] wordText = words[wordNumber].toCharArray();
//                    if (this.lineWidth + wordWidths[wordNumber] > activeElement.width * 0.02) {
//                        this.lineWidth = 0;
//                        this.lineHeight += 0.05f;
//                        if (this.lineHeight >= activeElement.height * 0.05f) {
//                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
//                            if (activeElement == null) {
//                                breakBool = true;
//                                break;
//
//                            }
//                        }
//                    }
//                    for (char character : wordText) {
//                        if (ClientProxy.TEXT.containsKey(character)) {
//                            drawCharacter(character, tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
//                            this.lineWidth += ClientProxy.TEXT_WIDTH.get(character);
//                            if (this.lineWidth > activeElement.width * 0.02) {
//                                this.lineWidth = 0;
//                                this.lineHeight += 0.05f;
//                                if (this.lineHeight >= activeElement.height * 0.05f) {
//                                    activeElement = resetLinesNewBox(bookParagraph, boxOn++);
//                                    if (activeElement == null) {
//                                        breakBool = true;
//                                        break;
//
//                                    }
//                                }
//                            }
//                        } else {
//                            drawCharacter(' ', tileEntityIn, matrixStack, bufferSource, 0, 0, (int) activeElement.x, (int) activeElement.y, 0, light, overlay, pageOn, isItem);
//                            this.lineWidth += ClientProxy.TEXT_WIDTH.get(' ');
//                            if (this.lineWidth > activeElement.width * 0.02) {
//                                this.lineWidth = 0;
//                                this.lineHeight += 0.05f;
//                                if (this.lineHeight >= activeElement.height * 0.05f) {
//                                    activeElement = resetLinesNewBox(bookParagraph, boxOn++);
//                                    if (activeElement == null) {
//                                        breakBool = true;
//                                        break;
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    i += wordLength[wordNumber];
//                }
//            }
//        }


        Font font = Hexerei.font();
        boolean findNewWord = true;
        String[] words = pageText.getString().trim().split("\\s+");
        String pageTextString = pageText.getString();


        int itor = -1;
        for (String word : words) {
            itor++;
            if (word.length() > 2) {
                StringBuilder stringBuilder = new StringBuilder();
                if (word.charAt(0) == '%' && word.charAt(1) == 'k') {
                    for (int i = 2; i < word.length(); i++) {
                        stringBuilder.append(word.charAt(i));
                    }
                    String temp = stringBuilder.toString();

                    String alt = "key." + temp;

                    for (KeyMapping k : ClientProxy.keys) {
                        String name = k.getName();
                        if (name.equals(temp) || name.equals(alt)) {
                            String keyName = k.getTranslatedKeyMessage().getString();
                            if (keyName.length() <= 1)
                                keyName = keyName.toUpperCase(Locale.ROOT);
                            words[itor] = keyName;
                            pageTextString = pageTextString.replaceAll(word, words[itor]);
                        }
                    }

                }
            }

        }
        char[] text = pageTextString.toCharArray();

        int[] wordLength = new int[words.length];
        float[] wordWidths = new float[words.length];
        for (int k = 0; k < words.length; k++) {
            wordLength[k] = words[k].length();
            wordWidths[k] = font.width(words[k]);
        }

        boolean breakBool = false;
        ArrayList<String> strings = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length; i = i) {
            if (breakBool)
                break;
            if (text[i] == '\n') {
                this.lineWidth = 0;
                this.lineHeight++;
                strings.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                if (this.lineHeight >= activeElement.height) {
                    activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                    if (activeElement == null) {
                        breakBool = true;
                        break;

                    }
                }
                i++;
            } else if (text[i] == ' ') {
                findNewWord = true;
                stringBuilder.append(' ');
                this.lineWidth += font.width(" ");
                if (this.lineWidth > activeElement.width * 3.75f) {
                    this.lineWidth = 0;
                    this.lineHeight++;
                    strings.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    if (this.lineHeight >= activeElement.height) {
                        activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                        if (activeElement == null) {
                            breakBool = true;
                            break;

                        }
                    }
                }
                i++;
            } else if (findNewWord) {
                wordNumber++;

                char[] wordText = words[wordNumber].toCharArray();
                if (this.lineWidth > 0 && this.lineWidth + wordWidths[wordNumber] > activeElement.width * 3.75f) {
                    this.lineWidth = 0;
                    this.lineHeight++;
                    strings.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    if (this.lineHeight >= activeElement.height) {
                        activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                        if (activeElement == null) {
                            breakBool = true;
                            break;

                        }
                    }
                }
                for (char character : wordText) {
                    stringBuilder.append(character);
                    this.lineWidth += font.width(String.valueOf(character));
                    if (this.lineWidth > activeElement.width * 3.75f) {
                        this.lineWidth = 0;
                        this.lineHeight++;
                        strings.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        if (this.lineHeight >= activeElement.height) {
                            activeElement = resetLinesNewBox(bookParagraph, boxOn++);
                            if (activeElement == null) {
                                breakBool = true;
                                break;

                            }
                        }
                    }
                }

                i += wordLength[wordNumber];
            }
        }

        if (!stringBuilder.toString().isEmpty())
            strings.add(stringBuilder.toString());

        matrixStack.pushPose();

        if (pageOn == PageOn.LEFT_PAGE)
            translateToLeftPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_UNDER)
            translateToLeftPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.LEFT_PAGE_PREV)
            translateToLeftPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        if (pageOn == PageOn.RIGHT_PAGE)
            translateToRightPage(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_UNDER)
            translateToRightPageUnder(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);
        else if (pageOn == PageOn.RIGHT_PAGE_PREV)
            translateToRightPagePrevious(tileEntityIn, matrixStack, isItem, ItemDisplayContext.NONE);

        matrixStack.mulPose(Axis.YP.rotationDegrees(90));
        matrixStack.translate(-8.35f / 16f, 4.5f / 16f, -0.01f / 16f);
        matrixStack.scale(0.00272f, 0.00272f, 0.00272f);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//            font.drawInBatch(s1, (activeElement.x * 9f) - 24, (activeElement.y * 9f) - 4, 16777216, false, matrixStack.last().pose(), bufferSource, false, 0, light);


        int boxId = 0;
        int linenumber = 0;
        boolean flag = true;
        while (flag) {
            ArrayList<String> remainder = new ArrayList<>();
            if (bookParagraph.paragraphElements.size() > boxId && bookParagraph.paragraphElements.get(boxId) != null) {

                BookParagraphElements box = bookParagraph.paragraphElements.get(boxId);
                boolean alignVerticalMiddle = box.verticalAlign.equals("middle");

                float offsetY = 0;
                if (alignVerticalMiddle && Math.round(box.height * font.lineHeight) + 1 > strings.size())
                    offsetY = (box.height * font.lineHeight / 2f) - (strings.size() / 2f) * (font.lineHeight);

                for (String s1 : strings) {
                    if ((linenumber + 1) * font.lineHeight <= Math.round(box.height * font.lineHeight) + 1) {
                        float offsetX = 0;
                        if (bookParagraph.align.equals("middle"))
                            offsetX = (font.width(s1)) / 2;

                        font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + Math.round(linenumber * font.lineHeight)) - 4 + offsetY, HexereiUtil.getColorValue(0.12f, 0.12f, 0.12f), false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                        matrixStack.pushPose();
                        matrixStack.translate(0.25f, 0.25f, 1 / 16f);
                        font.drawInBatch(s1, (box.x * 8f) - 24 - offsetX, ((box.y) * (font.lineHeight) + Math.round(linenumber * font.lineHeight)) - 4 + offsetY, 16777216, false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                        matrixStack.popPose();
                    } else {
                        remainder.add(s1);
                    }
                    ++linenumber;
                }
            } else
                flag = false;
            if (remainder.isEmpty())
                flag = false;
            else {
                boxId++;
                linenumber = 0;
                strings = remainder;
            }
        }

//            font.drawInBatch(pageText, (xIn * 9f) - 24, (yIn * 9f) - 4, 16777216, false, matrixStack.last().pose(), bufferSource, false, 0, light);

        buffer.endBatch();
        matrixStack.popPose();

        resetLines();

    }

    public static enum PageOn {
        LEFT_PAGE,
        LEFT_PAGE_UNDER,
        LEFT_PAGE_PREV,
        RIGHT_PAGE,
        RIGHT_PAGE_UNDER,
        RIGHT_PAGE_PREV,
        MIDDLE_BUTTON

    }

}
