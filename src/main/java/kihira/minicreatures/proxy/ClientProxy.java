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

package kihira.minicreatures.proxy;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.client.render.RenderMiniCreature;
import kihira.minicreatures.client.render.RenderMiniPlayer;
import kihira.minicreatures.client.render.RenderMiniShark;
import kihira.minicreatures.client.render.RenderTRex;
import kihira.minicreatures.common.entity.*;
import kihira.minicreatures.common.network.ItemUseMessage;
import kihira.minicreatures.common.network.PersonalityMessage;
import kihira.minicreatures.common.network.ProspectBlocksMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is loaded as the {@link kihira.minicreatures.MiniCreatures#proxy} only on the client
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final ResourceLocation specialTextures = new ResourceLocation("minicreatures", "textures/model/specials.png");

    private static final ResourceLocation FOX_TEXTURE = new ResourceLocation("minicreatures", "textures/entity/fox.png");
    private static final ResourceLocation FOX_COLLAR_TEXTURE = new ResourceLocation("minicreatures", "textures/entity/foxcollar.png");
    private static final ResourceLocation RED_PANDA_TEXTURE = new ResourceLocation("minicreatures", "textures/entity/redpanda.png");
    private static final ResourceLocation RED_PANDA_COLLAR_TEXTURE = new ResourceLocation("minicreatures", "textures/entity/redpandacollar.png");

    /**
     * Registers the renderers for the various entities
     */
    @Override
    public void registerRenderers() {
        if (MiniCreatures.enableMiniFoxes) {
            RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, manager -> {
                return new RenderMiniCreature<>(manager, 0.4f, FOX_TEXTURE, FOX_COLLAR_TEXTURE);
            });
        }
        if (MiniCreatures.enableMiniTRex) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMiniTRex.class, RenderTRex::new);
        }
        if (MiniCreatures.enableMiniPlayers) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMiniPlayer.class, RenderMiniPlayer::new);
        }
        if (MiniCreatures.enableMiniShark) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMiniShark.class, RenderMiniShark::new);
        }
        if (MiniCreatures.enableMiniRedPandas) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRedPanda.class, manager -> {
                return new RenderMiniCreature<>(manager, 0.4f, RED_PANDA_TEXTURE, RED_PANDA_COLLAR_TEXTURE);
            });
        }
    }

    @Override
    public void registerMessages() {
        super.registerMessages();
        this.simpleNetworkWrapper.registerMessage(PersonalityMessage.PersonalityMessageHandler.class, PersonalityMessage.class, 0, Side.CLIENT);
        this.simpleNetworkWrapper.registerMessage(ItemUseMessage.ItemUseMessageHandler.class, ItemUseMessage.class, 4, Side.CLIENT);
        this.simpleNetworkWrapper.registerMessage(ProspectBlocksMessage.ProspectBlocksMessageHandler.class, ProspectBlocksMessage.class, 5, Side.CLIENT);
    }
}
