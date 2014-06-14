package kihira.minicreatures.common.personality;

import com.google.gson.Gson;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.network.PersonalityMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is serializable by Gson and should be saved that way
 */
public class Personality implements Serializable {

    public static final List<Mood> moodList = new ArrayList<Mood>();

    /**
     * A base neutral mood for when there is no other moods
     */
    public static final Mood neturalMood = new Mood("neutral");

    /**
     * All the MoodVariables assigned to this personality
     */
    public HashMap<String, MoodVariable> moodVariables = new HashMap<String, MoodVariable>();

    /**
     * The current active mood
     */
    private Mood currentMood;
    /**
     * How long the mood has been active for
     */
    private int currentMoodTime = 0;

    public Personality() {
        this.moodVariables.put("happiness", new MoodVariable());
        this.moodVariables.put("hostility", new MoodVariable());
    }

    public void onUpdate(IPersonality theEntity) {
        if (this.currentMood != null) {
            //If current mood has exceeded its max limit, reset it
            if (this.currentMood.maxMoodTime > this.currentMoodTime) {
                this.currentMood = neturalMood;
                this.currentMoodTime = 0;
            }
        }

        //Find new mood
        if (this.currentMood == neturalMood || this.currentMood == null) {
            if (!moodList.isEmpty()) {
                for (Mood mood : moodList) {
                    if (mood.isValidMood(this, theEntity)) {
                        this.currentMood = mood;
                        this.currentMoodTime = 0;
                        Gson gson = new Gson();
                        MiniCreatures.proxy.simpleNetworkWrapper.sendToDimension(new PersonalityMessage(theEntity.theEntity().getEntityId(), gson.toJson(this.currentMood)), theEntity.theEntity().dimension);
                    }
                }
            }
        }

        if (this.currentMood != null) {
            this.currentMoodTime++;
        }

        //Minute
        if (theEntity.theEntity().worldObj.getTotalWorldTime() % 1200 == 0) {
            for (MoodVariable moodVariable : this.moodVariables.values()) {
                if (moodVariable.getCurrentValue() > moodVariable.getRestingValue()) {
                    moodVariable.changeValue(-1);
                }
                else if (moodVariable.getCurrentValue() < moodVariable.getRestingValue()) {
                    moodVariable.changeValue(+1);
                }
            }
        }
    }

    /**
     * Gets the current mood. If there is none, sets a neutral mood and returns that
     * @return the current mood
     */
    public Mood getCurrentMood() {
        if (this.currentMood == null) {
            this.currentMood = neturalMood;
        }
        return this.currentMood;
    }

    /**
     * Changes the value for a mood variable. If a variable by that name doesn't exist, one is created
     * @param name Name of the variable
     * @param value Change
     */
    public void changeMoodVariableLevel(String name, int value) {
        MoodVariable moodVariable = new MoodVariable();
        if (this.moodVariables.containsKey(name)) {
            moodVariable = this.moodVariables.get(name);
        }

        moodVariable.changeValue(value);
    }

    public int getMoodVariableValue(String name) {
        MoodVariable moodVariable = new MoodVariable();
        if (this.moodVariables.containsKey(name)) {
            moodVariable = this.moodVariables.get(name);
        }

        return moodVariable.getCurrentValue();
    }
}
