package kihira.minicreatures.common.entity;

import kihira.minicreatures.common.CustomizerRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;
import java.util.EnumSet;

public interface IMiniCreature {

    public IInventory getInventory();

    public EntityLiving getEntity();

    public EnumSet<CustomizerRegistry.EnumPartCategory> getPartCatergoies();

    public ArrayList<String> getCurrentParts();
}
