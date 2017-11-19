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

import com.google.common.base.Strings;
import kihira.minicreatures.client.TextHelper;
import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.client.render.layers.LayerBackpack;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniPlayer extends RenderBiped<EntityMiniPlayer> {

    public RenderMiniPlayer(RenderManager manager) {
        super(manager, new ModelMiniPlayer(), 0.3F);
        addLayer(new LayerBackpack());
        addLayer(new LayerHeldItem(this));
        addLayer(new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelMiniPlayer(0.5F, true);
                this.modelArmor = new ModelMiniPlayer(1.0F, true);
            }
        });
    }

    @Override
    public ModelMiniPlayer getMainModel() {
        return (ModelMiniPlayer) super.getMainModel();
    }

    @Override
    public void doRender(EntityMiniPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        setPoses(entity);
        getMainModel().isSitting = entity.isSitting();

        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        //Draw the chat messages
        String chat = entity.getChat();
        if (!Strings.isNullOrEmpty(chat)) {
            TextHelper.drawWrappedMessageFacingPlayer(x, y + entity.height + 0.67F, z, 100, 20, chat, 0.016666668F * 1.1F);
        }

        //Draw stat changes
        //This is greater then 0 if we have changes to display
        if (entity.statMessageTime < 60) {
            //Messages floats up over time
            float yOffset = entity.height + 0.67F + (entity.statMessageTime / 150F);
            //Loop through any changes
            for (String statChange : entity.statMessage.split(";")) {
                if (!statChange.isEmpty()) {
                    TextHelper.drawMultiLineMessageFacingPlayer(x, y + yOffset, z, new String[]{statChange}, 0.016666668F, (int) (-(entity.statMessageTime / 60F) * 255) << 24, true, false);
                    yOffset += 0.4;
                }
            }
        }
    }

    @Override
    protected void renderLivingAt(EntityMiniPlayer entityLivingBaseIn, double x, double y, double z) {
        super.renderLivingAt(entityLivingBaseIn, x, y, z);
        if (entityLivingBaseIn.isSitting()) GlStateManager.translate(0F, -0.27F, 0F);
    }

    private void setPoses(EntityMiniPlayer miniPlayer) {
        ItemStack itemMainhand = miniPlayer.getHeldItemMainhand();
        ItemStack itemOffhand = miniPlayer.getHeldItemOffhand();
        ModelBiped.ArmPose mainhandPose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose offhandPose = ModelBiped.ArmPose.EMPTY;

        if (!itemMainhand.isEmpty()) {
            mainhandPose = ModelBiped.ArmPose.ITEM;

            if (miniPlayer.getItemInUseCount() > 0) {
                EnumAction enumaction = itemMainhand.getItemUseAction();
                switch (enumaction){
                    case BLOCK:
                        mainhandPose = ModelBiped.ArmPose.BLOCK;
                        break;
                    case BOW:
                        mainhandPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                        break;
                }
            }
        }
        if (!itemOffhand.isEmpty()) {
            offhandPose = ModelBiped.ArmPose.ITEM;

            if (miniPlayer.getItemInUseCount() > 0) {
                EnumAction enumaction = itemOffhand.getItemUseAction();
                switch (enumaction){
                    case BLOCK:
                        offhandPose = ModelBiped.ArmPose.BLOCK;
                        break;
                }
            }
        }

        if (miniPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
            getMainModel().rightArmPose = mainhandPose;
            getMainModel().leftArmPose = offhandPose;
        }
        else {
            getMainModel().rightArmPose = offhandPose;
            getMainModel().leftArmPose = mainhandPose;
        }
    }


    @Override
    protected void renderEntityName(EntityMiniPlayer entity, double x, double y, double z, String name, double p_188296_9_) {
        if (entity.isSitting()) {
            this.renderLivingLabel(entity, name, x, y - 0.3D, z, 64);
        }
        else {
            this.renderLivingLabel(entity, name, x, y, z, 64);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMiniPlayer entity) {
        //Gets the players skin
        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkin(entity.getUniqueID()); // todo only support legacy skin?
        if (entity.hasCustomName()) {
            resourcelocation = AbstractClientPlayer.getLocationSkin(entity.getCustomNameTag());
            AbstractClientPlayer.getDownloadImageSkin(resourcelocation, entity.getCustomNameTag());
        }
        return resourcelocation;
    }
}
