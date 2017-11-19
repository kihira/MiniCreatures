/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProspectingHandler {

    public static final ProspectingHandler INSTANCE = new ProspectingHandler();
    public int[][] blocks;
    public int showTime;

    private ProspectingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (showTime > 0) {
                showTime--;
                if (showTime == 0) {
                    blocks = null;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (blocks != null) {
            GlStateManager.pushMatrix();
            Entity entity = Minecraft.getMinecraft().player;
            double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
            double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
            double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
            GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
            GlStateManager.disableDepth();
            for (int[] orePos : blocks) {
                TextHelper.drawWrappedMessageFacingPlayer(orePos[0] + 0.5, orePos[1] + 0.5, orePos[2] + 0.5, 30, 0, "Ore!", 0.05F);
            }
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }
}
