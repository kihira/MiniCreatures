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

package kihira.minicreatures.common;

import cpw.mods.fml.common.network.IGuiHandler;
import kihira.minicreatures.client.gui.GuiCustomizer;
import kihira.minicreatures.common.entity.ICustomisable;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        //Get Entity ID as x coord. Inspired by OpenBlocks
        switch (ID) {
            case (0): return new ContainerChest(player.inventory, ((IMiniCreature)world.getEntityByID(x)).getInventory());
            case (1): return new ContainerCarriedAnvil(player.inventory, world, x, y, z, player);
            default: return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case (0): return new GuiChest(player.inventory, ((IMiniCreature)world.getEntityByID(x)).getInventory());
            case (1): return new GuiRepair(player.inventory, world, x, y, z);
            case (2): return new GuiCustomizer((ICustomisable) world.getEntityByID(x));
            default: return null;
        }
    }
}
