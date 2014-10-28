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

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Iterator;

public class EntityAIChat extends EntityAIBase {

    protected final EntityMiniPlayer miniPlayer;

    protected Iterator<String> chatLines;
    protected int chatCooldown = 0;

    EntityAIChat(EntityMiniPlayer miniPlayer) {
        this.miniPlayer = miniPlayer;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        return this.miniPlayer.isTamed() && !this.miniPlayer.isSitting() && this.miniPlayer.getNavigator().noPath();
    }

    @Override
    public boolean continueExecuting() {
        return this.chatCooldown > 0 || (this.chatLines != null && this.chatLines.hasNext());
    }

    @Override
    public void updateTask() {
        //Get next chat line
        if (this.chatCooldown <= 0 && this.chatLines != null && this.chatLines.hasNext()) {
            String chat = this.chatLines.next();
            this.miniPlayer.setChat(chat);
            this.chatCooldown = chat.length() * 3;
        }

        //Reduces the time between chat
        this.chatCooldown--;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
        this.chatLines = null;
        this.chatCooldown = 0;
        this.miniPlayer.setChat("");
    }
}
