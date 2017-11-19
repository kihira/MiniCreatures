package kihira.minicreatures.common.network;

import io.netty.buffer.ByteBuf;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
            EntityPlayer entityPlayer = ctx.getServerHandler().player;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ).expand(10, 10, 10);
            List entityList = entityPlayer.world.getEntitiesWithinAABB(EntityMiniPlayer.class, axisalignedbb);

            for (Object entry : entityList) {
                if (entry instanceof EntityMiniPlayer) {
                    EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entry;
                    if (miniPlayer.getOwner() == entityPlayer) {
                        EntityLivingBase attackTarget = (EntityLivingBase) entityPlayer.world.getEntityByID(message.targetEntityID);
                        if (attackTarget.isOnSameTeam(miniPlayer)) return null;
                        if ((attackTarget instanceof EntityTameable) && (((EntityTameable) attackTarget).getOwner() == entityPlayer))
                            return null;

                        miniPlayer.setAttackTarget(attackTarget);
                        //entityPlayer.addChatComponentMessage(new ChatComponentText(miniPlayer.getCommandSenderName() + ": Attacking " + attackTarget.getCommandSenderName()));
                    }
                }
            }
            return null;
        }
    }
}
