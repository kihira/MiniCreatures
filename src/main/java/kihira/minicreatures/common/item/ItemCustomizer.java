package kihira.minicreatures.common.item;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCustomizer extends Item {

    public ItemCustomizer() {
        this.setUnlocalizedName("customizer");
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxStackSize(1);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof IMiniCreature) {
            player.openGui(MiniCreatures.instance, 2, player.worldObj, entity.getEntityId(), 0, 0);
            return true;
        }
        return false;
    }
}
