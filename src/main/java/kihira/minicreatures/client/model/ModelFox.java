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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.minicreatures.common.entity.EntityFox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * The model for {@link kihira.minicreatures.common.entity.EntityFox}
 */
@SideOnly(Side.CLIENT)
public class ModelFox extends ModelBase {

    ModelRenderer LBLeg;
    ModelRenderer head;
    ModelRenderer tailMid;
    ModelRenderer RBLeg;
    ModelRenderer RFLeg;
    ModelRenderer LFLeg;
    ModelRenderer Body;
    ModelRenderer tailTip;
    ModelRenderer tailBase;
    ModelRenderer chest;

    public ModelFox() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-3F, -3F, -4F, 6, 6, 4);
        head.setRotationPoint(0F, 17F, -2F);
        head.setTextureOffset(20, 0).addBox(-1.5F, 0F, -7F, 3, 3, 3); //Muzzle
        head.setTextureOffset(30, 6).addBox(0.5F, -5F, -3F, 2, 2, 1); //Left Ear
        head.setTextureOffset(36, 6).addBox(1.5F, -6F, -3F, 1, 1, 1); //Left Ear Tip
        head.setTextureOffset(20, 6).addBox(-2.5F, -5F, -3F, 2, 2, 1); //Right Ear
        head.setTextureOffset(26, 6).addBox(-2.5F, -6F, -3F, 1, 1, 1); //Right Ear Tip
        LBLeg = new ModelRenderer(this, 42, 5);
        LBLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        LBLeg.setRotationPoint(1.5F, 21F, 2.5F);
        RBLeg = new ModelRenderer(this, 42, 5);
        RBLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        RBLeg.setRotationPoint(-1.5F, 21F, 2.5F);
        RFLeg = new ModelRenderer(this, 42, 5);
        RFLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        RFLeg.setRotationPoint(-1.5F, 21F, -1.5F);
        LFLeg = new ModelRenderer(this, 42, 5);
        LFLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        LFLeg.setRotationPoint(1.5F, 21F, -1.5F);
        Body = new ModelRenderer(this, 32, 10);
        Body.addBox(-2F, -2F, -2F, 4, 4, 5);
        Body.setRotationPoint(0F, 19F, 0F);
        chest = new ModelRenderer(this, 0, 17);
        chest.addBox(-3F, -1.5F, 0.5F, 6, 6, 8);
        chest.setRotationPoint(0F, 19F, -2F);

        tailBase = new ModelRenderer(this, 14, 10);
        tailBase.addBox(-1F, -1F, 0F, 2, 2, 3);
        tailBase.setRotationPoint(0F, 18F, 2F);
        setRotation(tailBase, 0.3316126F, 0F, 0F);
        tailMid = new ModelRenderer(this, 0, 10);
        tailMid.addBox(-1.5F, -1F, 2F, 3, 3, 4);
        tailMid.setRotationPoint(0F, 18F, 2F);
        setRotation(tailMid, 0.5934119F, 0F, 0F);
        tailTip = new ModelRenderer(this, 24, 10);
        tailTip.addBox(-1F, 2F, 4.5F, 2, 2, 2);
        tailTip.setRotationPoint(0F, 18F, 2F);
        setRotation(tailTip, 1.029744F, 0F, 0F);
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
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        EntityFox entityFox = (EntityFox)entity;
        GL11.glPushMatrix();
        if (entityFox.isSitting()) {
            GL11.glTranslatef(0.0f, f5 + 0.06f, 0f);
        }
        LBLeg.render(f5);
        head.renderWithRotation(f5);
        tailMid.render(f5);
        RBLeg.render(f5);
        RFLeg.render(f5);
        LFLeg.render(f5);
        Body.render(f5);
        tailTip.render(f5);
        tailBase.render(f5);
        if (entityFox.hasChest()) {
            GL11.glPushMatrix();
            GL11.glScalef(1f, 0.5f, 0.5f);
            if (entityFox.isSitting()) {
                GL11.glTranslatef(0.0f, 16F * f5,  f5 - 0.3f);
            }
            else {
                GL11.glTranslatef(0.0f, 18F * f5,  f5 - 0.15f);
            }
            chest.render(f5);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    /**
     * Sets the tails rotation point
     * @param x The x position
     * @param y The y position
     * @param z The z position
     */
    private void setTailRotationPoints(float x, float y, float z) {
        this.tailBase.setRotationPoint(x, y, z);
        this.tailMid.setRotationPoint(x, y, z);
        this.tailTip.setRotationPoint(x, y, z);
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
        EntityFox entityFox = (EntityFox)entityLivingBase;

        if (entityFox.isSitting()) {
            this.Body.setRotationPoint(0F, 19F, -1F);
            this.Body.rotateAngleX = -(float)Math.PI / 5F;
            this.chest.setRotationPoint(0F, 19F, -1F);
            this.chest.rotateAngleX = -(float)Math.PI / 5F;
            this.LFLeg.setRotationPoint(1.5F, 20F, -2F);
            this.RFLeg.setRotationPoint(-1.5F, 20F, -2F);
            this.LBLeg.setRotationPoint(1.5F, 21F, 1.5F);
            this.RBLeg.setRotationPoint(-1.5F, 21F, 1.5F);
            this.LFLeg.rotateAngleX = -1F;
            this.RFLeg.rotateAngleX = -1F;
            this.LBLeg.rotateAngleX = -1.5F;
            this.RBLeg.rotateAngleX = -1.5F;
            this.setTailRotationPoints(0F, 19F, 1.5F);
        }
        else {
            this.Body.setRotationPoint(0F, 19F, 0F);
            this.Body.rotateAngleX = 0;
            this.chest.setRotationPoint(0F, 19F, -2F);
            this.chest.rotateAngleX = 0;
            this.LFLeg.setRotationPoint(1.5F, 21F, -1.5F);
            this.RFLeg.setRotationPoint(-1.5F, 21F, -1.5F);
            this.LBLeg.setRotationPoint(1.5F, 21F, 2.5F);
            this.RBLeg.setRotationPoint(-1.5F, 21F, 2.5F);
            this.LFLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
            this.RFLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
            this.LBLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
            this.RBLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
            this.setTailRotationPoints(0F, 18F, 2F);
        }
    }

    /**
     * Sets the rotation for the {@link net.minecraft.client.model.ModelRenderer} provided
     * @param model The {@link net.minecraft.client.model.ModelRenderer}
     * @param x The x angle in radians
     * @param y The y angle in radians
     * @param z The z angle in radians
     */
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
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
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        this.head.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float)Math.PI);
    }
}