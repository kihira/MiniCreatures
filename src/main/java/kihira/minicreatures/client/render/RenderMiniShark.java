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

import kihira.minicreatures.client.model.ModelMiniShark;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderMiniShark extends RenderLiving {

    private final ResourceLocation sharkTexture = new ResourceLocation("minicreatures", "textures/entity/minishark.png");

    public RenderMiniShark() {
        super(new ModelMiniShark(), 0.4F);
        this.setRenderPassModel(new ModelMiniShark());
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return this.sharkTexture;
    }
}
