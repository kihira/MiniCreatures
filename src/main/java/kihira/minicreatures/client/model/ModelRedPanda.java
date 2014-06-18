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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class ModelRedPanda extends ModelBase {

    //fields
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

    public ModelRedPanda() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-2F, -2F, -3F, 4, 4, 6);
        this.body.setRotationPoint(0F, 20F, 0F);

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
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.body.render(f5);
        this.frontLeftLeg.render(f5);
        this.frontRightLeg.render(f5);
        this.backRightLeg.render(f5);
        this.backLeftLeg.render(f5);
        this.tail.render(f5);
        this.head.render(f5);
        this.nose.render(f5);
        this.leftEar.render(f5);
        this.leftEarTip.render(f5);
        this.rightEar.render(f5);
        this.rightEarTip.render(f5);
        this.backRightLeg.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float par2, float par3, float par4) {
        EntityRedPanda entityFox = (EntityRedPanda) entityLivingBase;

        if (entityFox.isSitting()) {
/*            this.body.setRotationPoint(0F, 19F, -1F);
            this.body.rotateAngleX = -(float)Math.PI / 5F;
            //this.chest.setRotationPoint(0F, 19F, -1F);
            //this.chest.rotateAngleX = -(float)Math.PI / 5F;
            this.frontLeftLeg.setRotationPoint(1.5F, 20F, -2F);
            this.frontRightLeg.setRotationPoint(-1.5F, 20F, -2F);
            this.backLeftLeg.setRotationPoint(1.6F, 22F, 2.4F);
            this.backRightLeg.setRotationPoint(-1.5F, 21F, 1.5F);
            this.frontLeftLeg.rotateAngleX = -1F;
            this.frontRightLeg.rotateAngleX = -1F;
            this.backLeftLeg.rotateAngleX = -1.5F;
            this.backRightLeg.rotateAngleX = -1.5F;
            //this.tail.setRotationPoint(0F, 19F, 1.5F);*/
        }
        else {
            this.body.setRotationPoint(0F, 20F, 0F);
            this.body.rotateAngleX = 0;
            //this.chest.setRotationPoint(0F, 19F, -2F);
            //this.chest.rotateAngleX = 0;
            this.frontLeftLeg.setRotationPoint(1.6F, 22F, -2.4F);
            this.frontRightLeg.setRotationPoint(-1.6F, 22F, -2.4F);
            this.backRightLeg.setRotationPoint(-1.6F, 22F, 2.4F);
            this.backLeftLeg.setRotationPoint(1.6F, 22F, 2.4F);
            this.tail.setRotationPoint(0F, 20F, 3F);
            this.setRotation(this.tail, -0.2617994F, 0F, 0F);
            this.frontLeftLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
            this.frontRightLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
            this.backLeftLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
            this.backRightLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
        }
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
        this.head.rotateAngleX = this.nose.rotateAngleX = this.leftEar.rotateAngleX = this.leftEarTip.rotateAngleX = this.rightEar.rotateAngleX = this.rightEarTip.rotateAngleX = f4 / (180F / (float)Math.PI);
        this.head.rotateAngleY = this.nose.rotateAngleY = this.leftEar.rotateAngleY = this.leftEarTip.rotateAngleY = this.rightEar.rotateAngleY = this.rightEarTip.rotateAngleY = f3 / (180F / (float)Math.PI);
    }
}
