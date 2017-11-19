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

package kihira.minicreatures.common.item;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.ICustomisable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCustomizer extends Item {

    public ItemCustomizer() {
        this.setUnlocalizedName("customizer");
        this.setRegistryName(MiniCreatures.MODID, "customizer");
        this.setCreativeTab(CreativeTabs.MISC);
        this.setMaxStackSize(1);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof ICustomisable) {
            player.openGui(MiniCreatures.instance, 3, player.world, entity.getEntityId(), 0, 0);
            return true;
        }
        return false;
    }
}
