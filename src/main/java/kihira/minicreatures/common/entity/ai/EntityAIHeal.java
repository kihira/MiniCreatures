/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIHeal extends EntityAIBase {

    private final IMiniCreature miniCreature;
    private final int regenCooldown;
    private final int regenAmount;
    private int ticksToNextRegen;

    public EntityAIHeal(IMiniCreature miniCreature, int regenCooldown, int regenAmount) {
        this.miniCreature = miniCreature;
        this.regenCooldown = regenCooldown;
        this.regenAmount = regenAmount;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateTask() {
        ticksToNextRegen = ticksToNextRegen <= 0 ? 0 : ticksToNextRegen - 1;

        if (ticksToNextRegen == 0) {
            //Natural regen
            miniCreature.getEntity().heal(regenAmount);
            ticksToNextRegen = regenCooldown;
        }
    }

    @Override
    public boolean shouldExecute() {
        return miniCreature.getEntity().getHealth() < miniCreature.getEntity().getMaxHealth();
    }

    @Override
    public void resetTask() {
        ticksToNextRegen = 0;
    }
}
