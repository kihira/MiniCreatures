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
import kihira.foxlib.client.TextHelper;
import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniPlayer extends RenderBiped<EntityMiniPlayer> {

    public RenderMiniPlayer(RenderManager manager) {
        super(manager, new ModelMiniPlayer(), 0.3F, 1F);
        this.addLayer(new LayerHeldItem(this));
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelMiniPlayer(0.5F, true);
                this.modelArmor = new ModelMiniPlayer(1.0F, true);
            }
        };
        this.addLayer(layerbipedarmor);
    }

    @Override
    public void doRender(EntityMiniPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        //Draw the chat messages
        String chat = entity.getChat();
        if (!Strings.isNullOrEmpty(chat)) {
            TextHelper.drawWrappedMessageFacingPlayer(x, y + entity.height + 0.67F, z, 0.016666668F * 1.1F, 100, 20, chat, -1);
        }

        //Draw stat changes
        //This is greater then 0 if we have changes to display
        if (entity.statMessageTime < 60) {
            //Messages floats up over time
            float yOffset = entity.height + 0.67F + (entity.statMessageTime / 150F);
            //Loop through any changes
            for (String statChange : entity.statMessage.split(";")) {
                if (!statChange.isEmpty()) {
                    TextHelper.drawMultiLineMessageFacingPlayer(x, y + yOffset, z, 0.016666668F, new String[]{statChange}, (int) (-(entity.statMessageTime / 60F) * 255) << 24, true, false);
                    yOffset += 0.4;
                }
            }
        }
    }

    @Override
    protected void renderEntityName(EntityMiniPlayer entity, double x, double y, double z, String name, double p_188296_9_) {
        super.renderEntityName(entity, x, y, z, name, p_188296_9_);
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
