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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.foxlib.client.TextHelper;
import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

@SideOnly(Side.CLIENT)
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

    @Override
    public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) par1Entity;
        this.doRender(miniPlayer, x, y, z, par8, par9);

        //Draw the chat messages
        String chat = miniPlayer.getChat();
        if (chat != null && !chat.isEmpty()) {
            TextHelper.drawWrappedMessageFacingPlayer(x, y + miniPlayer.height + 0.67F, z, 0.016666668F * 1.1F, 100, 20, chat, -1);
        }

        //Draw stat changes
        //This is greater then 0 if we have changes to display
        if (miniPlayer.statMessageTime < 60) {
            //Messages floats up over time
            float yOffset = miniPlayer.height + 0.67F + (miniPlayer.statMessageTime / 150F);
            //Loop through any changes
            for (String statChange : miniPlayer.statMessage.split(";")) {
                if (!statChange.isEmpty()) {
                    TextHelper.drawMultiLineMessageFacingPlayer(x, y + yOffset, z, 0.016666668F, new String[]{statChange}, (int) (-(miniPlayer.statMessageTime / 60F) * 255) << 24, true, false);
                    yOffset += 0.4;
                }
            }
        }
    }

    //Copied from RenderPlayer.renderSpecials
    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        ItemStack itemstack = par1EntityLivingBase.getHeldItem();
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) par1EntityLivingBase;
        if (itemstack != null) {
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));
            if (miniPlayer.isSitting()) GL11.glTranslatef(0F, 0.3F, 0F);
            if (miniPlayer.isRiding()) GL11.glTranslatef(0F, 0.25F, 0F);
            if ((miniPlayer.ridingEntity instanceof EntityTameable) && (((EntityTameable) miniPlayer.ridingEntity).isSitting())) GL11.glTranslatef(0F, 0.1F, 0F);
            //If item is a block or has a custom renderer
            if (itemstack.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))) {
                GL11.glTranslatef(0.0F, 0.8875F, -0.3F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-0.3F, -0.3F, -0.3F);
                this.renderManager.itemRenderer.renderItem(par1EntityLivingBase, itemstack, 0);
            }
            //Otherwise render "2D"
            else {
                GL11.glTranslatef(0F, 0.725F, -0.025F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                super.renderEquippedItems(par1EntityLivingBase, par2);
            }
        }
    }

    @Override
    protected void func_96449_a(EntityLivingBase entity, double x, double y, double z, String text, float p_96449_9_, double p_96449_10_) {
        if (((EntityMiniPlayer) entity).isSitting()) {
            this.func_147906_a(entity, text, x, y - 0.3D, z, 64);
        }
        else {
            this.func_147906_a(entity, text, x, y, z, 64);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        //Gets the players skin
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer)entity;
        if (entityMiniPlayer.hasCustomNameTag()) {
            resourcelocation = AbstractClientPlayer.getLocationSkin(entityMiniPlayer.getCustomNameTag());
            AbstractClientPlayer.getDownloadImageSkin(resourcelocation, entityMiniPlayer.getCustomNameTag());
        }
        return resourcelocation;
    }
}
