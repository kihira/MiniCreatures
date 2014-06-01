package kihira.minicreatures.common.personality;

//This would probably serve better as abstract however I like the flexibility of this
public interface IPersonality {

    public Mood getCurrentPersonality();

    public Personality getPersonality();
}
