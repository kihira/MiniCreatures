/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.network;

import io.netty.buffer.ByteBuf;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
                // todo support off hand
                miniPlayer.setItemInUse(miniPlayer.getHeldItem(EnumHand.MAIN_HAND), miniPlayer.getHeldItem(EnumHand.MAIN_HAND).getMaxItemUseDuration());
            }
            else {
                MiniCreatures.logger.warn("Received a personality update for an entity that is not a Mini Player! " + entity);
            }
            return null;
        }
    }
}
