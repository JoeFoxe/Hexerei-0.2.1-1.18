package net.joefoxe.hexerei.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.container.PackageContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

import static net.joefoxe.hexerei.container.PackageContainer.OFFSET;

public class CourierPackageScreen extends AbstractContainerScreen<PackageContainer> {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/courier_package_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");


    boolean clicked = false;
    int clickedTicks = 0;
    int clickedTicksOld = 0;

    public CourierPackageScreen(PackageContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabelY = 1 - OFFSET;
        titleLabelX = 31 - 15;
        inventoryLabelY = 47 - OFFSET;
        imageHeight = 67;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {


        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 2); // change seal to translate
        pGuiGraphics.drawString(this.font, Component.translatable("hexerei.package_letter.seal"), 145, -7, this.menu.isEmpty() ? 0x333333 : 4210752, false);
        pGuiGraphics.pose().popPose();


    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int i = this.leftPos;
        int j = this.topPos - OFFSET;
        guiGraphics.blit(GUI, i, j - 3, 0, 0, 182, 69);
        guiGraphics.blit(GUI, i + 78 - 15, j - 30, 230, 0, 26, 26);

        guiGraphics.blit(INVENTORY, i + 3, j + 41, 0, 0, 176, 100);

        if (!this.menu.isEmpty()){

            if (clicked) {
                guiGraphics.blit(GUI, i + 141, j + 17, 1, 102, 30, 16);
            } else if (x > this.leftPos + 141 && x < this.leftPos + 141 + 30 && y > this.topPos + 17 - CofferContainer.OFFSET && y < this.topPos + 17 + 16 - CofferContainer.OFFSET) {
                guiGraphics.blit(GUI, i + 141, j + 17, 1, 118, 30, 16);
            } else {
                guiGraphics.blit(GUI, i + 141, j + 17, 1, 70, 30, 16);
            }

//        guiGraphics.blit(GUI, i + 140, j + 21, 3, 32, 70, Mth.clamp((int) (31 * (Mth.lerp(partialTicks, this.clickedTicksOld, this.clickedTicks) / 15f)), 0, 30), 7, 256, 256);
            float xOffset = Mth.clamp((35 * (Mth.lerp(partialTicks, this.clickedTicksOld, this.clickedTicks) / 15f)), 0, 34);
            float xOffset2 = Mth.clamp((35 * (Mth.lerp(partialTicks, this.clickedTicksOld - 8, this.clickedTicks - 8) / 10f)), 0, 36);
            float yOffset = Mth.clamp((18 * (Mth.lerp(partialTicks, this.clickedTicksOld - 13, this.clickedTicks - 13) / 5f)), 0, 17);
            floatBlit(guiGraphics, GUI,
                    i + 139, i + 139 + xOffset,
                    j + 21, j + 28,
                    3,
                    (32) / 256f, (32 + xOffset) / 256f,
                    (70) / 256f, (70 + 7) / 256f);
            floatBlit(guiGraphics, GUI,
                    i + 138, i + 138 + xOffset2,
                    j + 25, j + 29,
                    4,
                    (37) / 256f, (37 + xOffset2) / 256f,
                    (78) / 256f, (78 + 4) / 256f);
            floatBlit(guiGraphics, GUI,
                    i + 160, i + 160 + 4,
                    j + 16 + 17 - yOffset, j + 16 + 17,
                    5,
                    (32) / 256f, (32 + 4) / 256f,
                    (78 + 17 - yOffset) / 256f, (78 + 17) / 256f);
        } else {

            guiGraphics.blit(GUI, i + 141, j + 17, 1, 86, 30, 16);
        }


        RenderSystem.disableDepthTest();
        guiGraphics.renderItem(this.menu.getSelf(),
                i + 83 - 15,
                j - 25);

        RenderSystem.enableDepthTest();

    }

    void floatBlit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, pX1, pY1, pBlitOffset).setUv(pMinU, pMinV);
        bufferbuilder.addVertex(matrix4f, pX1, pY2, pBlitOffset).setUv(pMinU, pMaxV);
        bufferbuilder.addVertex(matrix4f, pX2, pY2, pBlitOffset).setUv(pMaxU, pMaxV);
        bufferbuilder.addVertex(matrix4f, pX2, pY1, pBlitOffset).setUv(pMaxU, pMinV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);

        if (button == 0){
            if (!this.menu.isEmpty()) {
                if (x > this.leftPos + 141 && x < this.leftPos + 141 + 30 && y > this.topPos + 17 - CofferContainer.OFFSET && y < this.topPos + 17 + 16 - CofferContainer.OFFSET) {
                    this.clicked = true;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.25f));
                }
            }
        }

        return mouseClicked;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        clickedTicksOld = clickedTicks;
        if(clicked && !this.menu.isEmpty()) {
            if (clickedTicks % 8 == 0)
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_INSERT, 1.0F, 0.75f));
            clickedTicks++;

            if (clickedTicks > 25) {
                clicked = false;
                if (this.menu.clickMenuButton(this.minecraft.player, 1) && this.minecraft.gameMode != null){

                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 1);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_DROP_CONTENTS, 1.3F, 0.75f));
                }
                onClose();
            }
        } else {
            clickedTicks--;
            if (clickedTicks < 0)
                clickedTicks = 0;
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.clicked && pButton == 0)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25f));
        this.clicked = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double x, double y, int pButton, double pDragX, double pDragY) {

        if (clicked){
            if (!(x > this.leftPos + 141 && x < this.leftPos + 141 + 30 && y > this.topPos + 19 - CofferContainer.OFFSET && y < this.topPos + 19 + 16 - CofferContainer.OFFSET)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25f));
                this.clicked = false;
            }
        }
        return super.mouseDragged(x, y, pButton, pDragX, pDragY);
    }
}