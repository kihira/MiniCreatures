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

import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

public class RenderMiniPlayer extends RenderBiped {

    public RenderMiniPlayer() {
        super(new ModelMiniPlayer(), 0.3F);
    }

    @Override
    protected void func_82421_b() {
        //We need to set these values so armour renders properly. Kinda got an idea why
        this.field_82423_g = new ModelMiniPlayer(1.0F);
        this.field_82425_h = new ModelMiniPlayer(0.5F);
    }

    //Copied from RenderPlayer.renderSpecials
    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        ItemStack itemstack = par1EntityLivingBase.getHeldItem();
        if (itemstack != null) {
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));
            if (itemstack.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))) {
                GL11.glTranslatef(0.0F, 0.8875F, -0.3F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-0.3F, -0.3F, -0.3F);
                this.renderManager.itemRenderer.renderItem(par1EntityLivingBase, itemstack, 0);
            }
            else {
                GL11.glTranslatef(0F, 0.725F, -0.025F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                super.renderEquippedItems(par1EntityLivingBase, par2);
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer)entity;
        if (entityMiniPlayer.hasCustomNameTag()) {
            resourcelocation = AbstractClientPlayer.getLocationSkull(entityMiniPlayer.getCustomNameTag());
            AbstractClientPlayer.getDownloadImageSkin(resourcelocation, entityMiniPlayer.getCustomNameTag());
        }
        return resourcelocation;
    }

    //This is the method called to render name tags/scoreboard data
    //par9 == render pass?
    @Override
    protected void func_96449_a(EntityLivingBase entity, double x, double y, double z, String text, float par9, double renderDistance) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;

        //Our message
        //this.func_147906_a(miniPlayer, "I'm hungry...", x, y, z, 64);
        //Move the next message above this one
        //y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * par9);
        super.func_96449_a(entity, x, y, z, text, par9, renderDistance);
    }
}
