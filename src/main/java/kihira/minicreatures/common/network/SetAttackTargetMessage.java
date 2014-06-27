package kihira.minicreatures.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class SetAttackTargetMessage implements IMessage {
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

    public static class SetAttackTargetMessageHandler implements IMessageHandler<SetAttackTargetMessage, IMessage> {

        @Override
        public IMessage onMessage(SetAttackTargetMessage message, MessageContext ctx) {
            EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ).expand(10, 10, 10);
            List entityList = entityPlayer.worldObj.getEntitiesWithinAABB(EntityMiniPlayer.class, axisalignedbb);

            if (entityList != null) {
                for (Object entry : entityList) {
                    if (entry instanceof EntityMiniPlayer) {
                        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entry;
                        if (miniPlayer.getOwner() == entityPlayer) {
                            EntityLivingBase attackTarget = (EntityLivingBase) entityPlayer.worldObj.getEntityByID(message.targetEntityID);
                            if (attackTarget.isOnSameTeam(miniPlayer)) return null;
                            if ((attackTarget instanceof EntityTameable) && (((EntityTameable) attackTarget).getOwner() == entityPlayer))
                                return null;

                            miniPlayer.setAttackTarget(attackTarget);
                            //entityPlayer.addChatComponentMessage(new ChatComponentText(miniPlayer.getCommandSenderName() + ": Attacking " + attackTarget.getCommandSenderName()));
                        }
                    }
                }
            }
            return null;
        }
    }
}
