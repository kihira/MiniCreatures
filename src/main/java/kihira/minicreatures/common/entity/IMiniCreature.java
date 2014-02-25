package kihira.minicreatures.common.entity;

import kihira.minicreatures.common.CustomizerRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;

import java.util.EnumSet;

public interface IMiniCreature {

    public IInventory getInventory();

    public EnumSet<CustomizerRegistry.EnumPartCategory> getPartCatergoies();

    public EntityLiving getEntity();
}
