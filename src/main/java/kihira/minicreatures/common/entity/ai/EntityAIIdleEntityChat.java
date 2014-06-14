/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.minicreatures.common.entity.ai;

import com.google.common.collect.Iterators;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;

import java.util.List;

public class EntityAIIdleEntityChat extends EntityAIChat {

    private final int searchRadius;

    private EntityLivingBase target;

    public EntityAIIdleEntityChat(EntityMiniPlayer miniPlayer, int searchRadius) {
        super(miniPlayer);
        this.searchRadius = searchRadius;
    }

    @Override
    public boolean shouldExecute() {
        //Don't execute if untamed, sitting or moving
        if (!super.shouldExecute()) {
            return false;
        }
        else if (this.miniPlayer.getRNG().nextInt(200) != 0) {
            return false;
        }
        //Find nearby entity
        else {
            List list = this.miniPlayer.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.miniPlayer.boundingBox.expand(this.searchRadius, 3, this.searchRadius));

            if (list != null && !list.isEmpty()) {
                int i = 0;
                while (i < 3) {
                    //Get a random entity from the list
                    EntityLivingBase entityLiving = (EntityLivingBase) list.get(this.miniPlayer.getRNG().nextInt(list.size()));

                    if (entityLiving.isEntityAlive() && entityLiving != this.miniPlayer) {
                        //Set it as our target
                        this.target = entityLiving;
                        return true;
                    }

                    i++;
                }
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        //Look for some valid chat lines to say about this entity
        String entityName = EntityList.getEntityString(this.target);

        //Look for some valid chat lines to say about this block
        if (entityName != null) {
            for (int i = 0; i < 10; i++) {
                String chatLine = "chat.idle.entity." + entityName + "." + i;
                if (StatCollector.canTranslate(chatLine)) {
                    this.chatLines = Iterators.forArray(StatCollector.translateToLocal(chatLine).split(";"));
                    break;
                }
            }
        }
        //If we can't find anything, load default chat if there is a chance too
        if (this.chatLines == null && this.miniPlayer.getRNG().nextInt(30) == 0) {
            for (int i = 0; i < 10; i++) {
                String chatLine = "chat.idle.entity.generic." + i;
                if (StatCollector.canTranslate(chatLine)) {
                    this.chatLines = Iterators.forArray(StatCollector.translateToLocal(chatLine).split(";"));
                    break;
                }
            }
        }

        //Look at the entity
        this.miniPlayer.faceEntity(this.target, 3, 3);
    }

    @Override
    public void updateTask() {
        super.updateTask();

        //Look at the entity
        this.miniPlayer.faceEntity(this.target, 3, 3);
    }

    @Override
    public boolean continueExecuting() {
        return this.target != null && this.target.isEntityAlive() && super.continueExecuting();
    }

    @Override
    public void resetTask() {
        this.target = null;
        super.resetTask();
    }
}
