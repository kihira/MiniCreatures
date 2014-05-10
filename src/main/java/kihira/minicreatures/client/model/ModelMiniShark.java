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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMiniShark extends ModelBase {

    ModelRenderer body;
    ModelRenderer bodyTopFin;
    ModelRenderer head;
    ModelRenderer tail;
    ModelRenderer tailFinTop;
    ModelRenderer tailFinBottom;
    ModelRenderer bodyRightFin;
    ModelRenderer mouth;
    ModelRenderer bodyLeftFin;

    public ModelMiniShark() {
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-2F, -2F, 0F, 4, 4, 6);
        body.setRotationPoint(0F, 22F, -4F);
        bodyTopFin = new ModelRenderer(this, 20, 0);
        bodyTopFin.addBox(-0.5F, -5.5F, 0F, 1, 4, 2);
        bodyTopFin.setRotationPoint(0F, 22F, -4F);
        setRotation(bodyTopFin, -0.5235988F, 0F, 0F);
        head = new ModelRenderer(this, 0, 17);
        head.addBox(-1.5F, -0.3F, -4F, 3, 2, 4);
        head.setRotationPoint(0F, 20.5F, -4F);
        tail = new ModelRenderer(this, 0, 10);
        tail.addBox(-1.5F, -1.5F, 0F, 3, 3, 4);
        tail.setRotationPoint(0F, 22F, 2F);
        tailFinTop = new ModelRenderer(this, 20, 6);
        tailFinTop.addBox(-0.5F, 0.7F, 2.5F, 1, 2, 3);
        tailFinTop.setRotationPoint(0F, 22F, 2F);
        setRotation(tailFinTop, 0.7679449F, 0F, 0F);
        tailFinBottom = new ModelRenderer(this, 14, 10);
        tailFinBottom.addBox(-0.5F, -3F, 2.7F, 1, 2, 2);
        tailFinBottom.setRotationPoint(0F, 22F, 2F);
        setRotation(tailFinBottom, -0.7853982F, 0F, 0F);
        bodyRightFin = new ModelRenderer(this, 14, 14);
        bodyRightFin.addBox(-1F, 0.5F, 2F, 2, 1, 4);
        bodyRightFin.setRotationPoint(0F, 22F, -3F);
        setRotation(bodyRightFin, -0.1745329F, -0.6981317F, 0F);
        mouth = new ModelRenderer(this, 0, 22);
        mouth.addBox(-1.5F, 0F, -3F, 3, 1, 3);
        mouth.setRotationPoint(0F, 22.5F, -4F);
        setRotation(mouth, 0.2617994F, 0F, 0F);
        bodyLeftFin = new ModelRenderer(this, 14, 19);
        bodyLeftFin.addBox(-1F, 0.5F, 2F, 2, 1, 4);
        bodyLeftFin.setRotationPoint(0F, 22F, -3F);
        setRotation(bodyLeftFin, -0.1745329F, 0.6981317F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        body.render(f5);
        bodyTopFin.render(f5);
        head.render(f5);
        tail.render(f5);
        tailFinTop.render(f5);
        tailFinBottom.render(f5);
        bodyRightFin.render(f5);
        mouth.render(f5);
        bodyLeftFin.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
