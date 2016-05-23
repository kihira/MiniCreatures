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

package kihira.minicreatures.common.customizer;

import kihira.minicreatures.MiniCreatures;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomizerRegistry {

    private static final HashMap<String, ICustomizerPart> partList = new HashMap<>();

    /**
     * An instance of ICustomizerPart should be passed to here
     * @param name A unique name for the part
     * @param part The part to register
     */
    //TODO Don't pass an instance with ModelBase in. Create ICustomizerPartClient that extends ICustomizerPart
    public static void registerPart(String name, ICustomizerPart part) {
        if (name.contains(",")) throw new IllegalArgumentException("Part names cannot contain the character \",\"");
        if (!partList.containsKey(name)) {
            partList.put(name, part);
            MiniCreatures.logger.info("Registered the customizer part " + name);
        }
        else MiniCreatures.logger.error("A customizer with the name " + name + " is already registered!", new IllegalArgumentException());
    }

    public static ArrayList<String> getValidParts(Entity miniCreature, EnumPartCategory partCategory) {
        ArrayList<String> validPartsList = new ArrayList<>();
        for (Map.Entry<String, ICustomizerPart> part : partList.entrySet()) {
            if (part.getValue().isPartValidForEntity(miniCreature, partCategory)) validPartsList.add(part.getKey());
        }
        return validPartsList;
    }

    public static ICustomizerPart getPart(String name) {
        return partList.get(name);
    }
}
