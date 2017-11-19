package kihira.minicreatures.common.item;

import kihira.minicreatures.MiniCreatures;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBackpack extends Item {

    public ItemBackpack() {
        setRegistryName(MiniCreatures.MODID, "backpack");
        setUnlocalizedName("minicreatures.backpack");
        setCreativeTab(CreativeTabs.MISC);
        setMaxDamage(0);
        setMaxStackSize(1);
        setNoRepair();
    }
}
