/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.client.gui;

import com.google.common.base.Strings;
import com.mojang.realmsclient.gui.ChatFormatting;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ai.EnumRole;
import kihira.minicreatures.common.network.RoleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiRoleSelect extends GuiBaseScreen {

    private final ResourceLocation guiTextures = new ResourceLocation("minicreatures", "textures/gui/roleSelect.png");
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

        //Roles
        int offset = 0;
        for (EnumRole role : EnumRole.values()) {
            buttonList.add(new GuiRoleSelectButton(role.ordinal() + 1, guiLeft + 85, guiTop + 22 + (offset * 22), role, miniPlayer.getRole() == role, ""));
            offset++;
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Background
        mc.renderEngine.bindTexture(guiTextures);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, guiWidth, guiHeight);

        drawString(fontRendererObj, I18n.format("gui.roleselect.select"), guiLeft + 8, guiTop + 8, 0xFFFFFF);
        GuiInventory.drawEntityOnScreen(guiLeft + 43, guiTop + 105, 52, guiLeft + 43 - mouseX, guiTop + 65 - mouseY, miniPlayer);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof GuiRoleSelectButton) {
            MiniCreatures.proxy.simpleNetworkWrapper.sendToServer(new RoleMessage(miniPlayer.getEntityId(), ((GuiRoleSelectButton) button).role));
            mc.displayGuiScreen(null);
        }
        super.actionPerformed(button);
    }

    private class GuiRoleSelectButton extends GuiButton implements ITooltip {

        private final EnumRole role;
        private boolean selected;

        public GuiRoleSelectButton(int id, int x, int y, EnumRole role, boolean selected, String tooltip) {
            super(id, x, y, 120, 20, tooltip);
            this.role = role;
            this.selected = selected;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible) {
                GlStateManager.color(1F, 1F, 1F);
                mc.renderEngine.bindTexture(guiTextures);
                hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int colour = selected ? 0x2200ff00 : 0x22000000;
                drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + height, colour, colour);
                colour = colour | 0x33 << 24;
                drawGradientRect(xPosition, yPosition, xPosition + width - 20, yPosition + height, colour, colour);
                drawTexturedModalRect(xPosition + width - 20, yPosition + height - 20, (role.ordinal() - 1) * 20, 153, 20, 20);
                drawString(fontRendererObj, I18n.format("role." + role.name() + ".name"), xPosition + 3, yPosition + 6, 0xFFFFFF);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<String> getTooltip(int mouseX, int mouseY) {
            List<String> list = new ArrayList<>();
            if (selected) list.add(ChatFormatting.GREEN + "" + ChatFormatting.ITALIC + I18n.format("gui.roleselect.selected"));
            if (!Strings.isNullOrEmpty(displayString)) {
                list.addAll(fontRendererObj.listFormattedStringToWidth(displayString, guiWidth));
            }
            return list;
        }
    }
}
