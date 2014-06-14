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
import cpw.mods.fml.common.registry.GameData;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.util.StatCollector;

import java.util.Random;

public class EntityAIIdleBlockChat extends EntityAIChat {

    private final int searchRadius;

    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIIdleBlockChat(EntityMiniPlayer miniPlayer, int searchRadius) {
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
        //Find nearby block
        else {
            double x;
            double y;
            double z;
            Random random = new Random();

            //Search for block nearby to look at
            for (int i = 0; i < 5; i++) {
                x = (int) (this.miniPlayer.posX + random.nextInt(2 * this.searchRadius) - this.searchRadius);
                y = (int) (this.miniPlayer.posY + random.nextInt(2 * 3) - 3);
                z = (int) (this.miniPlayer.posZ + random.nextInt(2 * this.searchRadius) - this.searchRadius);
                //Check it's not an air block cause we don't want to talk hot air :D
                if (y > 0 && !this.miniPlayer.worldObj.isAirBlock((int) x, (int) y, (int) z)) {
                    this.targetX = x;
                    this.targetY = y;
                    this.targetZ = z;

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return !this.miniPlayer.worldObj.isAirBlock((int) this.targetX, (int) this.targetY, (int) this.targetZ) && super.continueExecuting();
    }

    @Override
    public void startExecuting() {
        Block targetBlock = this.miniPlayer.worldObj.getBlock((int) this.targetX, (int) this.targetY, (int) this.targetZ);

        //Look for some valid chat lines to say about this block
        String blockName = GameData.getBlockRegistry().getNameForObject(targetBlock);
        for (int i = 0; i < 10; i++) {
            String chatLine = "chat.idle.block." + this.miniPlayer.getPersonality().getCurrentMood().name + "." + blockName + "." + i;
            if (StatCollector.canTranslate(chatLine)) {
                this.chatLines = Iterators.forArray(StatCollector.translateToLocal(chatLine).split(";"));
                break;
            }
        }
        //If we can't find anything, load default chat if there is a chance too
        if (this.chatLines == null && this.miniPlayer.getRNG().nextInt(30) == 0) {
            for (int i = 0; i < 10; i++) {
                String chatLine = "chat.idle.block.generic." + this.miniPlayer.getPersonality().getCurrentMood().name + "." + i;
                if (StatCollector.canTranslate(chatLine)) {
                    this.chatLines = Iterators.forArray(StatCollector.translateToLocal(chatLine).split(";"));
                    break;
                }
            }
        }

        //Look at the block
        this.miniPlayer.getLookHelper().setLookPosition(this.targetX, this.targetY, this.targetZ, 3, 3);
    }

    @Override
    public void updateTask() {
        //Look at the block just incase view has changed
        this.miniPlayer.getLookHelper().setLookPosition(this.targetX, this.targetY, this.targetZ, 6, 6);

        super.updateTask();
    }

    @Override
    public void resetTask() {
        this.targetX = this.targetY = this.targetZ = -1;
        super.resetTask();
    }
}
