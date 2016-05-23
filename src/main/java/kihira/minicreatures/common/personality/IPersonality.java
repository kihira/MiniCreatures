package kihira.minicreatures.common.personality;

import net.minecraft.entity.EntityLiving;

//This would probably serve better as abstract however I like the flexibility of this. Or maybe a trait?
public interface IPersonality {

    /**
     * Gets the current Personality for the entity
     * @return the personality
     */
    Personality getPersonality();

    /**
     * Sets the current Personality for the entity. Mostly used for syncing from server to client as Personality is
     * serialized
     * @param personality The personality
     */
    void setPersonality(Personality personality);

    /**
     * A helper function to get the current entity
     * @return the entity
     */
    EntityLiving theEntity();

    /**
     * Gets the current chat message the entity is saying
     * @return The chat message
     */
    String getChat();

    /**
     * Sets a current chat message for the entity. It should be in the format "first line;second line"
     * @param string The chat message
     */
    void setChat(String string);
}
