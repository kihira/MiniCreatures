package kihira.minicreatures.common.network;

import com.google.common.base.Strings;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import kihira.minicreatures.MiniCreatures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.ArrayList;

public abstract class MiniCreaturesMessage implements IMessage {
    public abstract IMessage onMessage(MiniCreaturesMessage message, ChannelHandlerContext ctx, Side side);

    public static class UpdateEntityMessage extends MiniCreaturesMessage {
        private int entityID;
        private String partsList;

        public UpdateEntityMessage() {}
        public UpdateEntityMessage(int entityID, ArrayList<String> partsList) {
            this.entityID = entityID;
            for (String part : partsList) {
                this.partsList += part + "|";
            }
        }
        public UpdateEntityMessage(int entityID, String ... partsList) {
            this.entityID = entityID;
            for (String part : partsList) {
                this.partsList += part + "|";
            }
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityID = buf.readInt();
            this.partsList = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityID);
            ByteBufUtils.writeUTF8String(buf, this.partsList);
        }

        @Override
        public IMessage onMessage(MiniCreaturesMessage message, ChannelHandlerContext ctx, Side side) {
            if (side == Side.SERVER) {
                EntityPlayer entityPlayer = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
                for (String part : this.partsList.split("|")) {
                    if (!Strings.isNullOrEmpty(part)) MiniCreatures.logger.info("Part: " + part);
                }
            }
            return null;
        }
    }
}
