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

package kihira.minicreatures.client.model.parts;

import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.customizer.ICustomizerPartClient;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PartModelTail extends ModelBase implements ICustomizerPartClient {

    ModelRenderer tailBase;
    ModelRenderer tailPart1;
    ModelRenderer tailPart2;
    ModelRenderer tailTip;

    public PartModelTail() {
        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-1.5F, 8.8F, 3.25F, 3, 3, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);
        tailBase.rotateAngleX = -0.1570796F;
        tailPart1 = new ModelRenderer(this, 0, 5);
        tailPart1.addBox(-1F, 6.75F, 8F, 2, 2, 3);
        tailPart1.setRotationPoint(0F, 0F, 0F);
        tailPart1.rotateAngleX = -0.5235988F;
        tailPart2 = new ModelRenderer(this, 0, 10);
        tailPart2.addBox(-1F, -0.7F, 12.9F, 2, 2, 4);
        tailPart2.setRotationPoint(0F, 0F, 0F);
        tailPart2.rotateAngleX = -1.134464F;
        tailTip = new ModelRenderer(this, 0, 16);
        tailTip.addBox(-0.5F, 14.8F, -7.7F, 1, 4, 1);
        tailTip.setRotationPoint(0F, 0F, 0F);
        tailTip.rotateAngleX = 0.8726646F;
    }

    @Override
    public boolean isPartValidForEntity(Entity entity, EnumPartCategory enumPartCategory) {
        return (entity instanceof EntityMiniPlayer && enumPartCategory == EnumPartCategory.ALL || enumPartCategory == EnumPartCategory.BODY);
    }

    @Override
    public void render(Entity entity, ModelBase modelMiniPlayer, float par2, float par3, float par4, float par5, float par6, float par7) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.specialTextures);
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / 2F, 1.0F / 2F, 1.0F / 2F);
        GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
        this.tailBase.render(par7);
        this.tailPart1.render(par7);
        this.tailPart2.render(par7);
        this.tailTip.render(par7);
        GL11.glPopMatrix();
    }
}
