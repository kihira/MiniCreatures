package kihira.minicreatures.common.personality;

import net.minecraft.entity.EntityLiving;

//This would probably serve better as abstract however I like the flexibility of this
public interface IPersonality {

    public Mood getCurrentPersonality();

    public Personality getPersonality();

    public void setPersonality(Personality personality);

    public EntityLiving theEntity();
}
