package net.joefoxe.hexerei.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.joefoxe.hexerei.client.renderer.entity.model.ArmorModels;
import net.joefoxe.hexerei.container.ModContainers;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.*;
import net.joefoxe.hexerei.light.LightManager;
import net.joefoxe.hexerei.screen.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static net.joefoxe.hexerei.fluid.ModFluidTypes.*;

public class ClientEvents {
    /**
     * Update light rendering.
     *
     * @param event the catched event.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            LightManager.updateAll(event.getLevelRenderer());
        }
    }

    @SubscribeEvent
    public static void registerMenu(final RegisterMenuScreensEvent event) {

        event.register(ModContainers.MIXING_CAULDRON_CONTAINER.get(), MixingCauldronScreen::new);
        event.register(ModContainers.COFFER_CONTAINER.get(), CofferScreen::new);
        event.register(ModContainers.PACKAGE_CONTAINER.get(), CourierPackageScreen::new);
        event.register(ModContainers.HERB_JAR_CONTAINER.get(), HerbJarScreen::new);
        event.register(ModContainers.BROOM_CONTAINER.get(), BroomScreen::new);
        event.register(ModContainers.CROW_CONTAINER.get(), CrowScreen::new);
        event.register(ModContainers.OWL_CONTAINER.get(), OwlScreen::new);
        event.register(ModContainers.CROW_FLUTE_CONTAINER.get(), CrowFluteScreen::new);
        event.register(ModContainers.WOODCUTTER_CONTAINER.get(), WoodcutterScreen::new);
    }


    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {

        BroomItemRenderer broomRenderer = new BroomItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return broomRenderer.getRenderer();
            }
        }, ModItems.WILLOW_BROOM.get(), ModItems.MAHOGANY_BROOM.get(), ModItems.WITCH_HAZEL_BROOM.get());

        CandleItemRenderer candleItemRenderer = new CandleItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return candleItemRenderer.getRenderer();
            }
        }, ModItems.CANDLE.get());

        BroomKeychainItemRenderer broomKeychainItemRenderer = new BroomKeychainItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return broomKeychainItemRenderer.getRenderer();
            }
        }, ModItems.BROOM_KEYCHAIN.get());

        ChestItemRenderer chestItemRenderer = new ChestItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return chestItemRenderer.getRenderer();
            }
        }, ModItems.MAHOGANY_CHEST.get(), ModItems.WILLOW_CHEST.get(), ModItems.WITCH_HAZEL_CHEST.get());

        MixingCauldronItemRenderer mixingCauldronItemRenderer = new MixingCauldronItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return mixingCauldronItemRenderer.getRenderer();
            }
        }, ModItems.MIXING_CAULDRON.get());

        HerbJarItemRenderer herbJarItemRenderer = new HerbJarItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return herbJarItemRenderer.getRenderer();
            }
        }, ModItems.HERB_JAR.get());

        CrowBlankAmuletItemRenderer crowBlankAmuletItemRenderer = new CrowBlankAmuletItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return crowBlankAmuletItemRenderer.getRenderer();
            }
        }, ModItems.CROW_BLANK_AMULET.get());

        CustomItemRendererWithPageDrawing renderer = new HexereiBookItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.getRenderer();
            }
        }, ModItems.BOOK_OF_SHADOWS.get());

        event.registerItem(new IClientItemExtensions() {
            @Override
            public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ArmorModels.get(itemStack);
            }
        }, ModItems.WITCH_HELMET, ModItems.WITCH_CHESTPLATE, ModItems.WITCH_BOOTS, ModItems.MUSHROOM_WITCH_HAT);




        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation STILL = BLOOD_STILL_RL,
                    FLOW = BLOOD_FLOWING_RL,
                    OVERLAY = BLOOD_OVERLAY_RL,
                    VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/nether_wart_block.png");

            @Override
            public ResourceLocation getStillTexture()
            {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return VIEW_OVERLAY;
            }

            @Override
            public int getTintColor()
            {
                return 0xF9FFFFFF;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
            {
                int color = this.getTintColor();
                return new Vector3f(48f / 256f, 4f / 255f, 4f / 255f);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
            {
                nearDistance = -8F;
                farDistance = 4F;

                if (farDistance > renderDistance)
                {
                    farDistance = renderDistance;
                    shape = FogShape.CYLINDER;
                }

                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        }, BLOOD_FLUID_TYPE);

        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation STILL = TALLOW_STILL_RL,
                    FLOW = TALLOW_FLOWING_RL,
                    OVERLAY = TALLOW_OVERLAY_RL,
                    VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/sand.png");

            @Override
            public ResourceLocation getStillTexture()
            {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return VIEW_OVERLAY;
            }

            @Override
            public int getTintColor()
            {
                return 0xF9FFFFFF;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
            {
                int color = this.getTintColor();
                return new Vector3f(153f / 256f, 153f / 255f, 114f / 255f);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
            {
                nearDistance = -8F;
                farDistance = 3F;

                if (farDistance > renderDistance)
                {
                    farDistance = renderDistance;
                    shape = FogShape.CYLINDER;
                }

                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        }, TALLOW_FLUID_TYPE);

        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation STILL = QUICKSILVER_STILL_RL,
                    FLOW = QUICKSILVER_FLOWING_RL,
                    OVERLAY = QUICKSILVER_OVERLAY_RL,
                    VIEW_OVERLAY = ResourceLocation.parse("minecraft:textures/block/red_sand.png");

            @Override
            public ResourceLocation getStillTexture()
            {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return VIEW_OVERLAY;
            }

            @Override
            public int getTintColor()
            {
                return 0xF9FFFFFF;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
            {
                int color = this.getTintColor();
                return new Vector3f(80f / 256f, 80f / 255f, 80f / 255f);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
            {
                nearDistance = -8F;
                farDistance = 3F;

                if (farDistance > renderDistance)
                {
                    farDistance = renderDistance;
                    shape = FogShape.CYLINDER;
                }

                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        }, QUICKSILVER_FLUID_TYPE);
    }

}
