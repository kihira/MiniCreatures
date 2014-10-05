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
    COMBAT;

    public static void resetAI(EntityMiniPlayer miniPlayer) {
        //TODO CME
        for (Object obj : miniPlayer.tasks.taskEntries) {
            EntityAIBase ai = obj instanceof EntityAITasks.EntityAITaskEntry ? ((EntityAITasks.EntityAITaskEntry) obj).action : (EntityAIBase) obj;
            if (ai instanceof IRole) {
                miniPlayer.tasks.removeTask(ai);
            }
        }
    }

    public void applyAI(EntityMiniPlayer miniPlayer) {
        switch (this) {
            case MINER: {
                break;
            }
            case COMBAT: {
                miniPlayer.tasks.addTask(5, new EntityAIUsePotion(miniPlayer, 0.5F, 2, 100));
                break;
            }
        }
    }
}
