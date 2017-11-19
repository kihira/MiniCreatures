/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * The command for spawning entities that has support for mod added entities
 */
public class CommandSpawnEntity extends CommandBase {

    @Override
    public String getName() {
        return "spawnentity";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/spawnentity <entityname> [amount]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            String entityName = args[0];
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityName), sender.getEntityWorld());
            if (entity == null) throw new CommandException("Failed to create entity!");

            BlockPos senderPos = sender.getPosition();
            entity.setPosition(senderPos.getX(), senderPos.getY(), senderPos.getZ());
            if (args.length == 2) {
                for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                    sender.getEntityWorld().spawnEntity(entity);
                }
            }
            else sender.getEntityWorld().spawnEntity(entity);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        //Gather a list of the entity names
        return args.length > 0 ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList()) : Collections.emptyList();
    }
}
