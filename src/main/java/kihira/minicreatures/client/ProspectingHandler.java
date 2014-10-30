/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import kihira.foxlib.client.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class ProspectingHandler {

    public static final ProspectingHandler INSTANCE = new ProspectingHandler();
    public int[][] blocks;
    public int showTime;

    private ProspectingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
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
            GL11.glPushMatrix();
            Entity entity = Minecraft.getMinecraft().thePlayer;
            double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
            double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
            double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;
            GL11.glTranslated(-interpPosX, -interpPosY, -interpPosZ);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int[] orePos : blocks) {
                //GL11.glTranslated(orePos[0], orePos[1], orePos[2]);

                TextHelper.drawWrappedMessageFacingPlayer(orePos[0] + 0.5, orePos[1] + 0.5, orePos[2] + 0.5, 0.05F, 30, 0, "Ore!", -1);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }
    }
}
