/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.client.gui;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ai.EnumRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiRoleSelect extends GuiScreen {

    private final ResourceLocation guiTextures = new ResourceLocation("minicreatures", "textures/gui/roleselect.png");
    private final int guiWidth = 216;
    private final int guiHeight = 153;
    private final EntityMiniPlayer miniPlayer;
    private int guiLeft;
    private int guiTop;

    public GuiRoleSelect(EntityMiniPlayer miniPlayer) {
        this.miniPlayer = miniPlayer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        guiLeft = (width - guiWidth) / 2;
        guiTop = (height - guiHeight) / 2;
        int guiBottom = guiTop + guiHeight;

        buttonList.add(new GuiButton(0, guiLeft + 8, guiBottom - 33, 50, 20, "Cancel"));

        //Roles
        int offset = 0;
        for (EnumRole role : EnumRole.values()) {
            buttonList.add(new GuiRoleSelectButton(role.ordinal() + 1, guiLeft + 185, guiTop + 22 + (offset * 22), role, ""));
            offset++;
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Background
        mc.renderEngine.bindTexture(guiTextures);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, guiWidth, guiHeight);

        //Roles
        int offset = 0;
        for (EnumRole role : EnumRole.values()) {
            mc.renderEngine.bindTexture(guiTextures);
            drawGradientRect(guiLeft + 85, guiTop + 22 + (offset * 22), guiLeft + 185, guiTop + 42 + (offset * 22), 0x22000000, 0x22000000);
            drawString(fontRendererObj, role.name(), guiLeft + 88, guiTop + 22 + (offset * 22) + 6, 0xFFFFFF);
            offset++;
        }

        drawString(fontRendererObj, "Select your Mini Players role", guiLeft + 8, guiTop + 8, 0xFFFFFF);
        GuiInventory.func_147046_a(guiLeft + 43, guiTop + 105, 52, guiLeft + 43 - mouseX, guiTop + 65 - mouseY, miniPlayer);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiRoleSelectButton) {
            //TODO this might need to be a packet?
            miniPlayer.setRole(((GuiRoleSelectButton) button).role);
            mc.displayGuiScreen(null);
        }
        super.actionPerformed(button);
    }

    private class GuiRoleSelectButton extends GuiButton {

        private final EnumRole role;

        public GuiRoleSelectButton(int id, int x, int y, EnumRole role, String tooltip) {
            super(id, x, y, 21, 21, tooltip);
            this.role = role;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible) {
                GL11.glColor3f(1F, 1F, 1F);
                mc.renderEngine.bindTexture(guiTextures);
                drawTexturedModalRect(xPosition, yPosition, 44, 169, width, height);
            }
        }
    }
}
