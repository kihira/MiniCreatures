package kihira.minicreatures.common.personality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mood implements Serializable {

    public String name = "";
    public List<String> validClassNames = new ArrayList<String>();
    /**
     * These MoodVariables are used to define the max and minimum amounts for the MoodVariables in the main Personality
     */
    public HashMap<String, MoodVariable> moodVariablesLimits = new HashMap<String, MoodVariable>();

    public int maxMoodTime = 100;
    public int minMoodTime = 0;

    public Mood(String name) {
        this(name, new ArrayList<String>(), new HashMap<String, MoodVariable>());
    }

    public Mood(String name, List<String> validClassNames, HashMap<String, MoodVariable> moodVariablesLimits) {
        this.name = name;
        this.validClassNames = validClassNames;
        this.moodVariablesLimits = moodVariablesLimits;
    }

    public boolean isValidMood(Personality personality, IPersonality theEntity) {
        if (theEntity != null && this.validClassNames.contains(theEntity.getClass().getName())) {
            int happinessLevel = personality.getMoodVariableValue("happiness");
            int hostilityLevel = personality.getMoodVariableValue("hostility");
            MoodVariable happinessLimits = this.getMoodVariable("happiness");
            MoodVariable hostilityLimits = this.getMoodVariable("hostility");
            if (happinessLimits.isWithinBounds(happinessLevel) && hostilityLimits.isWithinBounds(hostilityLevel)) {
                return true;
            }
        }
        return false;
    }

    private MoodVariable getMoodVariable(String name) {
        return this.moodVariablesLimits.containsKey(name) ? this.moodVariablesLimits.get(name) : new MoodVariable();
    }
}
