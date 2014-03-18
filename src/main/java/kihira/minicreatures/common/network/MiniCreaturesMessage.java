package kihira.minicreatures.common.network;

import com.google.common.base.Strings;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MiniCreaturesMessage implements IMessage {
    public abstract IMessage onMessage(MiniCreaturesMessage message, ChannelHandlerContext ctx, Side side);

    public static class UpdateEntityMessage extends MiniCreaturesMessage {
        private int entityID;
        private ArrayList<String> partsList = new ArrayList<String>();

        public UpdateEntityMessage() {}
        public UpdateEntityMessage(int entityID, ArrayList<String> partsList) {
            this.entityID = entityID;
            this.partsList = partsList;
        }
        public UpdateEntityMessage(int entityID, String ... partsList) {
            this.entityID = entityID;
            Collections.addAll(this.partsList, partsList);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityID = buf.readInt();
            this.partsList = new ArrayList<String>();
            while (buf.isReadable()) {
                try {
                    this.partsList.add(ByteBufUtils.readUTF8String(buf));
                } catch (Exception e) {
                    break;
                }
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityID);
            for (String part : this.partsList) {
                ByteBufUtils.writeUTF8String(buf, part);
            }
        }

        @Override
        public IMessage onMessage(MiniCreaturesMessage message, ChannelHandlerContext ctx, Side side) {
            if (side == Side.SERVER) {
                EntityPlayer entityPlayer = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
                for (String part : this.partsList) {
                    if (!Strings.isNullOrEmpty(part)) MiniCreatures.logger.info("Part: " + part);
                }
                ((IMiniCreature) entityPlayer.worldObj.getEntityByID(this.entityID)).setParts(this.partsList, false);
            }
            return null;
        }
    }

    public static class SetAttackTargetMessage extends MiniCreaturesMessage {
        private int targetEntityID;

        public SetAttackTargetMessage() {}
        public SetAttackTargetMessage(int targetEntityID) {
            this.targetEntityID = targetEntityID;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.targetEntityID = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.targetEntityID);
        }

        @Override
        public IMessage onMessage(MiniCreaturesMessage message, ChannelHandlerContext ctx, Side side) {
            if (side == Side.SERVER) {
                EntityPlayer entityPlayer = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
                AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ).expand(10, 10, 10);
                List entityList = entityPlayer.worldObj.getEntitiesWithinAABB(EntityMiniPlayer.class, axisalignedbb);

                if (entityList != null) {
                    for (Object entry : entityList) {
                        if (entry instanceof EntityMiniPlayer) {
                            EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entry;
                            if (miniPlayer.getOwner() == entityPlayer) {
                                EntityLivingBase attackTarget = (EntityLivingBase) entityPlayer.worldObj.getEntityByID(this.targetEntityID);
                                if (attackTarget.isOnSameTeam(miniPlayer)) return null;
                                if ((attackTarget instanceof EntityTameable) && (((EntityTameable) attackTarget).getOwner() == entityPlayer)) return null;

                                miniPlayer.setAttackTarget(attackTarget);
                                entityPlayer.addChatComponentMessage(new ChatComponentText(miniPlayer.getCommandSenderName() + ": Attacking " + attackTarget.getCommandSenderName()));
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
