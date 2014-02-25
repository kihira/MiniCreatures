package kihira.minicreatures.common.entity;

import kihira.minicreatures.common.customizer.EnumPartCategory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;
import java.util.EnumSet;

public interface IMiniCreature {

    public IInventory getInventory();

    public EntityLiving getEntity();

    public EnumSet<EnumPartCategory> getPartCatergoies();

    public ArrayList<String> getCurrentParts();

    public void setParts(ArrayList<String> parts);
}
