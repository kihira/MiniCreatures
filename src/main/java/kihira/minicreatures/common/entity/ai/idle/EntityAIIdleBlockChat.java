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

package kihira.minicreatures.common.entity.ai.idle;

import com.google.common.collect.Iterators;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class EntityAIIdleBlockChat extends EntityAIChat {

    private final int searchRadius;

    private BlockPos target;

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
            Random random = new Random();

            //Search for block nearby to look at
            for (int i = 0; i < 5; i++) {
                BlockPos pos = new BlockPos(
                        (int) (miniPlayer.posX + random.nextInt(2 * searchRadius) - searchRadius),
                        (int) MathHelper.clamp_double(miniPlayer.posY + random.nextInt(2 * 3) - 3, 1, 256),
                        (int) (miniPlayer.posZ + random.nextInt(2 * searchRadius) - searchRadius));
                //Check it's not an air block cause we don't want to talk hot air :D
                if (!miniPlayer.worldObj.isAirBlock(pos)) {
                    this.target = pos;

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return !this.miniPlayer.worldObj.isAirBlock(target) && super.continueExecuting();
    }

    @Override
    public void startExecuting() {
        Block targetBlock = this.miniPlayer.worldObj.getBlockState(target).getBlock();

        //Look for some valid chat lines to say about this block
        String blockName = targetBlock.getUnlocalizedName();
        for (int i = 0; i < 10; i++) {
            String chatLine = "chat.idle.block." + miniPlayer.getPersonality().getCurrentMood().name + "." + blockName + "." + i;
            if (I18n.hasKey(chatLine)) {
                chatLines = Iterators.forArray(I18n.format(chatLine).split(";"));
                break;
            }
        }
        //If we can't find anything, load default chat if there is a chance too
        if (chatLines == null && miniPlayer.getRNG().nextInt(30) == 0) {
            for (int i = 0; i < 10; i++) {
                String chatLine = "chat.idle.block.generic." + this.miniPlayer.getPersonality().getCurrentMood().name + "." + i;
                if (I18n.hasKey(chatLine)) {
                    chatLines = Iterators.forArray(I18n.format(chatLine).split(";"));
                    break;
                }
            }
        }
    }

    @Override
    public void updateTask() {
        //Look at the block just incase view has changed
        miniPlayer.getLookHelper().setLookPosition(target.getX(), target.getY(), target.getZ(), 10F, miniPlayer.getVerticalFaceSpeed());
        super.updateTask();
    }

    @Override
    public void resetTask() {
        target = null;
        super.resetTask();
    }
}
