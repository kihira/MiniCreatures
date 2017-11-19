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

package kihira.minicreatures.common.handler;

import kihira.minicreatures.client.gui.GuiCustomizer;
import kihira.minicreatures.client.gui.GuiRoleSelect;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ICustomisable;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        //Get Entity ID as x coord. Inspired by OpenBlocks
        switch (ID) {
            case (0): return new ContainerChest(player.inventory, ((IMiniCreature)world.getEntityByID(x)).getInventory(), player);
            case (1): return new ContainerRepair(player.inventory, world, player) {
                @Override
                public boolean canInteractWith(EntityPlayer playerIn) {
                    return true;
                }
            };
            default: return null;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case (0): return new GuiChest(player.inventory, ((IMiniCreature)world.getEntityByID(x)).getInventory());
            case (1): return new GuiRepair(player.inventory, world);
            case (2): return new GuiCustomizer((ICustomisable) world.getEntityByID(x));
            case (3): return new GuiRoleSelect((EntityMiniPlayer) world.getEntityByID(x));
            default: return null;
        }
    }
}
