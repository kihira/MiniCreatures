package kihira.minicreatures.common.item;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class ItemMindControlHelmet extends Item {

    public ItemMindControlHelmet() {
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("mindControlHelmet");
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityMiniPlayer) {
                EntityMiniPlayer miniPlayer = (EntityMiniPlayer) entity;
                if (miniPlayer.getOwner() == player && !miniPlayer.isMindControlled()) {
                    miniPlayer.setMindControlled(true);
                    player.addChatComponentMessage(new ChatComponentText(miniPlayer.getCommandSenderName() + ": I will do anything you command of me Master..."));
                }
            }
            return true;
        }
        return false;
    }
}
