/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class ItemUseMessage implements IMessage {

    public int entityID;
    public int itemUseTime;

    public ItemUseMessage() {}
    public ItemUseMessage(int entityID, int itemUseTime) {
        this.entityID = entityID;
        this.itemUseTime = itemUseTime;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.itemUseTime = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(this.itemUseTime);
    }

    public static class ItemUseMessageHandler implements IMessageHandler<ItemUseMessage, IMessage> {

        public ItemUseMessageHandler() {}

        @Override
        public IMessage onMessage(ItemUseMessage message, MessageContext ctx) {
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            if (entity instanceof EntityMiniPlayer) {
                EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;
                miniPlayer.setItemInUse(miniPlayer.getHeldItem(), miniPlayer.getHeldItem().getMaxItemUseDuration());
            }
            else {
                MiniCreatures.logger.warn("Received a personality update for an entity that is not a Mini Player! " + entity);
            }
            return null;
        }
    }
}
