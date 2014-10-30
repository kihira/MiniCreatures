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

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.client.render.*;
import kihira.minicreatures.common.entity.*;
import kihira.minicreatures.common.network.ItemUseMessage;
import kihira.minicreatures.common.network.PersonalityMessage;
import kihira.minicreatures.common.network.ProspectBlocksMessage;
import net.minecraft.util.ResourceLocation;

/**
 * This class is loaded as the {@link kihira.minicreatures.MiniCreatures#proxy} only on the client
 */
public class ClientProxy extends CommonProxy {

    public static final ResourceLocation specialTextures = new ResourceLocation("minicreatures", "textures/model/specials.png");

    /**
     * Registers the renderers for the various entities
     */
    @Override
    public void registerRenderers() {
        if (MiniCreatures.enableMiniFoxes) RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, new RenderFox());
        if (MiniCreatures.enableMiniTRex) RenderingRegistry.registerEntityRenderingHandler(EntityTRex.class, new RenderTRex());
        if (MiniCreatures.enableMiniPlayers) RenderingRegistry.registerEntityRenderingHandler(EntityMiniPlayer.class, new RenderMiniPlayer());
        if (MiniCreatures.enableMiniShark) RenderingRegistry.registerEntityRenderingHandler(EntityMiniShark.class, new RenderMiniShark());
        if (MiniCreatures.enableMiniRedPandas) RenderingRegistry.registerEntityRenderingHandler(EntityRedPanda.class, new RenderRedPanda());
    }

    @Override
    public void registerMessages() {
        super.registerMessages();
        this.simpleNetworkWrapper.registerMessage(PersonalityMessage.PersonalityMessageHandler.class, PersonalityMessage.class, 0, Side.CLIENT);
        this.simpleNetworkWrapper.registerMessage(ItemUseMessage.ItemUseMessageHandler.class, ItemUseMessage.class, 4, Side.CLIENT);
        this.simpleNetworkWrapper.registerMessage(ProspectBlocksMessage.ProspectBlocksMessageHandler.class, ProspectBlocksMessage.class, 5, Side.CLIENT);
    }
}
