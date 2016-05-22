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

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class ItemMindControlHelmet extends Item {

    public ItemMindControlHelmet() {
        this.setCreativeTab(CreativeTabs.MISC);
        this.setUnlocalizedName("mindControlHelmet");
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityMiniPlayer) {
                EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;
                if (miniPlayer.getOwner() == player && !miniPlayer.isMindControlled()) {
                    miniPlayer.setMindControlled(true);
                    //todo check
                    player.addChatComponentMessage(new TextComponentString(miniPlayer.getDisplayName() + ": I will do anything you command of me Master..."));
                }
            }
            return true;
        }
        return false;
    }
}
