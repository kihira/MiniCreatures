/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

public enum EnumRole {
    NONE,
    MINER,
    FARMER,
    COMBAT;

    public static void resetAI(EntityMiniPlayer miniPlayer) {
        for (Object obj : miniPlayer.tasks.taskEntries) {
            EntityAIBase ai = obj instanceof EntityAITasks.EntityAITaskEntry ? ((EntityAITasks.EntityAITaskEntry) obj).action : (EntityAIBase) obj;
            if (ai instanceof IRole) {
                //((IRole) ai).markForRemoval();
                miniPlayer.tasks.removeTask(ai); //TODO CME
            }
        }
    }

    public void applyAI(EntityMiniPlayer miniPlayer) {
        switch (this) {
            case MINER: {
                miniPlayer.tasks.addTask(3, new EntityAICollect(miniPlayer, 1.5F, 7.5F));
                break;
            }
            case COMBAT: {
                miniPlayer.tasks.addTask(3, new EntityAIUsePotion(miniPlayer, 0.5F, 2, 100));
                break;
            }
        }
    }
}
