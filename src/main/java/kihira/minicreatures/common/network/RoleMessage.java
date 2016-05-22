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
import kihira.minicreatures.common.entity.ai.EnumRole;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RoleMessage implements IMessage {

    private int entityID;
    private int roleID;

    public RoleMessage() {}
    public RoleMessage(int entityID, EnumRole role) {
        this.entityID = entityID;
        this.roleID = role.ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        roleID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(roleID);
    }

    public static class RoleMessageHandler implements IMessageHandler<RoleMessage, IMessage> {

        @Override
        public IMessage onMessage(RoleMessage message, MessageContext ctx) {
            Entity entity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityID);
            if (entity instanceof EntityMiniPlayer) {
                ((EntityMiniPlayer) entity).setRole(EnumRole.values()[message.roleID]);
            }
            else {
                MiniCreatures.logger.warn("Attempted to set a role for an invalid entity! ID: " + message.entityID);
            }
            return null;
        }
    }
}
