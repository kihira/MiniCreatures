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

package kihira.minicreatures.common.entity;

import kihira.minicreatures.common.customizer.EnumPartCategory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;
import java.util.EnumSet;

public interface IMiniCreature {

    public IInventory getInventory();

    public EntityLiving getEntity();

    public EnumSet<EnumPartCategory> getPartCatergoies();

    public ArrayList<String> getCurrentParts(boolean isPreview);

    public void setParts(ArrayList<String> parts, boolean isPreview);
}
