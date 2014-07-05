package kihira.minicreatures.common.personality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mood is basically a state of the personality. It defines the entities actions
 */
public class Mood implements Serializable {

    /**
     * This is used for serialization so we know if it's a sub-class or not
     */
    private String clazz = this.getClass().getCanonicalName();
    public String name = "";
    /**
     * A list of class names (As returned by {@link Class#getName()}) of entities that can use this mood
     */
    public List<String> validClassNames = new ArrayList<String>();
    /**
     * These MoodVariables are used to define the max and minimum amounts for the MoodVariables in the main Personality
     */
    public HashMap<String, MoodVariable> moodVariablesLimits = new HashMap<String, MoodVariable>();

    /**
     * This defines the maximum amount of time in ticks that the mood can be active. When it reaches this time, the mod
     * will attempt to find a new mood (may be the same mood)
     */
    public int maxMoodTime = 100;
    /**
     * Minimum amount of time the mood is active. The mod won't attempt to find a new mood if the mood hasn't been active
     * longer then minimum
     */
    public int minMoodTime = 0;

    public Mood(String name) {
        this(name, new ArrayList<String>(), new HashMap<String, MoodVariable>());
    }

    public Mood(String name, List<String> validClassNames, HashMap<String, MoodVariable> moodVariablesLimits) {
        this.name = name;
        this.validClassNames = validClassNames;
        this.moodVariablesLimits = moodVariablesLimits;
    }

    /**
     * Checks if this mood is valid for the entity and its current personality. The basic moods (base Mood class) simply
     * checks if the {@link kihira.minicreatures.common.personality.MoodVariable}s on the
     * {@link kihira.minicreatures.common.personality.Personality} are within the bounds for this mood.
     * @param personality The personality
     * @param theEntity The entity
     * @return Whether the mood is valid or not
     */
    public boolean isValidMood(Personality personality, IPersonality theEntity) {
        if (theEntity != null && this.validClassNames.contains(theEntity.getClass().getName())) {
            for (Map.Entry<String, MoodVariable> entry : this.moodVariablesLimits.entrySet()) {
                if (!entry.getValue().isWithinBounds(personality.getMoodVariableValue(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the {@link kihira.minicreatures.common.personality.MoodVariable} for the name provided. If it does not
     * exist, a new one is returned instead
     * @param name Name of the variable
     * @return The variable
     */
    public MoodVariable getMoodVariable(String name) {
        return this.moodVariablesLimits.containsKey(name) ? this.moodVariablesLimits.get(name) : new MoodVariable();
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Max Time: %s, Min Time: %s, Valid Classes: [%s], Mood Variables: [%s]", this.name, this.maxMoodTime, this.minMoodTime, this.validClassNames, this.moodVariablesLimits);
    }
}
