package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joefoxe.hexerei.container.HerbJarContainer.OFFSET;

public class HerbJarScreen extends AbstractContainerScreen<HerbJarContainer> {
    private final ResourceLocation GUI = HexereiUtil.getResource(
            "textures/gui/herb_jar_gui.png");
    private final ResourceLocation INVENTORY = HexereiUtil.getResource(
            "textures/gui/inventory.png");

    public HerbJarScreen(HerbJarContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        inventoryLabelY = 135 - OFFSET;
        inventoryLabelX = 8;
        titleLabelY = 1 - OFFSET;
        titleLabelX = 52;
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
        this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(GUI, i, j - 3 - OFFSET, 0, 0, 214, 157);

        if(this.menu.getToggled() == 1)
        {
            guiGraphics.blit(GUI, i + 82, j + 105 - OFFSET, 238, 26, 18, 18);

        }
        guiGraphics.blit(GUI, i + 78, j - 30 - OFFSET, 230, 0, 26, 26);
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        RenderSystem.disableDepthTest();
        guiGraphics.renderItem(this.menu.stack,
                this.leftPos + 83,
                this.topPos - 25 - OFFSET);
        RenderSystem.enableDepthTest();

        guiGraphics.blit(INVENTORY, i + 3, j + 129 - OFFSET, 0, 0, 176, 100);





    }



    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean mouseClicked = super.mouseClicked(x, y, button);


        if(x > this.leftPos + 82 && x <= this.leftPos + 82 + 18 &&  y >= this.topPos + 105 - OFFSET && y < this.topPos + 105 + 18 - OFFSET){
            this.menu.setToggled(1 - this.menu.getToggled());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
//        this.menu.playSound();

        return mouseClicked;
    }


    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
    }

    public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY){
        List<Component> components = new ArrayList<>();
        if (isHovering(mouseX, mouseY, 82D, 105 - OFFSET, 18D, 18D)) {
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button"));
            if (Screen.hasShiftDown()) {
                components.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            } else {
                components.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}