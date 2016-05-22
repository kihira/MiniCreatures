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
import kihira.minicreatures.client.model.parts.PartModelFairy;
import kihira.minicreatures.client.model.parts.PartModelHorns;
import kihira.minicreatures.client.model.parts.PartModelTail;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.network.RoleMessage;
import kihira.minicreatures.common.network.SetAttackTargetMessage;
import kihira.minicreatures.common.network.UpdateEntityMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public final SimpleNetworkWrapper simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("minicreatures");

    public void registerRenderers() {}

    //TODO only register on server join after config has synced with server to allow server disabling
    public void registerCustomizerParts() {
        CustomizerRegistry.registerPart("fairy", new PartModelFairy());
        CustomizerRegistry.registerPart("horns", new PartModelHorns());
        CustomizerRegistry.registerPart("tail", new PartModelTail());
    }

    public void registerMessages() {
        this.simpleNetworkWrapper.registerMessage(UpdateEntityMessage.UpdateEntityMessageHandler.class, UpdateEntityMessage.class, 1, Side.SERVER);
        this.simpleNetworkWrapper.registerMessage(SetAttackTargetMessage.SetAttackTargetMessageHandler.class, SetAttackTargetMessage.class, 2, Side.SERVER);
        this.simpleNetworkWrapper.registerMessage(RoleMessage.RoleMessageHandler.class, RoleMessage.class, 3, Side.SERVER);
    }

    public void registerItems() {
        if (MiniCreatures.enableCustomizer) GameRegistry.registerItem(MiniCreatures.itemCustomizer, "customizer");
        //GameRegistry.registerItem(itemMindControlHelmet, "mindControlHelmet");
    }
}
