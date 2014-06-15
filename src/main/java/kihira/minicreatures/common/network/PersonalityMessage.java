package kihira.minicreatures.common.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.personality.IPersonality;
import kihira.minicreatures.common.personality.Personality;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

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
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            if (entity instanceof IPersonality) {
                Gson gson = new GsonBuilder().setVersion(1.0).create();
                try {
                    Personality newPersonality = gson.fromJson(message.personalityJson, Personality.class);
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
