package kihira.minicreatures.common.personality;

import com.google.gson.Gson;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.network.PersonalityMessage;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class Personality {

    public static final List<Mood> moodList = new ArrayList<Mood>();

    private Mood currentMood;
    private int currentMoodTime = 0;
    private float happinessLevel = 0;
    private float hostilityLevel = 0;

    private final float upperBound = 50;
    private final float lowerBound = -50;

    public Personality() {}

    public void onUpdate(IPersonality theEntity) {
        if (this.currentMood != null) {
            //If current mood has exceeded its max limit, reset it
            if (this.currentMood.maxMoodTime > this.currentMoodTime) {
                this.currentMood = null;
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

    public Mood getCurrentMood() {
        return this.currentMood;
    }

    public float getHappinessLevel() {
        return this.happinessLevel;
    }

    public float getHostilityLevel() {
        return this.hostilityLevel;
    }

    public void changeHappinessLevel(float change) {
        MathHelper.clamp_float(this.happinessLevel += change, this.lowerBound, this.upperBound);
        MiniCreatures.logger.info("Changed happiness level to %s(%s)", this.happinessLevel, change);
    }

    public void changeHostilityLevel(float change) {
        MathHelper.clamp_float(this.hostilityLevel += change, this.lowerBound, this.upperBound);
        MiniCreatures.logger.info("Changed hostility level to %s(%s)", this.happinessLevel, change);
    }
}
