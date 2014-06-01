package kihira.minicreatures.common.personality;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

import java.util.Collection;

public class Personality {

    public static final Multimap<Class<? extends EntityLiving>, PersonalityType> personalityMap = HashMultimap.create();

    public final EntityLiving theEntity;
    private PersonalityType currentMood;
    private int currentMoodTime = 0;

    private float happinessLevel = 0;
    private float hostilityLevel = 0;

    private final float upperBound = 50;
    private final float lowerBound = -50;

    public Personality(EntityLiving theEntity, float happinessLevel, float hostilityLevel) {
        this.theEntity = theEntity;
        this.happinessLevel = happinessLevel;
        this.hostilityLevel = hostilityLevel;
    }

    public Personality(EntityLiving theEntity) {
        this.theEntity = theEntity;
    }

    public PersonalityType getCurrentMood() {
        if (this.currentMood != null) {
            //If current mood has exceeded its max limit, reset it
            if (this.currentMood.maxMoodTime > this.currentMoodTime) {
                this.currentMood = null;
                this.currentMoodTime = 0;
            }
            //Return current mood
            else return this.currentMood;
        }

        //Find new mood
        Collection<PersonalityType> personalityTypes = personalityMap.get(this.theEntity.getClass());
        if (personalityTypes != null && !personalityTypes.isEmpty()) {
            for (PersonalityType personalityType : personalityTypes) {
                if (personalityType.isValidMood(this)) {
                    this.currentMood = personalityType;
                    this.currentMoodTime = 0;
                    return this.currentMood;
                }
            }
        }
        return null;
    }

    public float getHappinessLevel() {
        return this.happinessLevel;
    }

    public float getHostilityLevel() {
        return this.hostilityLevel;
    }

    public void changeHappinessLevel(float change) {
        MathHelper.clamp_float(this.happinessLevel += change, this.lowerBound, this.upperBound);
    }

    public void changeHostilityLevel(float change) {
        MathHelper.clamp_float(this.hostilityLevel += change, this.lowerBound, this.upperBound);
    }
}
