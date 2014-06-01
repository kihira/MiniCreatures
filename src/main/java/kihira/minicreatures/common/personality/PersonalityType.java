package kihira.minicreatures.common.personality;

import net.minecraft.entity.EntityLiving;

public class PersonalityType {

    public String name = "";
    public String className;
    public float maxHappiness = 0;
    public float minHappiness = 0;
    public float maxHostility = 0;
    public float minHostility = 0;

    public int maxMoodTime = 100;
    public int minMoodTime = 0;

    public PersonalityType(String name, Class<? extends EntityLiving> entityClass, float maxHappiness, float minHappiness, float maxHostility, float minHostility) {
        this.name = name;
        this.className = entityClass.getName();
        this.maxHappiness = maxHappiness;
        this.minHappiness = minHappiness;
        this.maxHostility = maxHostility;
        this.minHostility = minHostility;
    }

    public boolean isValidMood(Personality personality) {
        if (personality.theEntity != null) {
            float happinessLevel = personality.getHappinessLevel();
            float hostilityLevel = personality.getHostilityLevel();
            if (happinessLevel >= this.minHappiness && happinessLevel <= this.maxHappiness &&
                    hostilityLevel >= this.minHostility && hostilityLevel <= this.maxHostility) {
                return true;
            }
        }
        return false;
    }

    public Class getEntityClass() {
        try {
            return Class.forName(this.className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
