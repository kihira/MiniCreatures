package kihira.minicreatures.common.customizer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.model.ModelBase;

import java.util.*;

public class CustomizerRegistry {

    private static final HashMap<String, ICustomizerPart> partList = new HashMap<String, ICustomizerPart>();
    private static final ListMultimap<EnumSoundCategory, String> soundsList = ArrayListMultimap.create();

    /**
     * An instance of ICustomizerPart should be passed to here
     * @param name A unique name for the part
     * @param part The part to register
     */
    public static void registerPart(String name, ICustomizerPart<? extends ModelBase> part) {
        if (name.contains(",")) throw new IllegalArgumentException("Part names cannot contain the character \",\"");
        if (!partList.containsKey(name)) {
            partList.put(name, part);
            MiniCreatures.logger.info("Registered the customizer part " + name);
        }
        else MiniCreatures.logger.error("A customizer with the name " + name + " is already registered!", new IllegalArgumentException());
    }

    /**
     * Registers a sound for the specified category. It can be used by all entities
     * TODO Make it so sounds are limited to specific entities?
     * @param name This must be the name that is registered with Minecraft
     * @param soundCategories A set of categories that the sound is valid for
     */
    public static void registerSound(String name, EnumSet<EnumSoundCategory> soundCategories) {
        for (EnumSoundCategory soundCategory : soundCategories) {
            if (!soundsList.get(soundCategory).contains(name)) soundsList.put(soundCategory, name);
            else {
                MiniCreatures.logger.error("A sound with the name " + name + " is already registered!", new IllegalArgumentException());
                break;
            }
        }
    }

    public static ArrayList<String> getValidParts(IMiniCreature miniCreature, EnumPartCategory partCategory) {
        ArrayList<String> validPartsList = new ArrayList<String>();
        for (Map.Entry<String, ICustomizerPart> part : partList.entrySet()) {
            if (part.getValue().isPartValidForEntity(miniCreature.getEntity(), partCategory)) validPartsList.add(part.getKey());
        }
        return validPartsList;
    }

    public static List<String> getSoundsForCategory(EnumSoundCategory soundCategory) {
        return soundsList.get(soundCategory);
    }

    public static ICustomizerPart getPart(String name) {
        return partList.get(name);
    }
}
