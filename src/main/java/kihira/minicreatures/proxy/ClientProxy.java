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
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.client.render.RenderFox;
import kihira.minicreatures.client.render.RenderMiniPlayer;
import kihira.minicreatures.client.render.RenderMiniShark;
import kihira.minicreatures.client.render.RenderTRex;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniShark;
import kihira.minicreatures.common.entity.EntityTRex;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy {

    public static final ResourceLocation specialTextures = new ResourceLocation("minicreatures", "textures/model/specials.png");

    @Override
    public void registerRenderers() {
        if (MiniCreatures.enableMiniFoxes) RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, new RenderFox());
        if (MiniCreatures.enableMiniTRex) RenderingRegistry.registerEntityRenderingHandler(EntityTRex.class, new RenderTRex());
        if (MiniCreatures.enableMiniPlayers) RenderingRegistry.registerEntityRenderingHandler(EntityMiniPlayer.class, new RenderMiniPlayer());
        if (MiniCreatures.enableMiniShark) RenderingRegistry.registerEntityRenderingHandler(EntityMiniShark.class, new RenderMiniShark());
    }
}
