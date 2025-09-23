package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import static com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode.MOD_ID;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.network.BrewRequestPacket;
import com.caffeineaddict.caffeineaddictmode.registry.ModNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class CoffeeMachineScreen extends AbstractContainerScreen<CoffeeMachineMenu> {

    private static final ResourceLocation GUI = new ResourceLocation(MOD_ID, "/textures/gui/shotmachine_v3.png");
    private static final ResourceLocation PLAYER_INVENTORY_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public CoffeeMachineScreen(CoffeeMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 248;
        this.imageHeight = 172;
        this.inventoryLabelY = 140 + 6;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, PLAYER_INVENTORY_TEXTURE);
        this.blit(poseStack, leftPos+36, topPos+138, 0, 126, 176, 124, 256, 250);


        RenderSystem.setShaderTexture(0, GUI);

        this.blit(poseStack, leftPos, topPos-34, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        // Draw animated gauge bar for each input slot
        for (int i = 1; i < 4; i++) {
            int fill = menu.getProgressForSlot(i); // progress: 0-24
            int barWidth = (int)(40 * fill / 24.0); // 13 px max bar height

            int barX = leftPos + i*53;
            int barY = topPos + 58;
            // Debug test: 큰 빨간 네모 그려보기
            //this.fill(poseStack, leftPos, topPos, leftPos + 50, topPos + 50, 0xFFFF0000);

            this.fill(poseStack, barX, barY, barX + barWidth, barY + 9, 0xFF000000);
            this.blit(poseStack, barX, barY, 106, 92, barWidth - 2, 9, imageWidth, imageHeight);
        }
    }
//89,34
    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, 9, -30, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, 44, 139, 4210752);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    private void sendBrewRequest(int idx) {
        ModNetwork.sendToServer(new BrewRequestPacket(idx));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Log pressed key for debugging
        CaffeineAddictMode.LOGGER.info("[DEBUG] keyPressed: " + keyCode);

        if (keyCode == GLFW.GLFW_KEY_1) {
            CaffeineAddictMode.LOGGER.info("[DEBUG] Brew key 1 pressed!");
            sendBrewRequest(0);
            return true; // mark as handled
        }
        if (keyCode == GLFW.GLFW_KEY_2) {
            CaffeineAddictMode.LOGGER.info("[DEBUG] Brew key 2 pressed!");
            sendBrewRequest(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_3) {
            CaffeineAddictMode.LOGGER.info("[DEBUG] Brew key 3 pressed!");
            sendBrewRequest(2);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}