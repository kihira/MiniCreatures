package kihira.minicreatures.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;

public class PersonalityAI extends EntityAIBase {

    private final EntityTameable theEntity;

    public PersonalityAI(EntityTameable entityLiving) {
        this.theEntity = entityLiving;
    }

    @Override
    public boolean shouldExecute() {
        return this.theEntity.isTamed();
    }
}
