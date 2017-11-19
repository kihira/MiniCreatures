package kihira.minicreatures.common.network;

import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.GsonHelper;
import kihira.minicreatures.common.personality.IPersonality;
import kihira.minicreatures.common.personality.Personality;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PersonalityMessage implements IMessage {

    public String personalityJson;
    public int entityID;

    public PersonalityMessage() {}
    public PersonalityMessage(int entityID, String personalityJson) {
        this.entityID = entityID;
        this.personalityJson = personalityJson;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.personalityJson = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        ByteBufUtils.writeUTF8String(buf, this.personalityJson);
    }

    public static class PersonalityMessageHandler implements IMessageHandler<PersonalityMessage, IMessage> {

        public PersonalityMessageHandler() {}

        @Override
        public IMessage onMessage(PersonalityMessage message, MessageContext ctx) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
            if (entity instanceof IPersonality) {
                try {
                    Personality newPersonality = GsonHelper.fromJson(message.personalityJson, Personality.class);
                    if (newPersonality != null) {
                        ((IPersonality) entity).setPersonality(newPersonality);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            else {
                MiniCreatures.logger.warn("Received a personality update for an entity that is not a personality! " + entity);
            }
            return null;
        }
    }
}
