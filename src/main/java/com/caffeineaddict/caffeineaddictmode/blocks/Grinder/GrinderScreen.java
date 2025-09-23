package com.caffeineaddict.caffeineaddictmode.blocks.Grinder;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.renderer.GameRenderer;

public class GrinderScreen extends AbstractContainerScreen<GrinderMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CaffeineAddictMode.MOD_ID, "textures/gui/grinder_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            new ResourceLocation(CaffeineAddictMode.MOD_ID, "textures/gui/arrow.png");

    public GrinderScreen(GrinderMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    // 진행바 표시
    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE); // grinder_gui.png
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isCrafting()) {
            RenderSystem.setShaderTexture(0, ARROW_TEXTURE); // arrow.png
            int progress = menu.getScaledProgress(); // 0~24 크기
            blit(poseStack, leftPos + 79, topPos + 34, 0, 0, progress, 17, 24, 17);
        }
    }


    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, 8, 6, 4210752);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }
}