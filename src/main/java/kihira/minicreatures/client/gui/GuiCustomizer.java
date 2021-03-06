/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures.client.gui;

import com.google.common.base.Strings;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.entity.ICustomisable;
import kihira.minicreatures.common.network.UpdateEntityMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GuiCustomizer extends GuiScreen {

    private final ICustomisable miniCreature;
    private final ResourceLocation guiTextures = new ResourceLocation("minicreatures", "textures/gui/customizer.png");
    private int guiLeft;
    private int guiTop;
    private int xSize = 205;
    private int ySize = 170;
    private int currentPage = 0;
    private GuiButton categoryButton;
    private EnumPartCategory currentCategory = EnumPartCategory.ALL;
    private String[] partsList = new String[6];
    private ArrayList<String> currentValidParts;
    private ArrayList<String> currentEquippedParts;
    //This should never be changed except during init
    private ArrayList<String> originalParts;

    public GuiCustomizer(ICustomisable entity) {
        this.miniCreature = entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Add in category button
        this.categoryButton = new GuiButton(0, this.guiLeft + 83, this.guiTop + 7, 112, 20, I18n.format("category." + this.currentCategory.name() + ".part"));
        this.buttonList.add(0, this.categoryButton);
        //Add in parts buttons
        for (int i = 1; i < 7; i++) {
            this.buttonList.add(i, new GuiButton(i, this.guiLeft + 84, this.guiTop + 9 + (i * 21), 92, 20, ""));
        }
        //Add in navigation buttons
        this.buttonList.add(7, new GuiButton(7, this.guiLeft + 84, this.height / 2 + 54, 20, 20, "<"));
        this.buttonList.add(8, new GuiButton(8, this.guiLeft + 156, this.height / 2 + 54, 20, 20, ">"));
        this.buttonList.add(9, new GuiButton(9, this.guiLeft + 105, this.height / 2 + 54, 50, 20, I18n.format("gui.done")));
        //Add in reset buttons
        this.buttonList.add(10, new GuiButton(10, this.guiLeft + 8, this.guiTop + 95, 72, 20, I18n.format("gui.reset")));
        this.buttonList.add(11, new GuiButton(11, this.guiLeft + 8, this.guiTop + 116, 72, 20, I18n.format("gui.clear")));

        //Load current part data.
        this.currentEquippedParts = this.miniCreature.getCurrentParts(false);
        this.originalParts = new ArrayList<>(this.currentEquippedParts);
        this.miniCreature.setParts(this.currentEquippedParts, true);

        //Always perform this last
        updatePartsList();
        updateNavButtons();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void actionPerformed(GuiButton button) {
        //Category button
        if (button.id == 0) {
            EnumPartCategory[] partCategories = this.miniCreature.getPartCatergoies().toArray(new EnumPartCategory[20]);
            if (this.currentCategory.ordinal() + 1 >= this.miniCreature.getPartCatergoies().size()) this.currentCategory = EnumPartCategory.ALL;
            else this.currentCategory = partCategories[this.currentCategory.ordinal() + 1];
            this.categoryButton.displayString = I18n.format("category." + this.currentCategory.name() + ".part");
        }
        //Nav button
        else if (button.id == 7) this.currentPage--;
        else if (button.id == 8) this.currentPage++;
        else if (button.id == 9) closeGUI(true);
        //Parts buttons
        if (button.id > 0 && button.id < 7) {
            String partName = this.partsList[button.id - 1];
            if (this.currentEquippedParts.contains(partName)) this.currentEquippedParts.remove(partName);
            else this.currentEquippedParts.add(partName);
            this.miniCreature.setParts(this.currentEquippedParts, true);
        }
        //Reset button
        if (button.id == 10) {
            this.miniCreature.setParts(this.originalParts, true);
            this.currentEquippedParts = new ArrayList<>(this.originalParts);
        }
        //Clear button
        if (button.id == 11) {
            this.miniCreature.setParts(new ArrayList<>(), true);
            this.currentEquippedParts = new ArrayList<>();
        }

        //Update everything just to be safe
        updatePartsList();
        updateNavButtons();
    }

    private void updateNavButtons() {
        this.buttonList.get(7).enabled = this.currentPage != 0;
        this.buttonList.get(8).enabled = !((this.currentPage + 1) * 6 >= this.currentValidParts.size());
    }

    private void updatePartsList() {
        this.currentValidParts = CustomizerRegistry.getValidParts(this.miniCreature.getEntity(), this.currentCategory);
        this.partsList = new String[6];
        for (int i = 1; i < 7; i++) {
            int num = (this.currentPage * 6) + i - 1;
            GuiButton button = this.buttonList.get(i);
            if (this.currentValidParts.size() > num) {
                button.visible = true;
                button.displayString = I18n.format("name." + this.currentValidParts.get(num) + ".part");
                this.partsList[num] = this.currentValidParts.get(num);
            }
            else {
                button.visible = false;
            }
        }
    }

    private void closeGUI(boolean shouldUpdate) {
        if (shouldUpdate) {
            MiniCreatures.proxy.simpleNetworkWrapper.sendToServer(new UpdateEntityMessage(this.miniCreature.getEntity().getEntityId(), this.currentEquippedParts));
        }
        else {
            this.miniCreature.setParts(this.originalParts, false);
        }
        this.mc.displayGuiScreen(null);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == 1) closeGUI(false);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawBackground(par1, par2);
        drawForeground(par1, par2);
        super.drawScreen(par1, par2, par3);
    }

    private void drawForeground(int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(this.guiTextures);
        //Render checkmarks if part is currently equipped
        for (int i = 0; i < 6; i++) {
            if (!Strings.isNullOrEmpty(this.partsList[i])) {
                if (this.currentEquippedParts.contains(this.partsList[i])) this.drawTexturedModalRect(this.guiLeft + 174, this.guiTop + 10 + ((i + 1) * 20), 0, 168, 20, 20);
                else this.drawTexturedModalRect(this.guiLeft + 174, this.guiTop + 9 + ((i + 1) * 21), 22, 168, 20, 20);
            }
        }
    }

    private void drawBackground(int p_146976_2_, int p_146976_3_) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(this.guiTextures);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GuiInventory.drawEntityOnScreen(this.guiLeft + 42, this.guiTop + 82, 45, (float) (this.guiLeft + 51) - p_146976_2_, (float) (this.guiTop + 75 - 50) - p_146976_3_, this.miniCreature.getEntity());
    }
}
