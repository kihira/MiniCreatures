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

import kihira.minicreatures.common.entity.EntityFox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The model for {@link kihira.minicreatures.common.entity.EntityFox}
 */
@SideOnly(Side.CLIENT)
public class ModelFox extends ModelBase {

    private ModelRenderer LBLeg;
    private ModelRenderer head;
    private ModelRenderer tailMid;
    private ModelRenderer RBLeg;
    private ModelRenderer RFLeg;
    private ModelRenderer LFLeg;
    private ModelRenderer Body;
    private ModelRenderer tailTip;
    private ModelRenderer tailBase;
    private ModelRenderer chest;

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
        tailMid.addBox(-1.5F, -1F, 0F, 3, 3, 4);
        tailMid.setRotationPoint(0F, -0.25F, 2F);
        setRotation(tailMid, 0.5934119F, 0F, 0F);

        tailTip = new ModelRenderer(this, 24, 10);
        tailTip.addBox(-1F, 2F, 0F, 2, 2, 2);
        tailTip.setRotationPoint(0F, -0.25F, 0.9F);
        setRotation(tailTip, 1.029744F, 0F, 0F);

        tailMid.addChild(tailTip);
        tailBase.addChild(tailMid);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        EntityFox entityFox = (EntityFox)entityIn;
        GlStateManager.pushMatrix();
        if (entityFox.isSitting()) {
            GlStateManager.translate(0f, scale + 0.06f, 0f);
        }

        LBLeg.render(scale);
        head.renderWithRotation(scale);
        RBLeg.render(scale);
        RFLeg.render(scale);
        LFLeg.render(scale);
        Body.render(scale);
        tailBase.render(scale);
        if (entityFox.hasChest()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1f, 0.5f, 0.5f);
            if (entityFox.isSitting()) {
                GlStateManager.translate(0.0f, 16F * scale,  scale - 0.3f);
            }
            else {
                GlStateManager.translate(0.0f, 18F * scale,  scale - 0.15f);
            }
            chest.render(scale);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        EntityFox entityFox = (EntityFox)entitylivingbaseIn;

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
            this.tailBase.setRotationPoint(0F, 19F, 1.5F);
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
            this.LFLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 1.4F * limbSwingAmount;
            this.RFLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.LBLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.RBLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.5F) * 1.4F * limbSwingAmount;
            this.tailBase.setRotationPoint(0F, 18F, 2F);
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

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        EntityFox entityLiving = (EntityFox) entityIn;
        EntityPlayer owner = (EntityPlayer) entityLiving.getOwner();
        float health = entityLiving.getHealth() / entityLiving.getMaxHealth(); //Grab this here to reduce data watcher calls

        this.head.rotateAngleX = headPitch / (180F / (float)Math.PI);
        this.head.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.tailBase.rotateAngleX = health / 3F;
        this.tailMid.rotateAngleX = health / 1.5F;

        //Tail
        //We can't use a number / distanceToOwner cause sometimes owner returns null even though the owner is there
        double speedModifier = health * (owner != null && entityLiving.getDistance(owner) < 28D ? 1.5D : 0.5D);
        this.tailBase.rotateAngleY = (float) Math.cos((ageInTicks / 2F) * speedModifier) / (2.5F / health);
    }
}