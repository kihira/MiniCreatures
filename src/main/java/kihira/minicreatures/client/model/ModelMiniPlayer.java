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

package kihira.minicreatures.client.model;

import kihira.minicreatures.client.gui.GuiCustomizer;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.customizer.ICustomizerPart;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ICustomisable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import org.lwjgl.opengl.GL11;

/**
 * The model for {@link kihira.minicreatures.common.entity.EntityMiniPlayer}
 */
public class ModelMiniPlayer extends ModelBiped {

    public ModelMiniPlayer() {
        this(0.0F);
    }

    public ModelMiniPlayer(float par1) {
        super(par1, 0.0F, 64, 32);

        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.1F;
        this.bipedLeftLeg.rotationPointZ = 0.1F;
        this.bipedRightLeg.rotationPointY = 12.0F;
        this.bipedLeftLeg.rotationPointY = 12.0F;
        this.bipedHead.rotationPointY = 0.0F;
        this.bipedHeadwear.rotationPointY = 0.0F;
    }

    /**
     * Renders the model based off the parameters provided. Sets rotations then calls
     * {@link net.minecraft.client.model.ModelRenderer#render(float)}
     * @param entity The entity this model is used by
     * @param f
     * @param f1
     * @param f2
     * @param f3
     * @param f4
     * @param f5 A mystery number
     */
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        ICustomisable miniPlayer = (ICustomisable)entity;
        GL11.glPushMatrix();
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        float f6 = 2.0F;
        GL11.glPushMatrix();
        GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
        GL11.glTranslatef(0.0F, 16.0F * f5, 0.0F);
        this.bipedHead.render(f5);
        this.bipedHeadwear.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
        GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F);
        this.bipedBody.render(f5);
        this.bipedRightArm.render(f5);
        this.bipedLeftArm.render(f5);
        this.bipedRightLeg.render(f5);
        this.bipedLeftLeg.render(f5);
        GL11.glPopMatrix();

        for (String partName : miniPlayer.getCurrentParts(Minecraft.getMinecraft().currentScreen instanceof GuiCustomizer)) {
            ICustomizerPart part = CustomizerRegistry.getPart(partName);
            if (part != null) part.render(entity, this, f, f1, f2, f3, f4, f5);
        }
        GL11.glPopMatrix();
    }

    /**
     * Sets the models various rotation angles
     * @param par1 Swing speed/time
     * @param par2 Maximum swing angle
     * @param par3
     * @param par4 Head rotation angle y
     * @param par5 Head rotation angle x
     * @param par6
     * @param entity The entity
     */
    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, miniPlayer);

        if (this.isRiding) {
            GL11.glTranslatef(0F, 0.25F, 0F);
            if ((miniPlayer.ridingEntity instanceof EntityTameable) && (((EntityTameable) miniPlayer.ridingEntity).isSitting())) GL11.glTranslatef(0F, 0.1F, 0F);
        }

        if (this.heldItemRight != 0 && (miniPlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            this.bipedLeftArm.rotateAngleX = -0.8F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
            this.bipedRightArm.rotateAngleX = -0.8F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
        }

        if (miniPlayer.isSitting()) {
            GL11.glTranslatef(0F, 0.3F, 0F);
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
        }
    }

    /**
     * This is called in {@link net.minecraft.client.renderer.entity.RenderLiving} to set the various angles for the
     * various {@link net.minecraft.client.model.ModelRenderer}s on this model.
     * @param entityLivingBase The entity
     * @param par2
     * @param par3
     * @param par4
     */
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float par2, float par3, float par4) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entityLivingBase;
        this.aimedBow = !miniPlayer.isSitting() && (miniPlayer.getHeldItem() != null) && (miniPlayer.getHeldItem().getItem() == Items.bow);
        super.setLivingAnimations(entityLivingBase, par2, par3, par4);
    }
}
