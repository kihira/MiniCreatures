package kihira.minicreatures.common.personality;

import java.util.ArrayList;
import java.util.List;

public class Mood {

    public String name = "";
    public List<String> validClassNames = new ArrayList<String>();
    public float maxHappiness = 0;
    public float minHappiness = 0;
    public float maxHostility = 0;
    public float minHostility = 0;

    public int maxMoodTime = 100;
    public int minMoodTime = 0;

    public Mood(String name) {
        this(name, new ArrayList<String>(), 0, 0, 0, 0);
    }

    public Mood(String name, List<String> validClassNames, float maxHappiness, float minHappiness, float maxHostility, float minHostility) {
        this.name = name;
        this.validClassNames = validClassNames;
        this.maxHappiness = maxHappiness;
        this.minHappiness = minHappiness;
        this.maxHostility = maxHostility;
        this.minHostility = minHostility;
    }

    public boolean isValidMood(Personality personality, IPersonality theEntity) {
        if (theEntity != null && this.validClassNames.contains(theEntity.getClass().getName())) {
            float happinessLevel = personality.getHappinessLevel();
            float hostilityLevel = personality.getHostilityLevel();
            if (happinessLevel >= this.minHappiness && happinessLevel <= this.maxHappiness &&
                    hostilityLevel >= this.minHostility && hostilityLevel <= this.maxHostility) {
                return true;
            }
        }
        return false;
    }
}
