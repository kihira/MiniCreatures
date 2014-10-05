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

public enum EnumRole {
    NONE,
    MINER,
    COMBAT,
    TEST1,
    TEST2;

    public void resetAI(EntityMiniPlayer miniPlayer) {
        for (Object obj : miniPlayer.tasks.taskEntries) {
            if (obj instanceof IRole) {
                miniPlayer.tasks.removeTask((EntityAIBase) obj);
            }
        }
    }

    public void applyAI(EntityMiniPlayer miniPlayer) {
        switch (this) {
            case MINER: {
                break;
            }
            case COMBAT: {
                break;
            }
        }
    }
}
