package kihira.minicreatures.common.customizer;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.model.ModelBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomizerRegistry {

    private static final HashMap<String, ICustomizerPart> partList = new HashMap<String, ICustomizerPart>();

    /**
     * An instance of ICustomizerPart should be passed to here
     * @param part The part to register
     */
    public static void registerPart(String name, ICustomizerPart<? extends ModelBase> part) {
        if (!partList.containsKey(name)) {
            partList.put(name, part);
            MiniCreatures.logger.info("Registered the customizer part " + name);
        }
        else MiniCreatures.logger.error("A customizer with the name " + name + " is already registered!", new IllegalArgumentException());
    }

    public static ArrayList<String> getValidParts(IMiniCreature miniCreature, EnumPartCategory partCategory) {
        ArrayList<String> validPartsList = new ArrayList<String>();
        for (Map.Entry<String, ICustomizerPart> part : partList.entrySet()) {
            if (part.getValue().isPartValidForEntity(miniCreature.getEntity(), partCategory)) validPartsList.add(part.getKey());
        }
        return validPartsList;
    }

    public static ICustomizerPart getPart(String name) {
        return partList.get(name);
    }
}
