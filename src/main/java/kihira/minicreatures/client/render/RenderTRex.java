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

package kihira.minicreatures.client.render;

import kihira.minicreatures.client.model.ModelTRex;
import kihira.minicreatures.common.entity.EntityMiniTRex;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTRex extends RenderLiving<EntityMiniTRex> {
    private static final ResourceLocation TREX_TEXTURE = new ResourceLocation("minicreatures", "textures/entity/trex.png");

    public RenderTRex(RenderManager manager) {
        super(manager, new ModelTRex(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMiniTRex entity) {
        return TREX_TEXTURE;
    }
}
