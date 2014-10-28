/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.IMiniCreature;
import kihira.minicreatures.common.entity.ai.combat.EntityAIUsePotion;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityTameable;

public enum EnumRole {
    NONE,
    MINER,
    FARMER,
    COMBAT;

    public static <T extends EntityTameable & IMiniCreature> void resetAI(T entity) {
        for (Object obj : entity.tasks.taskEntries) {
            EntityAIBase ai = obj instanceof EntityAITasks.EntityAITaskEntry ? ((EntityAITasks.EntityAITaskEntry) obj).action : (EntityAIBase) obj;
            if (ai instanceof IRole) {
                //((IRole) ai).markForRemoval();
                entity.tasks.removeTask(ai); //TODO CME
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityTameable & IMiniCreature> void applyAI(T entity) {
        switch (this) {
            case MINER: {
                entity.tasks.addTask(3, new EntityAICollect(entity, 1.5F, 7.5F));
                break;
            }
            case COMBAT: {
                if (entity instanceof EntityMiniPlayer) entity.tasks.addTask(3, new EntityAIUsePotion(((EntityMiniPlayer) entity), 0.5F, 2, 100));
                break;
            }
        }
    }
}
