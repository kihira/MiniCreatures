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

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.Set;

//Some code by cpw
public class ConfigGuiFactory implements IModGuiFactory {
    public static class ConfigGuiScreen extends GuiScreen {
        private GuiScreen parent;

        public ConfigGuiScreen(GuiScreen parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initGui() {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        }

        @Override
        protected void actionPerformed(GuiButton par1GuiButton) {
            if (par1GuiButton.enabled && par1GuiButton.id == 1) FMLClientHandler.instance().showGuiScreen(parent);
        }

        @Override
        public void drawScreen(int par1, int par2, float par3) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "Mini Creatures Test Config Screen", this.width / 2, 40, 0xFFFFFF);
            super.drawScreen(par1, par2, par3);
        }
    }

    @SuppressWarnings("unused")
    private Minecraft minecraft;

    @Override
    public void initialize(Minecraft minecraftInstance) {
        this.minecraft = minecraftInstance;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigGuiScreen.class;
    }

    //Not used
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    //Not used
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return new RuntimeOptionGuiHandler() {
            @Override
            public void paint(int x, int y, int w, int h) { }

            @Override
            public void close() { }

            @Override
            public void addWidgets(List<Gui> widgets, int x, int y, int w, int h) { }

            @Override
            public void actionCallback(int actionId) { }
        };
    }
}
