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
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityTameable;

import java.util.ArrayList;
import java.util.List;

public enum EnumRole {
    NONE,
    MINER,
    FARMER,
    COMBAT;

    //Yeah it's not nice but this won't be called often
    @SuppressWarnings("unchecked")
    public static <T extends EntityTameable & IMiniCreature> void resetAI(T entity) {
        List tasks = new ArrayList(entity.tasks.taskEntries);
        for (Object obj : tasks) {
            EntityAIBase ai = obj instanceof EntityAITasks.EntityAITaskEntry ? ((EntityAITasks.EntityAITaskEntry) obj).action : (EntityAIBase) obj;
            entity.tasks.removeTask(ai); //Might be a CME again
        }
        //Clear list
        entity.tasks.taskEntries.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityTameable & IMiniCreature> void applyAI(T entity) {
        switch (this) {
            case MINER: {
                entity.tasks.addTask(3, new EntityAICollect(entity, 1.5F, 7.5F));
                break;
            }
            case COMBAT: {
                break;
            }
        }
        entity.applyAI(this);
    }
}
