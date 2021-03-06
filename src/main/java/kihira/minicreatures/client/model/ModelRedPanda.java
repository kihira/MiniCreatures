/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.minicreatures.client.model;

import kihira.minicreatures.common.entity.EntityRedPanda;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The model for {@link kihira.minicreatures.common.entity.EntityRedPanda}
 */
@SideOnly(Side.CLIENT)
public class ModelRedPanda extends ModelBase {

    ModelRenderer body;
    ModelRenderer frontLeftLeg;
    ModelRenderer frontRightLeg;
    ModelRenderer backRightLeg;
    ModelRenderer backLeftLeg;
    ModelRenderer tail;
    ModelRenderer head;
    ModelRenderer nose;
    ModelRenderer leftEar;
    ModelRenderer leftEarTip;
    ModelRenderer rightEar;
    ModelRenderer rightEarTip;
    ModelRenderer chest;

    public ModelRedPanda() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-2F, -2F, -3F, 4, 4, 6);
        this.body.setRotationPoint(0F, 20F, 0F);

        this.chest = new ModelRenderer(this, 20, 18);
        this.chest.addBox(-3F, -1.5F, 0.5F, 6, 6, 8);
        this.chest.setRotationPoint(0F, 19F, -2F);

        this.frontLeftLeg = new ModelRenderer(this, 0, 10);
        this.frontLeftLeg.addBox(-1F, 0F, -1F, 2, 2, 2);
        this.frontLeftLeg.setRotationPoint(1.6F, 22F, -2.4F);

        this.frontRightLeg = new ModelRenderer(this, 0, 10);
        this.frontRightLeg.addBox(-1F, 0F, -1F, 2, 2, 2);
        this.frontRightLeg.setRotationPoint(-1.6F, 22F, -2.4F);

        this.backRightLeg = new ModelRenderer(this, 0, 10);
        this.backRightLeg.addBox(-1F, 0F, -1F, 2, 2, 2);
        this.backRightLeg.setRotationPoint(-1.6F, 22F, 2.4F);

        this.backLeftLeg = new ModelRenderer(this, 0, 10);
        this.backLeftLeg.addBox(-1F, 0F, -1F, 2, 2, 2);
        this.backLeftLeg.setRotationPoint(1.6F, 22F, 2.4F);

        this.tail = new ModelRenderer(this, 0, 14);
        this.tail.addBox(-1.5F, -1.5F, -0.5F, 3, 3, 6);
        this.tail.setRotationPoint(0F, 20F, 3F);
        this.setRotation(tail, -0.2617994F, 0F, 0F);

        this.head = new ModelRenderer(this, 0, 23);
        this.head.addBox(-3F, -3.5F, -4F, 6, 5, 4);
        this.head.setRotationPoint(0F, 20F, -3F);

        this.nose = new ModelRenderer(this, 8, 10);
        this.nose.addBox(-1.5F, -0.5F, -5F, 3, 2, 1);
        this.nose.setRotationPoint(0F, 20F, -3F);

        this.leftEar = new ModelRenderer(this, 20, 0);
        this.leftEar.addBox(0.5F, -4.5F, -2F, 2, 1, 1);
        this.leftEar.setRotationPoint(0F, 20F, -3F);

        this.leftEarTip = new ModelRenderer(this, 26, 0);
        this.leftEarTip.addBox(1.5F, -5.5F, -2F, 1, 1, 1);
        this.leftEarTip.setRotationPoint(0F, 20F, -3F);

        this.rightEar = new ModelRenderer(this, 20, 0);
        this.rightEar.addBox(-2.5F, -4.5F, -2F, 2, 1, 1);
        this.rightEar.setRotationPoint(0F, 20F, -3F);

        this.rightEarTip = new ModelRenderer(this, 26, 0);
        this.rightEarTip.addBox(-2.5F, -5.5F, -2F, 1, 1, 1);
        this.rightEarTip.setRotationPoint(0F, 20F, -3F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.body.render(scale);
        this.frontLeftLeg.render(scale);
        this.frontRightLeg.render(scale);
        this.backRightLeg.render(scale);
        this.backLeftLeg.render(scale);
        this.tail.render(scale);
        this.head.render(scale);
        this.nose.render(scale);
        this.leftEar.render(scale);
        this.leftEarTip.render(scale);
        this.rightEar.render(scale);
        this.rightEarTip.render(scale);
        this.backRightLeg.render(scale);

        if (((EntityRedPanda) entity).hasChest()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1F, 0.5F, 0.5F);
            GlStateManager.translate(0.0f, 18F * scale,  scale - 0.3f);
            this.chest.render(scale);
            GlStateManager.popMatrix();
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    /**
     * This is called in {@link net.minecraft.client.renderer.entity.RenderLiving} to set the various angles for the
     * various {@link net.minecraft.client.model.ModelRenderer}s on this model.
     * @param entityLivingBase The entity
     */
    @Override
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float limbSwing, float limbSwingAmount, float partialTickTime) {
        EntityRedPanda entityRedPanda = (EntityRedPanda) entityLivingBase;

        if (entityRedPanda.isSitting()) {
            this.body.setRotationPoint(0F, 21F, -0.3F);
            this.body.rotateAngleX = -(float)Math.PI / 10F;
            this.chest.setRotationPoint(0F, 21F, 0F);
            this.chest.rotateAngleX = -(float)Math.PI / 10F;
            this.frontLeftLeg.setRotationPoint(1.6F, 22F, -3.4F);
            this.frontRightLeg.setRotationPoint(-1.6F, 22F, -3.4F);
            this.backRightLeg.setRotationPoint(-1.6F, 23F, 2.4F);
            this.backLeftLeg.setRotationPoint(1.6F, 23F, 2.4F);
            this.tail.setRotationPoint(0F, 21.5F, 3F);
            this.tail.rotateAngleX = -0.1F;
            this.backLeftLeg.rotateAngleX = (float) Math.toRadians(-80F);
            this.backRightLeg.rotateAngleX = (float) Math.toRadians(-80F);
        }
        else {
            this.body.setRotationPoint(0F, 20F, 0F);
            this.body.rotateAngleX = 0;
            this.chest.setRotationPoint(0F, 20F, 0F);
            this.chest.rotateAngleX = 0;
            this.frontLeftLeg.setRotationPoint(1.6F, 22F, -2.4F);
            this.frontRightLeg.setRotationPoint(-1.6F, 22F, -2.4F);
            this.backRightLeg.setRotationPoint(-1.6F, 22F, 2.4F);
            this.backLeftLeg.setRotationPoint(1.6F, 22F, 2.4F);
            this.tail.setRotationPoint(0F, 20F, 3F);
            this.setRotation(this.tail, -0.2617994F, 0F, 0F);
            this.frontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 1.4F * limbSwingAmount;
            this.frontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.backLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.backRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 1.4F * limbSwingAmount;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.head.rotateAngleX = this.nose.rotateAngleX = this.leftEar.rotateAngleX = this.leftEarTip.rotateAngleX = this.rightEar.rotateAngleX = this.rightEarTip.rotateAngleX = headPitch / (180F / (float)Math.PI);
        this.head.rotateAngleY = this.nose.rotateAngleY = this.leftEar.rotateAngleY = this.leftEarTip.rotateAngleY = this.rightEar.rotateAngleY = this.rightEarTip.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
    }
}
