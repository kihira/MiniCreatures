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
import kihira.minicreatures.common.entity.EntityFox;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFox extends RenderLiving {

    private final ResourceLocation foxTexture = new ResourceLocation("minicreatures", "textures/entity/fox.png");
    private final ResourceLocation foxCollarTexture = new ResourceLocation("minicreatures", "textures/entity/foxcollar.png");

    public RenderFox() {
        super(new ModelFox(), 0.4F);
        this.setRenderPassModel(this.mainModel);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        EntityFox entityFox = (EntityFox) par1EntityLivingBase;
        if (par2 == 1 && entityFox.isTamed()) {
            this.bindTexture(this.foxCollarTexture);
            int j = entityFox.getCollarColor();
            GL11.glColor3f(EntitySheep.fleeceColorTable[j][0], EntitySheep.fleeceColorTable[j][1], EntitySheep.fleeceColorTable[j][2]);
            return 1;
        }
        else return -1;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.foxTexture;
    }
}
