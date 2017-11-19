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

import kihira.minicreatures.client.model.ModelFox;
import kihira.minicreatures.client.render.layers.LayerCollar;
import kihira.minicreatures.common.entity.EntityMiniCreature;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniCreature<T extends EntityMiniCreature> extends RenderLiving<T> {

    private final ResourceLocation texture;

    public RenderMiniCreature(RenderManager manager, float shadow, ResourceLocation texture, ResourceLocation collarTexture) {
        super(manager, new ModelFox(), shadow);
        this.texture = texture;
        addLayer(new LayerCollar<RenderMiniCreature>(this, collarTexture));
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
