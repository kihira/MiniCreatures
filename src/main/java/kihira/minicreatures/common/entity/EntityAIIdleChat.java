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

package kihira.minicreatures.common.entity;

import com.google.common.collect.Iterators;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.StatCollector;

import java.util.Iterator;
import java.util.Random;

public class EntityAIIdleChat extends EntityAIBase {

    private final EntityMiniPlayer miniPlayer;
    private final int searchRadius;

    private double targetX;
    private double targetY;
    private double targetZ;
    private Iterator<String> chatLines;
    private int chatCooldown = 0;

    public EntityAIIdleChat(EntityMiniPlayer miniPlayer, int searchRadius) {
        this.miniPlayer = miniPlayer;
        this.searchRadius = searchRadius;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        //Don't execute if untamed, sitting or moving
        if (!this.miniPlayer.isTamed() && !this.miniPlayer.isSitting() && !this.miniPlayer.getNavigator().noPath()){
            return false;
        }
        else if (this.miniPlayer.getRNG().nextInt(2) != 0) {
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
        return this.chatCooldown >= 0 || (this.chatLines != null && this.chatLines.hasNext() && !this.miniPlayer.worldObj.isAirBlock((int) this.targetX, (int) this.targetY, (int) this.targetZ));
    }

    @Override
    public void startExecuting() {
        Block targetBlock = this.miniPlayer.worldObj.getBlock((int) this.targetX, (int) this.targetY, (int) this.targetZ);

        //Look for some valid chat lines to say about this block
        for (int i = 0; i < 10; i++) {
            String blockName = GameData.getBlockRegistry().getNameForObject(targetBlock);
            if (StatCollector.canTranslate("chat.idle." + blockName + "." + i)) {
                this.chatLines = Iterators.forArray(StatCollector.translateToLocal("chat.idle." + blockName + "." + i).split(";"));
                break;
            }
        }
        //If we can't find anything, load default chat if there is a chance too
        if (this.chatLines == null && this.miniPlayer.getRNG().nextInt(30) == 0) {
            for (int i = 0; i < 10; i++) {
                if (StatCollector.canTranslate("chat.idle.generic." + i)) {
                    this.chatLines = Iterators.forArray(StatCollector.translateToLocal("chat.idle.generic." + i).split(";"));
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

        //Get next chat line
        if (this.chatCooldown <= 0 && this.chatLines != null && this.chatLines.hasNext()) {
            String chat = this.chatLines.next();
            this.miniPlayer.setChat(chat);
            this.chatCooldown = chat.length() * 2;
        }

        //Reduces the time between chat
        this.chatCooldown--;
    }

    @Override
    public void resetTask() {
        this.targetX = 0;
        this.targetY = -1;
        this.targetZ = 0;
        this.chatLines = null;
        this.chatCooldown = 0;
        this.miniPlayer.setChat("");
    }
}
