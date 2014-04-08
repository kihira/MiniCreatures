package kihira.minicreatures.common.entity.ai;

public abstract class PersonalityAIIdleBase {

    public PersonalityAIIdleBase() {

    }

    public abstract boolean shouldUpdate();

    public abstract void onUpdate();

    public abstract PersonalityManager.Message getMessage();
}
