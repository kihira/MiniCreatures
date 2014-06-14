package kihira.minicreatures.common.personality;

import com.google.gson.Gson;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.network.PersonalityMessage;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class Personality {

    public static final List<Mood> moodList = new ArrayList<Mood>();

    /**
     * A base neutral mood for when there is no other moods
     */
    private final Mood neturalMood = new Mood("neutral");

    /**
     * The current active mood
     */
    private Mood currentMood;
    /**
     * How long the mood has been active for
     */
    private int currentMoodTime = 0;
    /**
     * Current happiness level of the entity
     */
    private float happinessLevel = 0;
    /**
     * Current hostility level of the entity
     */
    private float hostilityLevel = 0;

    private final float upperBound = 50;
    private final float lowerBound = -50;

    public Personality() {}

    public void onUpdate(IPersonality theEntity) {
        if (this.currentMood != null) {
            //If current mood has exceeded its max limit, reset it
            if (this.currentMood.maxMoodTime > this.currentMoodTime) {
                this.currentMood = this.neturalMood;
                this.currentMoodTime = 0;
            }
        }

        //Find new mood
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
        if (this.currentMood != null) {
            this.currentMoodTime++;
        }
    }

    /**
     * Gets the current mood. If there is none, sets a neutral mood and returns that
     * @return the current mood
     */
    public Mood getCurrentMood() {
        if (this.currentMood == null) {
            this.currentMood = this.neturalMood;
        }
        return this.currentMood;
    }

    /**
     * @return Get the current happiness level for this personality
     */
    public float getHappinessLevel() {
        return this.happinessLevel;
    }

    /**
     * @return Get the current hostility level for this personality
     */
    public float getHostilityLevel() {
        return this.hostilityLevel;
    }

    /**
     * Changes the current happiness level by a certain amount. This is only a change, so it will be added on to the
     * current value!
     * @param change The value change (supports negatives)
     */
    public void changeHappinessLevel(float change) {
        MathHelper.clamp_float(this.happinessLevel += change, this.lowerBound, this.upperBound);
        MiniCreatures.logger.info("Changed happiness level to %s(%s)", this.happinessLevel, change);
    }

    /**
     * Changes the current hostility level by a certain amount. This is only a change, so it will be added on to the
     * current value!
     * @param change The value change (supports negatives)
     */
    public void changeHostilityLevel(float change) {
        MathHelper.clamp_float(this.hostilityLevel += change, this.lowerBound, this.upperBound);
        MiniCreatures.logger.info("Changed hostility level to %s(%s)", this.happinessLevel, change);
    }
}
