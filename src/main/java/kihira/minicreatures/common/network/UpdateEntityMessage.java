package kihira.minicreatures.common.network;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.ICustomisable;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Collections;

public class UpdateEntityMessage implements IMessage {
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

    public static class UpdateEntityMessageHandler implements IMessageHandler<UpdateEntityMessage, IMessage> {

        @Override
        public IMessage onMessage(UpdateEntityMessage message, MessageContext ctx) {
            for (String part : message.partsList) {
                if (!Strings.isNullOrEmpty(part)) MiniCreatures.logger.info("Part: " + part);
            }
            Entity entity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityID);
            if (entity instanceof ICustomisable) {
                ((ICustomisable) entity).setParts(message.partsList, false);
            }
            return null;
        }
    }
}
