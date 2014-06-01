package kihira.minicreatures.common.network;

import com.google.gson.Gson;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
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
                Gson gson = new Gson();
                Personality personality = gson.fromJson(message.personalityJson, Personality.class);
                if (personality != null) {
                    ((IPersonality) entity).setPersonality(personality);
                }
            }
            return null;
        }
    }
}
