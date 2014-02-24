package kihira.minicreatures.common;

import kihira.minicreatures.MiniCreatures;
import net.minecraft.client.model.ModelBase;

import java.util.ArrayList;

public class CustomizerRegistry {

    private static final ArrayList<ICustomizerPart<? extends ModelBase>> partList = new ArrayList<ICustomizerPart<? extends ModelBase>>();

    /**
     * An instance of ICustomizerPart should be passed to here
     * @param part The part to register
     */
    public static void registerPart(ICustomizerPart<? extends ModelBase> part) {
        if (!partList.contains(part)) {
            partList.add(part);
            MiniCreatures.logger.info("Registered the customizer part " + part.toString());
        }
        else MiniCreatures.logger.error("The customizer part " + part.toString() + " is already registered!", new IllegalArgumentException());
    }

    public static ArrayList<ICustomizerPart<? extends ModelBase>> getPartList() {
        return partList;
    }
}
