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

import cpw.mods.fml.common.registry.GameRegistry;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.client.model.parts.PartModelFairy;
import kihira.minicreatures.client.model.parts.PartModelHorns;
import kihira.minicreatures.client.model.parts.PartModelTail;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.network.PacketHandler;

public class CommonProxy {

    public final PacketHandler packetHandler = new PacketHandler();

    public void registerRenderers() { }

    //TODO only register on server join after config has synced with server to allow server disabling
    public void registerCustomizerParts() {
        CustomizerRegistry.registerPart("fairy", new PartModelFairy());
        CustomizerRegistry.registerPart("horns", new PartModelHorns());
        CustomizerRegistry.registerPart("tail", new PartModelTail());
    }

    public void registerItems() {
        if (MiniCreatures.enableCustomizer) GameRegistry.registerItem(MiniCreatures.itemCustomizer, "customizer");
        //GameRegistry.registerItem(itemMindControlHelmet, "mindControlHelmet");
    }

}
