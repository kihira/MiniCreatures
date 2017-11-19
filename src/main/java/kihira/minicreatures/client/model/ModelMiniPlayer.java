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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The model for {@link kihira.minicreatures.common.entity.EntityMiniPlayer}
 */
@SideOnly(Side.CLIENT)
public class ModelMiniPlayer extends ModelBiped {

    public boolean isSitting;

    public ModelMiniPlayer() {
        this(0f, false);
    }

    public ModelMiniPlayer(float modelSize, boolean smallTexture) {
        super(modelSize, 0f, 64, smallTexture ? 32 : 64);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;
        isChild = true;
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.pushMatrix();
        for (String partName : miniPlayer.getCurrentParts(Minecraft.getMinecraft().currentScreen instanceof GuiCustomizer)) {
            ICustomizerPart part = CustomizerRegistry.getPart(partName);
            if (part != null) part.render(entity, this, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

        // todo consider how we want mini players to hold blocks. endermen style if interactable? Need to override LayerHeldItem if thats the case
/*        if (this.rightArmPose == ArmPose.ITEM && miniPlayer.getHeldItemMainhand() != null && miniPlayer.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            this.bipedLeftArm.rotateAngleX = -0.8F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
            this.bipedRightArm.rotateAngleX = -0.8F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
        }*/
        // need to re-call this cause ~mojang~
        // standard model rendering calls setLivingAnimations then setRotationAngles causing values to be overwritten
        // However armour rendering only calls setLivingAnimations
        setAnimations((EntityMiniPlayer) entity);
    }

    // armour rendering only calls setLivingAnimations
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        setAnimations((EntityMiniPlayer) entity);
    }

    private void setAnimations(EntityMiniPlayer miniPlayer) {
        if (this.isSitting) {
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
        }

        if (miniPlayer.getItemInUseCount() > 0) {
            EnumAction action = miniPlayer.getActiveItemStack().getItemUseAction();
            if (action == EnumAction.DRINK || action == EnumAction.EAT) {
                float itemUseCount = (float) miniPlayer.getItemInUseCount() + 1F;
                float timeLeft = 1F - itemUseCount / (float) miniPlayer.getActiveItemStack().getMaxItemUseDuration();
                float angle = (float) (1F - Math.pow(1F - timeLeft, 9));
                ModelRenderer arm = getArmForSide(miniPlayer.getPrimaryHand());
                arm.rotateAngleX += (-angle * (miniPlayer.isSitting() ? 0.6F : 1.2F)) + MathHelper.abs(MathHelper.cos(itemUseCount / 4F * (float) Math.PI) * 0.1F) * (timeLeft > 0.2F ? 1F : 0F);
                arm.rotateAngleY = -angle * 0.3F;
                arm.rotateAngleZ = angle * 0.4F;
            }
        }
    }

    @Override
    public void postRenderArm(float scale, EnumHandSide side) {
        // todo support small arms and left arm?
        if (this.isSitting) GlStateManager.translate(0F, 0.35F, -0.05F);
        else GlStateManager.translate(0F, 0.325F, 0F);
        GlStateManager.rotate(30F, -1.0F, 0F, 0F);
        super.postRenderArm(scale, side);
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);

        this.isSitting = ((ModelMiniPlayer)model).isSitting;
    }
}
